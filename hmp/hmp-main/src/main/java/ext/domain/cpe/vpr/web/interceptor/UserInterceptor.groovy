package org.osehra.cpe.vpr.web.interceptor

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.web.servlet.ModelAndView
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication

import org.osehra.cpe.param.ParamService
import org.springframework.web.servlet.view.UrlBasedViewResolver
import org.osehra.cpe.param.Param
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import org.osehra.cpe.vpr.pom.POMUtils

/**
 * Adds current user details and params to the ModelMap so that they are in all views.
 */
class UserInterceptor extends HandlerInterceptorAdapter {

    private static final String VPR_USER_PREF_PARAM = 'VPR USER PREF'

    @Autowired
    ParamService paramService

    @Override
    void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView && !modelAndView.viewName.startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX) && !modelAndView.viewName.startsWith(UrlBasedViewResolver.FORWARD_URL_PREFIX)) {
            Authentication auth = SecurityContextHolder.context.authentication;
            Map user = [:]

            // include user.details
            if (auth?.principal) {
                user.details = auth.principal
            }

            // include user.params
            Map values = paramService.getUserParamMap(VPR_USER_PREF_PARAM, null);
            if (values) {
                user.params = [
                        'ext.lib': values.'ext.lib' ?: null,
                        'ext.theme': values.'ext.theme' ?: 'ext-all.css',
                        'cpe.patientpicker.loc': values.'cpe.patientpicker.loc' ?: 'north',
                        'cpe.patientpicker.pinned': values.'cpe.patientpicker.pinned' ?: 'false',
                        'cpe.patientpicker.roster': values.'cpe.patientpicker.roster' ?: 'null',
                        'cpe.patientpicker.loc': values.'cpe.patientpicker.mask' ?: 'north',
                        'cpe.patientpicker.loc': values.'cpe.patientpicker.hash' ?: 'false'
                ]
            }
            modelAndView.getModel().put("user", user);
        }
    }

}
