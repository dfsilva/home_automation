package br.com.diegosilva.rfnative;

public class RfNative {
    final private static String LIB_NAME = "rfnative";

    static {
        System.loadLibrary(LIB_NAME);
    }

    public native void start();

    public native boolean send(String msg);

    public void onReceive(String msg){
        System.out.println(msg);
    }
}