package org.osehra.cpe.vpr.web.servlet.mvc;

import org.codehaus.groovy.grails.commons.ApplicationAttributes;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.web.context.ServletContextHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GrailsWebRequestFilter extends org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        super.doFilterInternal(request, response, filterChain);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void initialize() {
        super.initialize();

        WebApplicationContext webContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        getServletContext().setAttribute(ApplicationAttributes.PARENT_APPLICATION_CONTEXT, webContext.getParent());
        getServletContext().setAttribute(GrailsApplication.APPLICATION_ID, webContext.getBean(GrailsApplication.APPLICATION_ID, GrailsApplication.class));

        getServletContext().setAttribute(ApplicationAttributes.APPLICATION_CONTEXT, webContext);

//         ServletContextHolder.setServletContext(getServletContext());
    }
}
