package EXT.DOMAIN.cpe.vpr.pom.jds;

import EXT.DOMAIN.cpe.jsonc.JsonCCollection;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.dao.DataAccessException;

import java.net.URI;
import java.util.Map;

/**
 * Interface specifying a basic set of JSON Data Store (JDS) operations. Implemented by {@link JdsTemplate}.
 * Not often used directly, but a useful option to enhance testability, as it can easily
 * be mocked or stubbed.
 *
 * @see JdsTemplate
 */
public interface JdsOperations {
    // TODOC: write javadoc for this
    void ping() throws DataAccessException;

    // TODOC: write javadoc for this
    JsonCCollection<Map<String, Object>> getForJsonC(String url, Object... uriVariables) throws DataAccessException;

    // TODOC: write javadoc for this
    JsonCCollection<Map<String, Object>> getForJsonC(String url, Map<String, ?> uriVariables) throws DataAccessException;

    // TODOC: write javadoc for this
    JsonNode getForJsonNode(String url) throws DataAccessException;

    /**
     * Retrieve a representation by doing a GET on the relative JDS URL.
     * The response (if any) is converted and returned.
     * @param url the URL
     * @param responseType the type of the return value
     * @return the converted object
     */
    <T> T getForObject(String url, Class<T> responseType) throws DataAccessException;

    /**
     * Retrieve a representation by doing a GET on the relative JDS URI template.
     * The response (if any) is converted and returned.
     * <p>URI Template variables are expanded using the given map.
     * @param url the URL
     * @param responseType the type of the return value
     * @param uriVariables the map containing variables for the URI template
     * @return the converted object
     */
    <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws DataAccessException;

    // TODOC: write javadoc for this
    <T> URI postForLocation(String url, T item) throws DataAccessException;

    // TODOC: write javadoc for this
    <T> URI putForLocation(String url, T item) throws DataAccessException;

    // TODOC: write javadoc for this
    void delete(String url) throws DataAccessException;

}
