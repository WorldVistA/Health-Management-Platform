package org.osehra.cpe.vpr.web.servlet.filter;
 
import java.io.IOException;
import java.util.Enumeration;
 
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
 
/**
 * Servlet filter that sets HTTP Response Headers based on initialization parameters.
 * <p/>
 * Example usage in web.xml of servlet application:
 * <code>
 * <filter>
 * <filter-name>responseHeaderFilter</filter-name>
 * <filter-class>org.osehra.cpe.vpr.web.servlet.filter.ResponseHeaderFilter</filter-class>
 * <init-param>
 * <param-name>Cache-Control</param-name>
 * <param-value>max-age=3600</param-value>
 * </init-param>
 * </filter>
 * ...
 * <filter-mapping>
 * <filter-name>responseHeaderFilter</filter-name>
 * <url-pattern>/logo.png</url-pattern>
 * </filter-mapping>
 * </code>
 */
public class ResponseHeaderFilter implements Filter {
 
    FilterConfig filterConfig;
 
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
 
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        // set the provided HTTP response parameters
        for (Enumeration e = filterConfig.getInitParameterNames(); e.hasMoreElements();) {
            String headerName = (String) e.nextElement();
            response.addHeader(headerName, filterConfig.getInitParameter(headerName));
        }
 
        filterChain.doFilter(req, response);
    }
 
    public void destroy() {
        this.filterConfig = null;
    }
}
