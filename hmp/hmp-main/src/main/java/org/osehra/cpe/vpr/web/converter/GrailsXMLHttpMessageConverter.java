package org.osehra.cpe.vpr.web.converter;

import grails.converters.XML;
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class GrailsXMLHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName(XML.DEFAULT_REQUEST_ENCODING);

    private XML xmlConverter = new XML();

    GrailsXMLHttpMessageConverter() {
        super(new MediaType("application", "xml", DEFAULT_CHARSET));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal(Object t, HttpOutputMessage outputMessage) {
        try {
            xmlConverter.setTarget(t);
            xmlConverter.render(new OutputStreamWriter(outputMessage.getBody()));
        } catch (ConverterException e) {
            throw new HttpMessageNotWritableException("Could not write XML: ", e);
        } catch (IOException e) {
            throw new HttpMessageNotWritableException("Could not write XML: ", e);
        }
    }

}

