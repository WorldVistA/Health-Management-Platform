package org.osehra.cpe.vpr.sync.convert;

import org.osehra.cpe.vpr.SyncError;
import org.osehra.cpe.vpr.sync.SyncMessageConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.io.IOException;
import java.util.Date;

import static org.osehra.cpe.vpr.sync.SyncMessageConstants.*;

/**
 * Converts a JMS {@link MapMessage} to a {@link SyncError}
 */
public class MapMessageToSyncError implements Converter<MapMessage, SyncError> {
    @Override
    public SyncError convert(MapMessage msg) {
        try {
            SyncError error = new SyncError();
            error.setId(msg.getJMSMessageID());
            error.setDateCreated(new Date(msg.getJMSTimestamp()));
            error.setPid(msg.getString(PATIENT_ID));
            error.setItem(getItem(msg));
            error.setMessage(msg.getString(EXCEPTION_MESSAGE));
            error.setStackTrace(msg.getString(EXCEPTION_STACK_TRACE));
            String json = msg.getString(RPC_ITEM_CONTENT);
            if (json != null) {
                ObjectMapper jsonMapper = new ObjectMapper();
                json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMapper.readTree(json));
            }
            error.setJson(json);
            return error;
        } catch (Exception e) {
            throw new IllegalArgumentException("unable to convert MapMessage", e);
        }
    }

    private String getItem(MapMessage msg) throws JMSException {
        if (msg.itemExists(VPR_DOMAIN)) {
             return String.format("'%s' chunk %d of %d returned from %s", msg.getString(VPR_DOMAIN), msg.getInt(RPC_ITEM_INDEX) + 1, msg.getInt(RPC_ITEM_COUNT), msg.getString(RPC_URI));
        } else {
            return msg.getString(EXCEPTION_NAME);
        }
    }
}
