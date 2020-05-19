package br.com.diegosilva.rfnative;

public class RfNative {
    final private static String LIB_NAME = "rfnative";

    static {
        System.loadLibrary(LIB_NAME);
    }

    public native void start(int node);

    public native boolean send(int node, String msg);

    public void onReceive(String msg){
        System.out.println(msg);
    }
}