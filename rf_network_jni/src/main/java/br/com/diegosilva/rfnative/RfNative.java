package br.com.diegosilva.rfnative;

import java.io.IOException;

public class RfNative {

    public interface ReceiListener {
        void onReceive(RfNative instance, String msg);
    }

    static {
        try {
            NativeUtils.loadLibraryFromJar("/librfnative.so");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ReceiListener receiListener;

    public RfNative() {
    }

    public RfNative(ReceiListener receiListener) {
        this.receiListener = receiListener;
    }

    public void setReceiListener(ReceiListener receiListener) {
        this.receiListener = receiListener;
    }

    public native void start(int node);

    public native boolean send(int node, String msg);

    public void onReceive(String msg) {
        if (receiListener != null)
            receiListener.onReceive(this, msg);
    }

}