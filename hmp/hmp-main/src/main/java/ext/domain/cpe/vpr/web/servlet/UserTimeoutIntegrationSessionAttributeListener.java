package org.osehra.cpe.vpr.web.servlet;

import org.osehra.cpe.auth.HmpUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * Sets the servlet {@link javax.servlet.http.HttpSession} timeout based on an HMP user's timeout setting.
 *
 * @see org.osehra.cpe.auth.HmpUserDetails#getTimeoutSeconds()
 */
public class UserTimeoutIntegrationSessionAttributeListener implements HttpSessionAttributeListener {

    private static final Logger log = LoggerFactory.getLogger(UserTimeoutIntegrationSessionAttributeListener.class);

    public void attributeAdded(HttpSessionBindingEvent event) {
        if (!event.getName().equals(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)) return;

        setTimeOut(event);
    }

    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (!event.getName().equals(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)) return;
    }

    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (!event.getName().equals(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)) return;

        setTimeOut(event);
    }

    private void setTimeOut(HttpSessionBindingEvent event) {
        SecurityContext securityContext = (SecurityContext) event.getValue();
        if (securityContext == null) return;
        Authentication auth = securityContext.getAuthentication();
        if (auth == null) return;
        if (auth.getPrincipal() != null && auth.getPrincipal() instanceof HmpUserDetails) {
            HmpUserDetails userInfo = (HmpUserDetails) auth.getPrincipal();

            int timeOut = userInfo.getTimeoutSeconds();
            event.getSession().setMaxInactiveInterval(timeOut);
            log.debug("set timeout for user {} to {} seconds.", userInfo.getDUZ(), timeOut);
        }
    }
}
