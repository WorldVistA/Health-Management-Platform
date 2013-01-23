package org.osehra.cpe.auth;

import org.osehra.cpe.vpr.web.WebUtils;
import org.springframework.security.web.util.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * RequestMatcher that detects AJAX requests via the X-Requested-With HTTP Header.
 */
public class AjaxRequestMatcher implements RequestMatcher {
    @Override
    public boolean matches(HttpServletRequest request) {
        return WebUtils.isAjax(request);
    }
}
