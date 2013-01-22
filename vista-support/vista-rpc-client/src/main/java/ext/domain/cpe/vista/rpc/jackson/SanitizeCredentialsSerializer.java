package EXT.DOMAIN.cpe.vista.rpc.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import EXT.DOMAIN.cpe.vista.rpc.conn.AccessVerifyConnectionSpec;
import org.springframework.util.DigestUtils;

import java.io.IOException;

public class SanitizeCredentialsSerializer extends StdScalarSerializer<String> {

    public SanitizeCredentialsSerializer() {
        super(String.class);
    }

    /**
     * For Strings, both null and Empty String qualify for emptiness.
     */
    @Override
    public boolean isEmpty(String value) {
        return (value == null) || (value.length() == 0);
    }

    @Override
    public void serialize(String value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        AccessVerifyConnectionSpec av = AccessVerifyConnectionSpec.create(value);
        jgen.writeString(DigestUtils.md5DigestAsHex(av.getCredentials().getBytes("UTF-8")));
    }
}
