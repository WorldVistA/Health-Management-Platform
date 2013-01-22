package EXT.DOMAIN.cpe.vpr.sync.convert;

import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

import static EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants.*;

public class VistaDataChunkToMap implements Converter<VistaDataChunk, Map<String, Object>> {

    public Map<String, Object> convert(VistaDataChunk fragment) {
        Map<String, Object> m = new HashMap<String, Object>();

        m.put(VISTA_ID, fragment.getSystemId());
        m.put(PATIENT_DFN, fragment.getLocalPatientId());

        m.putAll(fragment.getParams());

        Patient pt = fragment.getPatient();
        if (pt != null && pt.getPid() != null) m.put(PATIENT_ID, pt.getPid());
        if (pt != null && pt.getIcn() != null) m.put(PATIENT_ICN, pt.getIcn());

        m.put(VPR_DOMAIN, fragment.getDomain());

        m.put(RPC_URI, fragment.getRpcUri());
        m.put(RPC_ITEM_INDEX, fragment.getItemIndex());
        m.put(RPC_ITEM_COUNT, fragment.getItemCount());
        m.put(RPC_ITEM_CONTENT, fragment.getContent());

        return m;
    }
}
