package xyz.lazyghosty.phant0m.api;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import moe.shizuku.server.IRemoteProcess;

public class Phant0mNewProcess extends Process implements Parcelable {

    public static final Creator<Phant0mNewProcess> CREATOR = new Creator<Phant0mNewProcess>() {
        @Override
        public Phant0mNewProcess createFromParcel(Parcel in) {
            return new Phant0mNewProcess(in);
        }

        @Override
        public Phant0mNewProcess[] newArray(int size) {
            return new Phant0mNewProcess[size];
        }
    };
    private static final Set<Phant0mNewProcess> CACHE = Collections.synchronizedSet(new ArraySet<>());
    private static final String TAG = "Phant0mNewProcess";
    private IRemoteProcess remote;
    private OutputStream os;
    private InputStream is;

    public Phant0mNewProcess(IRemoteProcess remote) {
        this.remote = remote;
        try {
            this.remote.asBinder().linkToDeath(() -> {
                this.remote = null;
                Log.v(TAG, "remote process is dead");

                CACHE.remove(Phant0mNewProcess.this);
            }, 0);
        } catch (RemoteException e) {
            Log.e(TAG, "linkToDeath", e);
        }

        // The reference to the binder object must be hold
        CACHE.add(this);
    }

    protected Phant0mNewProcess(Parcel in) {
        remote = IRemoteProcess.Stub.asInterface(in.readStrongBinder());
    }

    @Override
    public OutputStream getOutputStream() {
        if (os == null) {
            try {
                os = new ParcelFileDescriptor.AutoCloseOutputStream(remote.getOutputStream());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return os;
    }

    @Override
    public InputStream getInputStream() {
        if (is == null) {
            try {
                is = new ParcelFileDescriptor.AutoCloseInputStream(remote.getInputStream());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return is;
    }

    @Override
    public InputStream getErrorStream() {
        try {
            return new ParcelFileDescriptor.AutoCloseInputStream(remote.getErrorStream());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int waitFor() throws InterruptedException {
        try {
            return remote.waitFor();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int exitValue() {
        try {
            return remote.exitValue();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            remote.destroy();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeStrongBinder(remote.asBinder());
    }
}
