import br.com.diegosilva.rfnative.RfNative;

public class MainTest {
    public static void main(String ... args){
        System.out.println(System.getProperty("java.library.path"));
        System.out.println(new RfNative().getString());
    }
}
