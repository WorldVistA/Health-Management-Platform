package EXT.DOMAIN.cpe.vista.springframework.security.userdetails.rpc;

import EXT.DOMAIN.cpe.vista.rpc.ConnectionCallback;
import EXT.DOMAIN.cpe.vista.rpc.RpcOperations;
import EXT.DOMAIN.cpe.vista.rpc.broker.protocol.BadCredentialsException;
import EXT.DOMAIN.cpe.vista.rpc.broker.protocol.VerifyCodeExpiredException;
import EXT.DOMAIN.cpe.vista.util.RpcUriUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.authentication.CredentialsExpiredException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

public class TestRpcTemplateUserDetailService {

    public static final String MOCK_VISTA_ID = "9F2B";
    public static final String MOCK_DIVISION = "960";
    public static final String MOCK_ACCESS_CODE = "10vehu";
    public static final String MOCK_VERIFY_CODE = "vehu10";

    private RpcOperations mockRpcTemplate;
    private RpcTemplateUserDetailService s;

    @Before
    public void setUp() {
        mockRpcTemplate = EasyMock.createMock(RpcOperations.class);

        s = new RpcTemplateUserDetailService();
        s.setRpcTemplate(mockRpcTemplate);
    }

    @Test
    public void testBadCredentials() {
        expect(mockRpcTemplate.execute(anyObject(ConnectionCallback.class), eq("vrpcb://" + RpcUriUtils.toAuthority(MOCK_VISTA_ID, MOCK_DIVISION, MOCK_ACCESS_CODE, MOCK_VERIFY_CODE)))).andThrow(new PermissionDeniedDataAccessException("", new BadCredentialsException()));
        replay(mockRpcTemplate);

        try {
            s.login(MOCK_VISTA_ID, MOCK_DIVISION, MOCK_ACCESS_CODE, MOCK_VERIFY_CODE, null, null, "127.0.0.1");
            fail("expected a Spring Security BadCredentialsException");
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            // NOOP
        }

        verify(mockRpcTemplate);
    }

    @Test
    public void testVerifyCodeExpired() {
        expect(mockRpcTemplate.execute(anyObject(ConnectionCallback.class), eq("vrpcb://" + RpcUriUtils.toAuthority(MOCK_VISTA_ID, MOCK_DIVISION, MOCK_ACCESS_CODE, MOCK_VERIFY_CODE)))).andThrow(new PermissionDeniedDataAccessException("",new VerifyCodeExpiredException()));
        replay(mockRpcTemplate);

        try {
            s.login(MOCK_VISTA_ID, MOCK_DIVISION, MOCK_ACCESS_CODE, MOCK_VERIFY_CODE, null, null, "127.0.0.1");
            fail("expected a Spring Security BadCredentialsException");
        } catch (CredentialsExpiredException e) {
            // NOOP
        }

        verify(mockRpcTemplate);
    }
}
