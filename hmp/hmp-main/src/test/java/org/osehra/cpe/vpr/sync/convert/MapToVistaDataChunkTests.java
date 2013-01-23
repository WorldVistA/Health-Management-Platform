package org.osehra.cpe.vpr.sync.convert;

import org.osehra.cpe.vista.rpc.RpcRequest;
import org.osehra.cpe.vista.util.RpcUriUtils;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.sync.SyncMessageConstants;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MapToVistaDataChunkTests {
    private Patient patient;
    private IPatientDAO patientDao;
    private MapToVistaDataChunk c;
	private Map<String, Object> m;
    
    @Before
    public void setUp() {
        patient = new Patient();
        patient.setData("pid", "42");
        patient.setData("icn", "12345");

        patientDao = mock(IPatientDAO.class);
        
        c = new MapToVistaDataChunk();
        c.setPatientDao(patientDao);
        m = new HashMap<String, Object>();
        m.put(SyncMessageConstants.VISTA_ID, "ABCDEF");
        m.put(SyncMessageConstants.PATIENT_DFN, "229");
        m.put(SyncMessageConstants.PATIENT_ID, "42");
        m.put(SyncMessageConstants.RPC_URI, RpcUriUtils.toURI(new RpcRequest("FOO/BAR", "arg1", "arg2")).toString());
        m.put(SyncMessageConstants.RPC_ITEM_INDEX, 7);
        m.put(SyncMessageConstants.RPC_ITEM_COUNT, 9);
        m.put("foo", "bar");
        m.put("baz", "spaz");
        m.put(SyncMessageConstants.VPR_DOMAIN, "foo");

        when(patientDao.findByVprPid(anyString())).thenReturn(patient);
    }

    @Test
    public void testConvertJson() throws SAXException, IOException {
        String jStr = "{\"foo\":\"bar\"}";
		m.put(SyncMessageConstants.RPC_ITEM_CONTENT, jStr );

        VistaDataChunk chunk = c.convert(m);
        assertNotNull(chunk);
        assertThat(chunk.getSystemId(), equalTo(m.get(SyncMessageConstants.VISTA_ID)));
        assertThat(chunk.getPatient().getPid(), equalTo("42"));
        assertThat(chunk.getPatientId(), equalTo("42"));
        assertThat(chunk.getRpcUri(), equalTo(m.get(SyncMessageConstants.RPC_URI)));
        assertThat(chunk.getItemIndex(), equalTo((Integer) m.get(SyncMessageConstants.RPC_ITEM_INDEX)));
        assertThat(chunk.getItemCount(), equalTo((Integer) m.get(SyncMessageConstants.RPC_ITEM_COUNT)));
        assertThat(chunk.getDomain(), equalTo(m.get(SyncMessageConstants.VPR_DOMAIN)));

        Map<String, String> map = new HashMap<String, String>();
        map.put("foo", "bar");
        map.put("baz", "spaz");
        assertTrue(chunk.getParams().equals(map));
        assertThat(chunk.getContent(),is(jStr));
    }
    
}
