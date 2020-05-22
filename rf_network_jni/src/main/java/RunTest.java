import br.com.diegosilva.rfnative.RfNative;

public class RunTest {
    public static void main(String ... args){
        new RfNative((instance, msg) -> {
            System.out.println(msg);
            System.out.println("Enviou 01: "+instance.send(01, msg));
            System.out.println("Enviou 02: "+instance.send(02, msg));
        }).start(00);
    }
}
