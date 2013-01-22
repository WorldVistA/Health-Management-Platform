package EXT.DOMAIN.cpe.grails.plugins

import org.codehaus.groovy.grails.plugins.web.ServletsGrailsPlugin
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class GrailsServletsPluginRegistrar implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext

    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
    }

    void afterPropertiesSet() {
        new ServletsGrailsPlugin().doWithDynamicMethods(applicationContext)
    }
}
