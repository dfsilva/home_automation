import br.com.diegosilva.rfnative.RfNative;

public class MainTest {
    public static void main(String ... args){
        System.out.println(System.getProperty("java.library.path"));
        new RfNative().start(00);
    }
}
