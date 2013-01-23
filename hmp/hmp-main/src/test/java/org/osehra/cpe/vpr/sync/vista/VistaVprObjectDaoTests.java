package org.osehra.cpe.vpr.sync.vista;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.osehra.cpe.test.mockito.ReturnsArgument;
import org.osehra.cpe.vista.rpc.RpcOperations;
import org.osehra.cpe.vpr.pom.IGenericPOMObjectDAO;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.osehra.cpe.vpr.UserInterfaceRpcConstants.VPR_PUT_OBJECT_RPC_URI;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class VistaVprObjectDaoTests {

    private VistaVprObjectDao dao;
    private RpcOperations mockRpcTemplate;
    private IGenericPOMObjectDAO mockJdsDao;

    @Before
    public void setUp() throws Exception {
        mockRpcTemplate = mock(RpcOperations.class);
        mockJdsDao = mock(IGenericPOMObjectDAO.class);

        dao = new VistaVprObjectDao();
        dao.setRpcTemplate(mockRpcTemplate);
        dao.setJdsDao(mockJdsDao);

        when(mockJdsDao.save(any(Foo.class))).then(new ReturnsArgument(0));
    }

    @Test
    public void testSaveMapReturnEntity() throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("bar", "spaz");
        data.put("baz", true);
        String requestJsonString = POMUtils.toJSON(data);

        JsonNode returnJson = createReturnJson("urn:va:foo:1");
        when(mockRpcTemplate.executeForJson(VPR_PUT_OBJECT_RPC_URI, "foo", requestJsonString)).thenReturn(returnJson);

        Foo foo = dao.save(Foo.class, data);

        assertThat(foo.getUid(), is("urn:va:foo:1"));
        assertThat(foo.getBar(), is("spaz"));
        assertThat(foo.isBaz(), is(true));

        verify(mockRpcTemplate).executeForJson(VPR_PUT_OBJECT_RPC_URI, "foo", requestJsonString);
        verify(mockJdsDao).save(foo);
    }

    @Test
    public void testSaveEntity() throws Exception {
        Foo foo = new Foo("spaz", true);
        String requestJsonString = POMUtils.toJSON(foo);

        JsonNode returnJson = createReturnJson("urn:va:foo:1");
        when(mockRpcTemplate.executeForJson(VPR_PUT_OBJECT_RPC_URI, "foo", requestJsonString)).thenReturn(returnJson);

        foo = dao.save(foo);

        assertThat(foo.getUid(), is("urn:va:foo:1"));
        assertThat(foo.getBar(), is("spaz"));
        assertThat(foo.isBaz(), is(true));

        verify(mockRpcTemplate).executeForJson(VPR_PUT_OBJECT_RPC_URI, "foo", requestJsonString);
        verify(mockJdsDao).save(foo);
    }

    private JsonNode createReturnJson(String uid) {
        ObjectNode returnJson = JsonNodeFactory.instance.objectNode();
        returnJson.put("apiVersion", "1.01");
        returnJson.put("success", true);
        ObjectNode dataNode = returnJson.putObject("data");
        dataNode.put("uid", uid);
        return returnJson;
    }
}
