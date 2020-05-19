package br.com.diegosilva.rfnative;

import java.io.IOException;

public class RfNative {
    static {
        try {
            NativeUtils.loadLibraryFromJar("/librfnative.so");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public native void start(int node);

    public native boolean send(int node, String msg);

    public void onReceive(String msg) {
        System.out.println(msg);
        System.out.println("Enviou: "+send(01, msg));
    }

}