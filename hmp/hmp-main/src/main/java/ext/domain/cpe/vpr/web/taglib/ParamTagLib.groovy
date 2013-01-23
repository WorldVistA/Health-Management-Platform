package org.osehra.cpe.vpr.web.taglib

import org.osehra.cpe.param.ParamService
import org.springframework.context.ApplicationContext
import javax.servlet.ServletContext
import org.springframework.web.context.support.WebApplicationContextUtils

// TODO: now that caching is handled internally in paramService, maybe we don't really need this taglib anymore?
class ParamTagLib {
    static namespace = 'hmp'

    // injected by name from Spring Application Context via GSP rendering layer
    ParamService paramService

    def paramVal = { attrs ->
		def val = paramService.getUserParamVal(attrs.param, attrs.key) ?: attrs.defaultVal;
        out << val.encodeAsHTML()
    }

    def userPref = { attrs ->
		def val = paramService.getUserParamVal("VPR USER PREF", attrs.key) ?: attrs.defaultVal;
        out << val.encodeAsHTML()
    }

    // like userPref tag but validates existence of specified resource (js library or css file)
    def userPrefResource = { attrs ->
        def val = paramService.getUserParamVal("VPR USER PREF", attrs.key) ?: attrs.defaultVal;
        if (!getContext(servletContext).getResource(val).exists()) {
            val = attrs.defaultVal
        }
        out << val.encodeAsHTML()
    }

    private ApplicationContext getContext(ServletContext servletContext) {
        return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }
}
