package EXT.DOMAIN.cpe.auth

import org.springframework.security.web.util.RequestMatcher

import javax.servlet.http.HttpServletRequest

/**
 * Matches requests where a request parameter of format is 'json' or 'xml', so spring security can
 * route authentication through the appropriate entry point.
 *
 * @see org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint
 */
class WebServiceRequestMatcher implements RequestMatcher {

    boolean matches(HttpServletRequest request) {
        String format = request.getParameter("format")
        if (!format) return false
        return format.equalsIgnoreCase("xml") || format.equalsIgnoreCase("json")
    }

}
