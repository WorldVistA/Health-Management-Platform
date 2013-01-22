import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import EXT.DOMAIN.cpe.vista.rpc.RpcTemplate;

import java.io.IOException;

/**
 * Scratch program for whatever.
 */
public class Foo {
    public static void main(String[] args) throws IOException {
        RpcTemplate t = new RpcTemplate();
        String response = t.executeForString("vrpcb://10vehu;vehu10@localhost:29060/VPR UI CONTEXT/VPR GET SOURCE", "Ward", "");
        System.out.println(response);
    }
}
