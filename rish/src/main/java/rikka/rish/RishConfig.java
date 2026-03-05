package rikka.rish;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import frb.phant0m.server.IPhant0mService;

public class RishConfig {

    static final int TRANSACTION_createHost = 0;
    static final int TRANSACTION_setWindowSize = 1;
    static final int TRANSACTION_getExitCode = 2;
    private static final String TAG = "RISHConfig";
    private static IPhant0mService phant0mService;
    private static String interfaceToken;
    private static int transactionCodeStart;
    private static String libraryPath;

    static IBinder getBinder() {
        return phant0mService.asBinder();
    }

    static IPhant0mService getPhant0mService() {
        return phant0mService;
    }

    static RemoteProcess newProcess(String[] cmd) throws RemoteException {
        return new RemoteProcess(getPhant0mService().newProcess(cmd, getPhant0mService().getEnvironment(0).getEnv(), null));
    }

    static String getInterfaceToken() {
        return interfaceToken;
    }

    static int getTransactionCode(int code) {
        return transactionCodeStart + code;
    }

    public static void setLibraryPath(String path) {
        libraryPath = path;
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static void loadLibrary() {
        if (libraryPath == null) {
            System.loadLibrary("rish");
        } else {
            System.load(libraryPath + "/librish.so");
        }
    }

    public static void init(String interfaceToken, int transactionCodeStart) {
        Log.d(TAG, "init (server) " + interfaceToken + " " + transactionCodeStart);
        RishConfig.interfaceToken = interfaceToken;
        RishConfig.transactionCodeStart = transactionCodeStart;
        loadLibrary();
    }

    public static void init(IBinder binder, String interfaceToken, int transactionCodeStart) {
        Log.d(TAG, "init (client) " + binder + " " + interfaceToken + " " + transactionCodeStart);
        RishConfig.phant0mService = IPhant0mService.Stub.asInterface(binder);
        RishConfig.interfaceToken = interfaceToken;
        RishConfig.transactionCodeStart = transactionCodeStart;
        loadLibrary();
    }
}
