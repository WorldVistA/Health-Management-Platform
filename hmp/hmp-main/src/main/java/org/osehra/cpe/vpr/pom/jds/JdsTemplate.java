package org.osehra.cpe.vpr.pom.jds;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.osehra.cpe.datetime.jackson.HealthTimeModule;
import org.osehra.cpe.jsonc.JsonCCollection;
import org.osehra.cpe.vpr.pom.IPOMObject;
import org.osehra.cpe.vpr.pom.JSONViews;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class JdsTemplate implements JdsOperations, InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected RestOperations restTemplate;

    protected String jdsUrl;

    private ObjectMapper jsonMapper;

    private ResponseExtractor<JsonNode> jsonNodeResponseExtractor;

    private JdsExceptionTranslator exceptionTranslator = new DefaultJdsExceptionTranslator();

    private boolean connectOnInitialization = true;

    public JdsTemplate() {
        jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new HealthTimeModule());
//        jsonMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Required
    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
        jsonNodeResponseExtractor = new ResponseExtractor<JsonNode>() {
            public JsonNode extractData(ClientHttpResponse response)
                    throws IOException {
                return new ObjectMapper().readTree(new InputStreamReader(response.getBody(), Charset.forName("ISO-8859-1")));
            }
        };
    }

    @Required
    public void setJdsUrl(String jdsUrl) {
        this.jdsUrl = jdsUrl;
        if (this.jdsUrl != null && !this.jdsUrl.endsWith("/")) this.jdsUrl = this.jdsUrl + "/";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.restTemplate, "[Assertion failed] - 'restTemplate' is required; it must not be null");
        Assert.hasText(this.jdsUrl, "[Assertion failed] - 'jdsUrl' must have length; it must not be null or empty");

        if (connectOnInitialization) {
            ping();
        }
    }

    public void setExceptionTranslator(JdsExceptionTranslator exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    public void setConnectOnInitialization(boolean connectOnInitialization) {
        this.connectOnInitialization = connectOnInitialization;
    }

    protected <T> HttpEntity<String> createHttpEntity(T entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            String json = null;
            if (entity instanceof IPOMObject) {
                json = ((IPOMObject) entity).toJSON(JSONViews.JDBView.class);
            } else {
                json = jsonMapper.writeValueAsString(entity);
            }
            return new HttpEntity<String>(json, headers);
        } catch (IOException e) {
            throw new DataAccessResourceFailureException("unable to convert entity to JSON", e);
        }
    }

    @Override
    public void ping() throws DataAccessException {
        String url = this.jdsUrl + "ping";
        try {
            restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            if (!isNotFound(e))
                throw handleResponseError("ping", url, e);
        }
    }

    @Override
    public JsonCCollection<Map<String, Object>> getForJsonC(String url, Object... uriVariables) {
        try {
            JsonNode jsonNode = doGetForJsonNode(url, uriVariables);
            JsonCCollection response = jsonMapper.convertValue(jsonNode, JsonCCollection.class);
            return response;
        } catch (RestClientException e) {
            if (isNotFound(e))
                return null;
            else
                throw handleResponseError("getForJsonC", url, e);
        }
    }

    @Override
    public JsonCCollection<Map<String, Object>> getForJsonC(String url, Map<String, ?> uriVariables) throws DataAccessException {
        try {
            JsonNode jsonNode = doGetForJsonNode(url, uriVariables);
            JsonCCollection<Map<String, Object>> response = JsonCCollection.create(jsonNode);
            return response;
        } catch (RestClientException e) {
            if (isNotFound(e))
                return null;
            else
                throw handleResponseError("getForJsonC", url, e);
        }
    }

    @Override
    public JsonNode getForJsonNode(String url) throws DataAccessException {
        try {
            return doGetForJsonNode(url);
        } catch (RestClientException e) {
            if (isNotFound(e))
                return null;
            else
                throw handleResponseError("getForJsonNode", url, e);
        }
    }

    public Map getForMap(String url) {
        try {
            return restTemplate.getForObject(getAbsoluteUrl(url), Map.class);
        } catch (RestClientException e) {
            if (isNotFound(e))
                return null;
            else
                throw handleResponseError("getForMap", url, e);
        }
    }

    public String getForString(String url) {
        try {
            return restTemplate.getForObject(getAbsoluteUrl(url), String.class);
        } catch (RestClientException e) {
            if (isNotFound(e))
                return null;
            else
                throw handleResponseError("getForString", url, e);
        }
    }

    public <T> List<T> getForList(Class<T> responseType, String url, Object... uriVariables) {
        try {
            JsonNode jsonNode = doGetForJsonNode(url, uriVariables);
            JsonCCollection response = jsonMapper.convertValue(jsonNode, JsonCCollection.class);
            return response.getItems();
        } catch (RestClientException e) {
            if (isNotFound(e))
                return null;
            else
                throw handleResponseError("getForJsonC", url, e);
        }
    }

    @Override
    public <T> T getForObject(String url, Class<T> responseType) {
        return getForObject(url, responseType, null);
    }

    @Override
    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws DataAccessException {
        try {
            JsonNode json = doGetForJsonNode(url);
            if (json == null) return null;
            // TODO: check for errors
//        if (json.has("error")) throw new exception;
//        if (json.get("data").get("items").get(0))
            JsonNode item = json.path("data").path("items").path(0);
            // TODO: check for missing node item.isMissingNode()
            T returnValue = jsonMapper.convertValue(item, responseType);

            logResponseSuccess("getForObject", url);
            return returnValue;
        } catch (RestClientException e) {
            if (isNotFound(e))
                return null;
            else
                throw handleResponseError("getForObject", url, e);
        }
    }

    @Override
    public <T> URI postForLocation(String url, T item) {
        try {
            URI uri = restTemplate.postForLocation(getAbsoluteUrl(url), createHttpEntity(item));
            logResponseSuccess("postForLocation", url);
            return uri;
        } catch (RestClientException e) {
            throw handleResponseError("postForLocation", url, e);
        }
    }

    @Override
    public <T> URI putForLocation(String url, T item) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(String url) {
        try {
            restTemplate.delete(getAbsoluteUrl(url));
        } catch (RestClientException e) {
            if (isNotFound(e)) {
                logger.warn("delete request for \"" + url + "\" resulted in 404 (Not Found)");
            } else {
                throw handleResponseError("delete", url, e);
            }
        }
    }

    protected JsonNode doGetForJsonNode(String url, Object... uriVariables) {
        return restTemplate.execute(toUri(url, uriVariables), HttpMethod.GET, null, jsonNodeResponseExtractor);
    }

    protected JsonNode doGetForJsonNode(String url, Map<String, ?> uriVariables) {
        return restTemplate.execute(toUri(url, uriVariables), HttpMethod.GET, null, jsonNodeResponseExtractor);
    }

    private boolean isNotFound(RestClientException e) {
        return e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode().equals(HttpStatus.NOT_FOUND);
    }

    protected URI toUri(String url, Object... uriVariables) {
        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString(getAbsoluteUrl(url));
        if (uriVariables != null && uriVariables.length > 0) {
            String query = ucb.build().getQuery();
            ucb.query(null);
            ucb = UriComponentsBuilder.fromUriString(ucb.buildAndExpand(uriVariables).toUriString());
            ucb.query(query);
            return ucb.build().toUri();
        } else {
            return ucb.build().toUri();
        }
    }

    protected URI toUri(String url, Map<String, ?> uriVariables) {
        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString(getAbsoluteUrl(url));
        if (uriVariables != null && !uriVariables.isEmpty()) {
            String query = ucb.build().getQuery();
            ucb.query(null);
            ucb = UriComponentsBuilder.fromUriString(ucb.buildAndExpand(uriVariables).toUriString());
            ucb.query(query);
            return ucb.build().toUri();
        } else {
            return ucb.build().toUri();
        }
    }

    private String getAbsoluteUrl(String url) {
        return StringUtils.applyRelativePath(jdsUrl, url);
    }

    private void logResponseSuccess(String method, String url) {
        if (logger.isDebugEnabled()) {
            logger.debug(method + " request for \"" + url + "\" resulted in 200 (OK)");
        }
    }

    private DataAccessException handleResponseError(String method, String url, RestClientException e) {
        if (logger.isErrorEnabled()) {
            if (e instanceof HttpStatusCodeException) {
                HttpStatusCodeException ex = (HttpStatusCodeException) e;
                logger.error(method + " request for \"" + url + "\" resulted in " + ex.getStatusCode() + " (" +
                        ex.getStatusText() + "); invoking error handler");
            } else {
                logger.error(method + " request for \"" + url + "\" resulted in " + e.getClass().getSimpleName() + " (" +
                        e.getMessage() + "); invoking error handler");
            }
        }
        return exceptionTranslator.translate(method, url, e);
    }
}
