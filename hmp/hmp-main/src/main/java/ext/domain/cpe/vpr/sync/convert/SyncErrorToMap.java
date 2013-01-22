package EXT.DOMAIN.cpe.vpr.sync.convert;

import EXT.DOMAIN.cpe.vpr.SyncError;
import EXT.DOMAIN.cpe.vpr.sync.SyncMessageConstants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SyncErrorToMap implements Converter<SyncError, Map> {
    @Override
    public Map convert(SyncError error) {
        Map msg = new HashMap();
        msg.put(SyncMessageConstants.EXCEPTION_NAME, error.getItem());
        msg.put(SyncMessageConstants.EXCEPTION_MESSAGE, error.getMessage());
        msg.put(SyncMessageConstants.EXCEPTION_STACK_TRACE, error.getStackTrace());
        if (StringUtils.hasText(error.getPid())) msg.put(SyncMessageConstants.PATIENT_ID, error.getPid());
        return msg;
    }
}
