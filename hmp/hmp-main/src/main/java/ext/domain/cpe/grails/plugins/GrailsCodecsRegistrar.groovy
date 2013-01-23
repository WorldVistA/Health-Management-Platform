package org.osehra.cpe.grails.plugins

import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsCodecClass

@Component
class GrailsCodecsRegistrar implements InitializingBean {

    @Autowired
    private GrailsApplication application

    void afterPropertiesSet() {
        for (GrailsCodecClass c in application.codecClasses) {
            def codecClass = c
            String codecName = codecClass.name
            String encodeMethodName = "encodeAs${codecName}"
            String decodeMethodName = "decode${codecName}"

            def encoder
            def decoder

            // Resolve codec methods once only at startup
            def encodeMethod = codecClass.encodeMethod
            def decodeMethod = codecClass.decodeMethod
            if (encodeMethod) {
                encoder = {-> encodeMethod(delegate) }
            }
            else {
                // note the call to delegate.getClass() instead of the more groovy delegate.class.
                // this is because the delegate might be a Map, in which case delegate.class doesn't
                // do what we want here...
                encoder = {-> throw new MissingMethodException(encodeMethodName, delegate.getClass(), [] as Object[]) }
            }
            if (decodeMethod) {
                decoder = {-> decodeMethod(delegate) }
            }
            else {
                // note the call to delegate.getClass() instead of the more groovy delegate.class.
                // this is because the delegate might be a Map, in which case delegate.class doesn't
                // do what we want here...
                decoder = {-> throw new MissingMethodException(decodeMethodName, delegate.getClass(), [] as Object[]) }
            }

            Object.metaClass."${encodeMethodName}" << encoder
            Object.metaClass."${decodeMethodName}" << decoder
        }
    }
}
