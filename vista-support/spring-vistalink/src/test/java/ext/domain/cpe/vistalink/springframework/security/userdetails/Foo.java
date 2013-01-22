package EXT.DOMAIN.cpe.vistalink.springframework.security.userdetails;

import EXT.DOMAIN.cpe.vistalink.ConnectionFactoryLocator;
import EXT.DOMAIN.cpe.vistalink.VistaLinkTemplate;
import EXT.DOMAIN.vistalink.adapter.cci.VistaLinkConnection;
import EXT.DOMAIN.vistalink.adapter.spi.EMAdapterEnvironment;
import EXT.DOMAIN.vistalink.adapter.spi.VistaLinkManagedConnectionFactory;
import EXT.DOMAIN.vistalink.rpc.RpcRequest;
import EXT.DOMAIN.vistalink.rpc.RpcRequestFactory;
import EXT.DOMAIN.vistalink.rpc.RpcResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jca.cci.core.CciTemplate;
import org.springframework.jca.cci.core.ConnectionCallback;
import org.springframework.jca.support.LocalConnectionFactoryBean;
import org.springframework.util.Assert;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Foo {
    private static final String RPC_CONTEXT = "XUS KAAJEE WEB LOGON";
    private static final String GET_USER_INFO_RPC = "XUS KAAJEE GET USER INFO";
    private static final String LOGOUT_RPC_NAME = "XUS KAAJEE LOGOUT";

    public static void main(String[] args) throws Exception {
        VistaLinkManagedConnectionFactory mcf = new VistaLinkManagedConnectionFactory() {
            protected String getPrimaryStation() {
                return "442";
            }
        };
        mcf.setNonManagedHostIPAddress("vhaislbll2.vha.DOMAIN.EXT");
        mcf.setNonManagedHostPort(8014);
        mcf.setNonManagedAccessCode("7eGAcrar");
        mcf.setNonManagedVerifyCode("wU67YUhe.");
        mcf.setAdapterEnvironment(EMAdapterEnvironment.J2EE);

        LocalConnectionFactoryBean cfb = new LocalConnectionFactoryBean();
        cfb.setManagedConnectionFactory(mcf);
        cfb.afterPropertiesSet();

        final ConnectionFactory cf = (ConnectionFactory) cfb.getObject();

        // login
        CciTemplate t = new CciTemplate(cf, new VistaLinkAccessVerifyConnectionSpec("442", "10VEHU", "VEHU20", InetAddress.getLocalHost().getHostAddress()));
        RpcResponse response = (RpcResponse) t.execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection, ConnectionFactory connectionFactory) throws ResourceException, SQLException, DataAccessException {
                Assert.isInstanceOf(VistaLinkConnection.class, connection);
                VistaLinkConnection conn = (VistaLinkConnection) connection;

                try {
                    RpcRequest vReq = RpcRequestFactory.getRpcRequest();
                    vReq.setRpcContext(RPC_CONTEXT);
                    vReq.setRpcClientTimeOut(600);
                    vReq.setUseProprietaryMessageFormat(true);
                    vReq.setRpcName(GET_USER_INFO_RPC);
                    vReq.getParams().setParam(1, "string", InetAddress.getLocalHost().getHostAddress());
                    vReq.getParams().setParam(2, "string,", "EDIS Tracking Board");
                    return conn.executeRPC(vReq);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    return null;
                }
            }
        });
        /*
           * ; Result(0) is the users DUZ.
           * ; Result(1) is the user name from the .01 field.
           * ; Result(2) is the users full name from the name standard file.
           * ; Result(3) is the FAMILY (LAST) NAME (or ^ if null)
           * ; Result(4) is the GIVEN (FIRST) NAME (or ^ if null)
           * ; Result(5) is the MIDDLE NAME (or ^ if null)
           * ; Result(6) is the PREFIX (or ^ if null)
           * ; Result(7) is the SUFFIX (or ^ if null)
           * ; Result(8) is the DEGREE (or ^ if null)
           * ; Result(9) is station # of the division that the user is working in.
           * ; Result(10) is the station # of the parent facility for the login division
           * ; Result(11) is the station # of the computer system "parent" from the KSP file.
           * ; Result(12) is the IEN of the signon log entry
           * ; Result(13) = # of permissible divisions
           * ; Result(14-n) are the permissible divisions for user login, in the format:
           * ;           IEN of file 4^Station Name^Station Number^default? (1 or 0)
           */
        System.out.println("<-->");
        System.out.println(response.getRawResponse());
        System.out.println("<-->");
        String result = response.getResults();
        String[] results = result.split("\n");
        System.out.println(results[0] + ":" + results[9] + ":" + results[12]);

        // call an RPC
        VistaLinkTemplate t2 = new VistaLinkTemplate(new ConnectionFactoryLocator() {
            public ConnectionFactory getConnectionFactory(String stationNumber) throws DataAccessResourceFailureException {
                return cf;
            }
        });
//        System.out.println(t2.executeRpc("442", "20012", "", ""));

        // logout
        t2.setTimeOut(600);
        List params = new ArrayList();
        params.add(results[12]);
        t2.rpcAsUser("442", "20012", RPC_CONTEXT, LOGOUT_RPC_NAME, params);
    }
}
