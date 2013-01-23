package org.osehra.cpe.grails.plugins

import org.springframework.beans.factory.annotation.Autowired
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import org.codehaus.groovy.grails.commons.GrailsTagLibClass
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.plugins.support.WebMetaUtils
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.context.request.RequestContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.web.pages.GroovyPageBinding
import org.codehaus.groovy.grails.web.pages.GroovyPageOutputStack
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.web.pages.TagLibraryLookup

class GrailsTagLibsRegistrar implements InitializingBean {

    @Autowired
    private GrailsApplication application

    @Autowired
    private TagLibraryLookup gspTagLibraryLookup

    void afterPropertiesSet() {
        for (GrailsTagLibClass t in application.tagLibClasses) {
            GrailsTagLibClass taglib = t
            MetaClass mc = taglib.clazz.metaClass
            String namespace = taglib.namespace ?: GroovyPage.DEFAULT_NAMESPACE

            WebMetaUtils.registerCommonWebProperties(mc, application)

            for (tag in taglib.tagNames) {
                WebMetaUtils.registerMethodMissingForTags(mc, gspTagLibraryLookup, namespace, tag)
            }

            mc.getTagNamesThatReturnObject = {-> taglib.getTagNamesThatReturnObject() }

            mc.throwTagError = {String message -> throw new GrailsTagException(message) }

//            mc.getPluginContextPath = {->
//                pluginManager.getPluginPathForInstance(delegate) ?: ""
//            }

            mc.getPageScope = {->
                def request = RequestContextHolder.currentRequestAttributes().currentRequest
                def binding = request.getAttribute(GrailsApplicationAttributes.PAGE_SCOPE)
                if (!binding) {
                    binding = new GroovyPageBinding()
                    request.setAttribute(GrailsApplicationAttributes.PAGE_SCOPE, binding)
                }
                binding
            }

            mc.getOut = {->
                GroovyPageOutputStack.currentWriter()
            }
            mc.setOut = {Writer newOut ->
                GroovyPageOutputStack.currentStack().push(newOut,true)
            }

            mc.propertyMissing = { String name ->
                def result = gspTagLibraryLookup.lookupNamespaceDispatcher(name)
                if (result == null) {
                    def tagLibrary = gspTagLibraryLookup.lookupTagLibrary(namespace, name)
                    if (!tagLibrary) {
                        tagLibrary = gspTagLibraryLookup.lookupTagLibrary(GroovyPage.DEFAULT_NAMESPACE, name)
                    }

                    def tagProperty = tagLibrary?."$name"
                    result = tagProperty ? tagProperty.clone() : null
                }

                if (result != null) {
                    mc."${GrailsClassUtils.getGetterName(name)}" = {-> result }
                    return result
                }

                throw new MissingPropertyException(name, delegate.class)
            }

            mc.methodMissing = { String name, args ->
                def usednamespace = namespace
                def tagLibrary = gspTagLibraryLookup.lookupTagLibrary(namespace, name)
                if (!tagLibrary) {
                    tagLibrary = gspTagLibraryLookup.lookupTagLibrary(GroovyPage.DEFAULT_NAMESPACE, name)
                    usednamespace = GroovyPage.DEFAULT_NAMESPACE
                }
                if (tagLibrary) {
                    WebMetaUtils.registerMethodMissingForTags(mc, gspTagLibraryLookup, usednamespace, name)
                    //WebMetaUtils.registerMethodMissingForTags(mc, tagLibrary, name)
                }
                if (mc.respondsTo(delegate, name, args)) {
                    return mc.invokeMethod(delegate, name, args)
                }

                throw new MissingMethodException(name, delegate.class, args)
            }
        }
    }
}
