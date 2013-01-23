package org.osehra.cpe.vpr.pom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.osehra.cpe.datetime.jackson.HealthTimeModule;
import org.osehra.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import org.osehra.cpe.vpr.ws.json.GrantedAuthorityJacksonAnnotations;
import org.osehra.cpe.vpr.ws.json.UserDetailsJacksonAnnotations;
import org.osehra.cpe.vpr.ws.json.VistaUserDetailsJacksonAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

/**
 * ObjectMapper configured with POM and VPR defaults.
 */
public class POMObjectMapper extends ObjectMapper {

    private Class<?> serializationView;

    public POMObjectMapper() {
        super();
        this.registerModule(new HealthTimeModule());
//        this.registerModule(new ViewDefDefModule());

        // the following config could be in its own Jackson Module ("VPRModule") if this list gets unwieldy or we want to
        // externalize the VPR's JSON serialization configuration more or not have dependencies on 3rd party libs.  No need right now, though.
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.addMixInAnnotations(VistaUserDetails.class, VistaUserDetailsJacksonAnnotations.class);
        this.addMixInAnnotations(UserDetails.class, UserDetailsJacksonAnnotations.class);
        this.addMixInAnnotations(GrantedAuthority.class, GrantedAuthorityJacksonAnnotations.class);
    }

    /**
     * Constructs an ObjectMapper instance that will serialize objects using specified JSON View (filter).
     *
     * @see "http://wiki.fasterxml.com/JacksonJsonViews"
     */
    public POMObjectMapper(Class<?> serializationView) {
        this();
        this.serializationView = serializationView;
    }

    @Override
    public void writeValue(JsonGenerator jgen, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        if (serializationView != null) {
            writerWithView(serializationView).writeValue(jgen, value);
        } else {
            super.writeValue(jgen, value);
        }
    }
}
