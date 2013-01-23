package org.osehra.cpe.vpr.sync.vista;

import static org.junit.Assert.*
import static org.hamcrest.CoreMatchers.*
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Ignore
import org.junit.Test;

public class VistaDataChunkTest {

    @Test
    public void testSetContentWithJson() throws Exception {
        String jString = "{\"foo\":\"bar\"}"
        JsonNode jNode = new ObjectMapper().readTree(jString)
        def chunk = new VistaDataChunk(domain: "baz", json: jNode)
        assertThat(chunk.content, is(jString))
        assertThat(chunk.domain, is("baz"))
    }

    @Test
    public void testGetJsonMap() throws Exception {
        String jsonString = "{\"foo\":\"bar\"}"
        VistaDataChunk chunk = new VistaDataChunk(json: new ObjectMapper().readTree(jsonString))
        assertThat(chunk.content, is(jsonString))
        assertThat(chunk.domain, nullValue())
        assertThat(chunk.getJsonMap(), equalTo(new ObjectMapper().readValue(jsonString, Map.class)))
    }
}
