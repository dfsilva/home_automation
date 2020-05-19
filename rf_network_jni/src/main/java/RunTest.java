import br.com.diegosilva.rfnative.RfNative;

public class RunTest {
    public static void main(String ... args){

        new RfNative((instance, msg) -> {
            System.out.println(msg);
            System.out.println("Enviou: "+instance.send(011, msg));
        }).start(00);
        
    }
}
