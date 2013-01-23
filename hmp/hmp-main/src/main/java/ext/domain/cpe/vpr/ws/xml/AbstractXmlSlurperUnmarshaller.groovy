package org.osehra.cpe.vpr.ws.xml

import org.springframework.oxm.Unmarshaller
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.sax.SAXSource

import org.xml.sax.XMLReader
import org.xml.sax.InputSource

import org.springframework.oxm.XmlMappingException
import org.springframework.oxm.UnmarshallingFailureException

import org.xml.sax.SAXException
import groovy.util.slurpersupport.GPathResult

/**
 * TODOC: Provide summary documentation of class AbstractXmlSlurperUnmarshaller
 */
abstract class AbstractXmlSlurperUnmarshaller implements Unmarshaller {

    private boolean keepWhitespace = true

    public void setKeepWhitespace(boolean keepWhitespace) {
        this.keepWhitespace = keepWhitespace
    }

    /**
     * Unmarshals the given provided <code>javax.xml.transform.Source</code> into an object graph.
     * <p>This implementation inspects the given result, and calls <code>unmarshalSaxSource</code>, or <code>unmarshalStreamSource</code>.
     * @param source the source to marshal from
     * @return the object graph
     * @throws IOException if an I/O Exception occurs
     * @throws XmlMappingException if the given source cannot be mapped to an object
     * @throws IllegalArgumentException if <code>source</code> is neither a <code>SAXSource</code>, nor a <code>StreamSource</code>
     * @see #unmarshalSaxSource(javax.xml.transform.sax.SAXSource)
     * @see #unmarshalStreamSource(javax.xml.transform.stream.StreamSource)
     */
    public final Object unmarshal(Source source) throws IOException, XmlMappingException {
        if (source instanceof GPathResultSource) {
            return unmarshalGPathResult(((GPathResultSource) source).xml)
        } else if (source instanceof SAXSource) {
            return unmarshalSaxSource((SAXSource) source);
        } else if (source instanceof StreamSource) {
            return unmarshalStreamSource((StreamSource) source);
        } else {
            throw new IllegalArgumentException("Unknown Source type: " + source.getClass());
        }
    }

    /**
     * Template method for handling <code>SAXSource</code>s.
     * <p>This implementation delegates to <code>unmarshalSaxReader</code>.
     * @param saxSource the <code>SAXSource</code>
     * @return the object graph
     * @throws XmlMappingException if the given source cannot be mapped to an object
     * @throws IOException if an I/O Exception occurs
     * @see #unmarshalSaxReader(org.xml.sax.XMLReader, org.xml.sax.InputSource)
     */
    protected Object unmarshalSaxSource(SAXSource saxSource) throws XmlMappingException, IOException {
        if (saxSource.getXMLReader() == null) {
            try {
                saxSource.setXMLReader(createXmlReader());
            } catch (SAXException ex) {
                throw new UnmarshallingFailureException("Could not create XMLReader for SAXSource", ex);
            }
        }
        if (saxSource.getInputSource() == null) {
            saxSource.setInputSource(new InputSource());
        }
        return unmarshalSaxReader(saxSource.getXMLReader(), saxSource.getInputSource());
    }

    /**
     * Template method for handling <code>StreamSource</code>s.
     * <p>This implementation defers to <code>unmarshalInputStream</code> or <code>unmarshalReader</code>.
     * @param streamSource the <code>StreamSource</code>
     * @return the object graph
     * @throws IOException if an I/O exception occurs
     * @throws XmlMappingException if the given source cannot be mapped to an object
     */
    protected Object unmarshalStreamSource(StreamSource streamSource) throws XmlMappingException, IOException {
        if (streamSource.getInputStream() != null) {
            return unmarshalInputStream(streamSource.getInputStream());
        } else if (streamSource.getReader() != null) {
            return unmarshalReader(streamSource.getReader());
        } else {
            throw new IllegalArgumentException("StreamSource contains neither InputStream nor Reader");
        }
    }

    /**
     * Create a <code>XMLReader</code> that this marshaller will when passed an empty <code>SAXSource</code>.
     * @return the XMLReader
     * @throws SAXException if thrown by JAXP methods
     */
    protected XMLReader createXmlReader() throws SAXException {
        return org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
    }

    protected XmlSlurper createXmlSlurper(XMLReader xmlReader) {
        XmlSlurper slurper = new XmlSlurper(xmlReader)
        slurper.setKeepWhitespace this.keepWhitespace
        return slurper
    }

    protected Object unmarshalInputStream(InputStream inputStream) {
        return unmarshalGPathResult(createXmlSlurper(createXmlReader()).parse(inputStream))
    }

    protected Object unmarshalReader(Reader reader) {
        return unmarshalGPathResult(createXmlSlurper(createXmlReader()).parse(reader))
    }

    protected Object unmarshalSaxReader(XMLReader xmlReader, InputSource inputSource) {
        return unmarshalGPathResult(createXmlSlurper(xmlReader).parse(inputSource))
    }

    abstract protected Object unmarshalGPathResult(GPathResult xml)
}
