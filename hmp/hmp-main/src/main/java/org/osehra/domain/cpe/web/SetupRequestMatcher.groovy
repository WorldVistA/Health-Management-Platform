package org.osehra.cpe.web

import org.springframework.security.web.util.RequestMatcher
import javax.servlet.http.HttpServletRequest

import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationContextAware
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.osehra.cpe.Bootstrap

/**
 *  RequestMatcher that checks to see if application setup as run yet or not.
 *  If it has not, spring security will redirect to the configured entry point in order to begin the setup process.
 */
class SetupRequestMatcher implements RequestMatcher, EnvironmentAware {

    private Environment environment;

    void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    boolean matches(HttpServletRequest request) {
        return !Bootstrap.isSetupComplete(environment);
    }

}
