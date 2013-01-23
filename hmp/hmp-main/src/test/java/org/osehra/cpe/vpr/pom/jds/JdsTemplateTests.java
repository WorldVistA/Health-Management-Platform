package org.osehra.cpe.vpr.pom.jds;

import org.osehra.cpe.jsonc.JsonCCollection;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.sync.vista.Foo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class JdsTemplateTests {
    private static final String JDS_URL = "http://localhost:9080";

    private RestOperations mockRestTemplate;
    private JdsTemplate t;
    private URI uri;

    @Before
    public void setUp() throws Exception {
        mockRestTemplate = mock(RestOperations.class);

        t = new JdsTemplate();
        t.setJdsUrl(JDS_URL);
        t.setRestTemplate(mockRestTemplate);
        uri = t.toUri("/vpr/34");
    }

    @Test
    public void testSetJdsUrlAddsTrailingSlash() throws Exception {
        assertThat(t.jdsUrl, equalTo(JDS_URL + "/"));
    }

    @Test
    public void testAfterPropertiesTestsConnectionToJds() throws Exception {
        t.afterPropertiesSet();
        verify(mockRestTemplate).getForObject(t.jdsUrl + "ping", String.class);
    }

    @Test
    public void testConnectOnInitializationFalse() throws Exception {
        t.setConnectOnInitialization(false);
        t.afterPropertiesSet();
        verifyZeroInteractions(mockRestTemplate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetMissingJdsUrl() throws Exception {
        t.setJdsUrl(null);
        t.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterPropertiesSetMissingRestTemplate() throws Exception {
        t.setRestTemplate(null);
        t.afterPropertiesSet();
    }

    @Test
    public void testGetForMap() throws Exception {
        Map mockResponse = new HashMap();
        mockResponse.put("apiVersion", "1.0");
        when(mockRestTemplate.getForObject(JDS_URL + "/vpr/34", Map.class)).thenReturn(mockResponse);

        Map map = t.getForMap("/vpr/34");
        assertThat((String) map.get("apiVersion"), equalTo("1.0"));

        verify(mockRestTemplate).getForObject(JDS_URL + "/vpr/34", Map.class);
    }

    @Test
    public void testGetForJsonC() throws Exception {
        JsonNode mockResponse = new ObjectMapper().readTree("{\"data\":{\"items\":[{\"foo\":\"bar\"}]}}");
        when(mockRestTemplate.execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any())).thenReturn(mockResponse);
//		when(mockRestTemplate.getForObject(eq(uri), eq(JsonNode.class))).thenReturn(mockResponse);

        JsonCCollection r = t.getForJsonC("/vpr/34");
        assertThat(r.getItems().size(), equalTo(1));
        verify(mockRestTemplate).execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any());
    }

    @Test
    public void testGetForJsonCNotFound() throws Exception {
        //when(mockRestTemplate.getForObject(eq(uri), eq(JsonNode.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        when(mockRestTemplate.execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        ;

        JsonCCollection r = t.getForJsonC("/vpr/34");
        assertThat(r, nullValue());
        verify(mockRestTemplate).execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any());
    }

    @Test
    public void testGetForJsonNode() throws Exception {
        JsonNode mockResponse = new ObjectMapper().readTree("{\"apiVersion\":\"1.0\",\"data\":{\"items\":[{\"icn\":\"foo\"}]}}");
        //when(mockRestTemplate.getForObject(uri, JsonNode.class)).thenReturn(mockResponse);
        when(mockRestTemplate.execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any())).thenReturn(mockResponse);

        JsonNode json = t.getForJsonNode("/vpr/34");
        verify(mockRestTemplate).execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any());
    }

    @Test
    public void testGetForObject() throws Exception {
        JsonNode mockResponse = new ObjectMapper().readTree("{\"apiVersion\":\"1.0\",\"data\":{\"items\":[{\"icn\":\"foo\"}]}}");
        //when(mockRestTemplate.getForObject(uri, JsonNode.class)).thenReturn(mockResponse);
        when(mockRestTemplate.execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any())).thenReturn(mockResponse);

        Patient pt = t.getForObject("/vpr/34", Patient.class);
        assertThat(pt.getIcn(), equalTo("foo"));
        verify(mockRestTemplate).execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any());
    }

    @Test
    public void testGetForObjectNotFound() throws Exception {
        //when(mockRestTemplate.getForObject(uri, JsonNode.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        when(mockRestTemplate.execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        Patient pt = t.getForObject("/vpr/34", Patient.class);
        assertThat(pt, nullValue());
        verify(mockRestTemplate).execute(eq(uri), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any());
    }

    @Test
    public void testPostForLocation() throws Exception {
        Foo foo = new Foo();
        foo.setBar("spaz");

        when(mockRestTemplate.postForLocation(eq(JDS_URL + "/vpr/34"), any(HttpEntity.class))).thenReturn(URI.create("/vpr/34/foo"));

        URI uri = t.postForLocation("/vpr/34", foo);
        assertThat(uri.getPath(), equalTo("/vpr/34/foo"));

        ArgumentCaptor<HttpEntity> httpEntity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(mockRestTemplate).postForLocation(eq(JDS_URL + "/vpr/34"), httpEntity.capture());
        assertThatHttpEntityIsJSONRequest(httpEntity.getValue());
    }

    @Test
    public void testToUriExpandsPathVariablesButNotQueryComponents() {
        Map params= new HashMap();
        params.put("pid", 34);
        t.getForJsonC("/vpr/{pid}/foo/bar?filter=or{eq(baz,spaz)}", params);

        verify(mockRestTemplate).execute(eq(URI.create(JDS_URL + "/vpr/34/foo/bar?filter=or%7Beq(baz,spaz)%7D")), eq(HttpMethod.GET), (RequestCallback) any(), (ResponseExtractor<Object>) any());
    }

    @Test
    public void testToUri() throws Exception {
        //Scenarios where special characters get treated differently in URI
        // This is important when sending uri with special characters to the JDS store using rest client

        // Handle curly braces in url - encoded {}
        uri = t.toUri("/vpr/uid:urn:va:F484:100845:obs:{914D8B94-68F2-44CB-885E-859196F0D4D8}");
        assertThat(uri.getPath(), equalTo("/vpr/uid:urn:va:F484:100845:obs:{914D8B94-68F2-44CB-885E-859196F0D4D8}"));
        assertThat(uri.getQuery(), equalTo(null));
        assertThat(uri.getRawSchemeSpecificPart(), equalTo("//localhost:9080/vpr/uid:urn:va:F484:100845:obs:%7B914D8B94-68F2-44CB-885E-859196F0D4D8%7D"));

        // Special characters for URIComponents with care for special chars in vri variables: \\{([^/]+?)\\}
        // they are treated diferently get encoded
        uri = t.toUri("/vpr/uid:urn:va:F484:100845:obs#abc");
        assertThat(uri.getPath(), equalTo("/vpr/uid:urn:va:F484:100845:obs"));
        assertThat(uri.getRawSchemeSpecificPart(), equalTo("//localhost:9080/vpr/uid:urn:va:F484:100845:obs"));

        uri = t.toUri("/vpr/uid:urn:va:F484:100845:o?asd");
        assertThat(uri.getPath(), equalTo("/vpr/uid:urn:va:F484:100845:o"));
        assertThat(uri.getQuery(), equalTo("asd"));
        assertThat(uri.getRawSchemeSpecificPart(), equalTo("//localhost:9080/vpr/uid:urn:va:F484:100845:o?asd"));

        // '%' gets encodded
        uri = t.toUri("/vpr/uid:urn:va:F484:100845:obs:%123");
        assertThat(uri.getPath(), equalTo("/vpr/uid:urn:va:F484:100845:obs:%123"));
        assertThat(uri.getRawSchemeSpecificPart(), equalTo("//localhost:9080/vpr/uid:urn:va:F484:100845:obs:%25123"));

        uri = t.toUri("/vpr/uid:urn:va:F484:100845:obs://123");
        assertThat(uri.getPath(), equalTo("/vpr/uid:urn:va:F484:100845:obs://123"));
        assertThat(uri.getRawSchemeSpecificPart(), equalTo("//localhost:9080/vpr/uid:urn:va:F484:100845:obs://123"));

        // '\\' get encoded
        uri = t.toUri("/vpr/uid:urn:va:F484:100845:obs:\\123");
        assertThat(uri.getPath(), equalTo("/vpr/uid:urn:va:F484:100845:obs:\\123"));
        assertThat(uri.getRawSchemeSpecificPart(), equalTo("//localhost:9080/vpr/uid:urn:va:F484:100845:obs:%5C123"));

        uri = t.toUri("/vpr/uid:urn:va:F484:100845:obs:(123)");
        assertThat(uri.getPath(), equalTo("/vpr/uid:urn:va:F484:100845:obs:(123)"));
        assertThat(uri.getRawSchemeSpecificPart(), equalTo("//localhost:9080/vpr/uid:urn:va:F484:100845:obs:(123)"));

        uri = t.toUri("/vpr/uid:urn:va:F484:100845:obs:123+abc");
        assertThat(uri.getPath(), equalTo("/vpr/uid:urn:va:F484:100845:obs:123+abc"));
        assertThat(uri.getRawSchemeSpecificPart(), equalTo("//localhost:9080/vpr/uid:urn:va:F484:100845:obs:123+abc"));

    }

    private void assertThatHttpEntityIsJSONRequest(HttpEntity httpEntity) {
        assertThat(httpEntity.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
        assertThat(httpEntity.getBody().toString().startsWith("{"), is(true));
        assertThat(httpEntity.getBody().toString().endsWith("}"), is(true));
    }
}
