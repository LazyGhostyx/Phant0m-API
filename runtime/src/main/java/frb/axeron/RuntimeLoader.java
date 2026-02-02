package frb.axeron;

import static frb.axeron.shared.AxeronApiConstant.server.TYPE_ENV;

import android.app.IActivityManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.system.Os;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import frb.axeron.server.IAxeronService;
import frb.axeron.server.ServerConstants;
import rikka.hidden.compat.PackageManagerApis;

public class RuntimeLoader {

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static final AtomicReference<IAxeronService> axeronService = new AtomicReference<>();
    private static final AtomicReference<String> sourceDir = new AtomicReference<>();
    private static final Binder replyBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, Parcel reply, int flags) {
            if (code == 1) {
                IBinder binder = data.readStrongBinder();

                String source = data.readString();
                if (binder != null) {
                    axeronService.set(IAxeronService.Stub.asInterface(binder));
                    sourceDir.set(source);
                    latch.countDown();
                } else {
                    abort("Server is not running");
                }
                return true;
            }
            return false;
        }
    };
    private static final int NOT_EXITED = Integer.MIN_VALUE;
    private static String[] args;
    private static String callingPackage = null;
    private final AtomicInteger exitCode = new AtomicInteger(NOT_EXITED);

    private static void requestBinder() throws Exception {
        Bundle bundle = new Bundle();
        bundle.putBinder("binder", replyBinder);

        Intent intent = new Intent(ServerConstants.REQUEST_BINDER_AXERISH)
                .setPackage("frb.axeron.manager")
                .setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .putExtra("data", bundle);

        IBinder amBinder = ServiceManager.getService("activity");
        IActivityManager am = IActivityManager.Stub.asInterface(amBinder);

        try {
            am.broadcastIntent(null, intent, null, null, 0, null, null,
                    null, -1, null, true, false, 0);
        } catch (Throwable e) {
            if ((Build.VERSION.SDK_INT != Build.VERSION_CODES.O && Build.VERSION.SDK_INT != Build.VERSION_CODES.O_MR1)
                    || !Objects.equals(e.getMessage(), "Calling application did not provide package name")) {
                throw e;
            }

            System.err.println("broadcastIntent fails on Android 8.0 or 8.1, fallback to startActivity");
            System.err.flush();

            Intent activityIntent = new Intent(ServerConstants.REQUEST_BINDER_AXERISH)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                    .putExtra("data", bundle);

            am.startActivityAsUser(null, callingPackage, activityIntent, null, null, null, 0, 0, null, null, Os.getuid() / 100000);
        }
    }

    public static void main(String[] args) {
        RuntimeLoader.args = args;
        String packageName;
        var pkg = PackageManagerApis.getPackagesForUidNoThrow(Os.getuid());
        if (pkg.size() == 1) {
            packageName = pkg.get(0);
        } else {
            packageName = System.getenv("APPLICATION_ID");
            if (TextUtils.isEmpty(packageName) || "PKG".equals(packageName)) {
                abort("APPLICATION_ID is not set, set this environment variable to the id of current application (package name)");
            }
        }

        RuntimeLoader.callingPackage = packageName;
        try {
            requestBinder();

            if (!latch.await(5, TimeUnit.SECONDS)) {
                System.err.println("Timeout: no response from server");
                System.exit(1);
            }

            new RuntimeLoader().start();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            abort("Error: " + t.getMessage());
        }
    }

    private static void abort(String message) {
        latch.countDown();
        System.err.println(message);
        System.err.flush();
        System.exit(1);
    }

    private void startShell() {
        String[] argv;
        try {

            if (args == null || args.length == 0) {
                argv = new String[]{"/system/bin/sh"};
            } else {
                String[] newArgv = new String[args.length + 1];
                newArgv[0] = "/system/bin/sh";
                System.arraycopy(args, 0, newArgv, 1, args.length);
                argv = newArgv;
            }

            RemoteProcess process = new RemoteProcess(axeronService.get().newProcess(argv, axeronService.get().getEnvironment(TYPE_ENV).getEnv(), null));

            new Thread(() -> {
                try (
                        InputStream in = process.getInputStream();
                        OutputStream out = System.out
                ) {
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = in.read(buf)) != -1) {
                        out.write(buf, 0, n);
                        out.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }, "runtime-stdout").start();

            new Thread(() -> {
                try (
                        InputStream in = process.getErrorStream();
                        OutputStream out = System.err
                ) {
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = in.read(buf)) != -1) {
                        out.write(buf, 0, n);
                        out.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }, "runtime-stderr").start();

            new Thread(() -> {
                try {
                    int code = process.waitFor();
                    exitCode.set(code);

                    try {
                        process.getOutputStream().close();
                    } catch (Exception ignored) {
                    }
                    System.exit(code);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }, "runtime-waiter").start();


            InputStream in = System.in;
            OutputStream out = process.getOutputStream();
            byte[] buf = new byte[4096];

            while (exitCode.get() == NOT_EXITED) {
                int n = in.read(buf);
                if (n == -1) break;

                out.write(buf, 0, n);
                out.flush();
            }
            System.exit(exitCode.get());
        } catch (Throwable e) {
            abort(e.getMessage());
        }
    }

    public void start() throws RemoteException {
//        if (axeronService.get().checkSelfPermission()) {
//            axeronService.get().requestPermission(0);
//            abort("Permission requested. Please re-run the command.");
//            return;
//        }
        startShell();
    }
}
