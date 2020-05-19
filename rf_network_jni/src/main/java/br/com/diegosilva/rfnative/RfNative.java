package br.com.diegosilva.rfnative;

public class RfNative {
    final private static String LIB_NAME = "rfnative.so";

    static {
        System.loadLibrary(LIB_NAME);
    }


    public native String getString();
}