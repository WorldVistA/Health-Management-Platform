package EXT.DOMAIN.cpe.vpr.web.converter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Extends the Spring implementation of this class to correct for image/x-png content-type and to set ETag headers
 */
public class ResourceHttpMessageConverter extends org.springframework.http.converter.ResourceHttpMessageConverter {

    private ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();

    public ResourceHttpMessageConverter() {
        fileTypeMap.setMappingLocation(new ClassPathResource("ext/domain/cpe/vpr/web/converter/mime.types"));
        fileTypeMap.afterPropertiesSet();
    }

    @Override
    protected MediaType getDefaultContentType(Resource resource) {
        String mediaType = fileTypeMap.getContentType(resource.getFilename());
        return (StringUtils.hasText(mediaType) ? MediaType.parseMediaType(mediaType) : MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    protected void writeInternal(Resource resource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // attempt to set Last-Modified header
        try {
            outputMessage.getHeaders().setLastModified(resource.lastModified());
        } catch (IOException e) {
            // NOOP (ignore if this resource doesn't support lastModifed)
        }

        super.writeInternal(resource, outputMessage);
    }
}
