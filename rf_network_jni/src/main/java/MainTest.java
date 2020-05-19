import br.com.diegosilva.rfnative.RfNative;

public class MainTest {
    public static void main(String ... args){
        System.setProperty("java.library.path",  MainTest.class.getResource("rf_native.cpp").getPath().replace("rf_native.cpp", ""));
        System.out.println(System.getProperty("java.library.path"));
        new RfNative().start(00);
    }
}
