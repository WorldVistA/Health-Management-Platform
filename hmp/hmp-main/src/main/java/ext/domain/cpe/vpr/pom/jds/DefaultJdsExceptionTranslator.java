package org.osehra.cpe.vpr.pom.jds;

import org.osehra.cpe.vpr.pom.POMUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

public class DefaultJdsExceptionTranslator implements JdsExceptionTranslator {

    @Override
    public DataAccessException translate(String task, String url, RestClientException e) {
        if (e instanceof ResourceAccessException) {
            return new DataAccessResourceFailureException(getMessage(task, url), e);
        } else if (e instanceof HttpStatusCodeException) {
            return new DataRetrievalFailureException(getMessage(task, url, ((HttpStatusCodeException) e).getResponseBodyAsString()), e);
        } else {
            return new NonTransientDataAccessResourceException(getMessage(task, url), e);
        }
    }

    private String getMessage(String task, String url) {
        return "JDS error during " + task + " at \"" + url + "\"";
    }

    private String getMessage(String task, String url, String jsonResponse) {
        JsonNode json = POMUtils.parseJSONtoNode(jsonResponse);
        JsonNode firstError = json.path("error").path("errors").path(0);
        String message = firstError.path("message").asText();

        return "JDS error during " + task + " at \"" + url + "\": " + message;
    }
}
