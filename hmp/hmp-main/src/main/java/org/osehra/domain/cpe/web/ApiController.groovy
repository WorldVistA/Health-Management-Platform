package org.osehra.cpe.web

import groovy.text.XmlTemplateEngine
import groovy.util.slurpersupport.GPathResult
import java.util.Map.Entry
import org.codehaus.groovy.grails.commons.GrailsApplication

import org.osehra.cpe.vpr.IAppService;
import org.osehra.cpe.param.ParamService
import org.osehra.cpe.vpr.NotFoundException
import org.osehra.cpe.vpr.web.BadRequestException
import org.osehra.cpe.vpr.Allergy
import org.osehra.cpe.vpr.Immunization
import org.osehra.cpe.vpr.VitalSign
import org.osehra.cpe.vpr.Result
import org.osehra.cpe.vpr.Procedure
import org.osehra.cpe.vpr.Problem
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.Order
import org.osehra.cpe.vpr.Observation
import org.osehra.cpe.vpr.Medication
import org.osehra.cpe.vpr.HealthFactor
import org.osehra.cpe.vpr.Task

import org.osehra.cpe.vpr.Encounter
import org.osehra.cpe.vpr.Document
import org.codehaus.groovy.grails.web.converters.ConverterUtil

import grails.util.GrailsNameUtils

import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.RequestParam
import org.osehra.cpe.vpr.service.IPatientDomainService
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory
import org.osehra.cpe.jsonc.JsonCResponse
import org.osehra.cpe.jsonc.JsonCCollection
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.method.HandlerMethod
import org.springframework.core.MethodParameter
import org.springframework.web.servlet.mvc.condition.NameValueExpression
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.util.StringUtils
import org.springframework.web.util.UriTemplate

@Controller
@RequestMapping("/api/**")
class ApiController {

    @Autowired
    GrailsApplication grailsApplication

    @Autowired
    IPatientDomainService patientDomainService
	
	@Autowired
	ParamService paramService

	@Autowired
	IAppService appService

    @Autowired
    RequestMappingHandlerMapping handlerMapping

    @RequestMapping(method = RequestMethod.GET)
    ModelAndView index() {
        List<ApiDescriptor> apis = [
                new ApiDescriptor(name: 'vpr', title: "Patient Data API", description: "Read only access to patient data."),
                new ApiDescriptor(name: 'vista-rpc', title: "VistA Remote Procedure Call API", description: "Execute existing VistA remote procedure calls."),
                new ApiDescriptor(name: 'roster', title: "Roster API", description: "Foo", version: "prototype"),
                new ApiDescriptor(name: 'pref', title: "Preferences API", description: "Configure system and user preferences.  Similar to Parameters System (XPAR) in VistA.", version: "prototype"),
                new ApiDescriptor(name: 'gadget', title: "Gadget API", description: "Configure and register new user interface components.", version: "prototype"),
                new ApiDescriptor(name: 'order-entry', title: "Order Entry API", description: "Order menus and quick orders placement. Supports ordering user interfaces (UI).", version: "prototype"),
                new ApiDescriptor(name: 'tiu', title: "Documentation API", description: "Update progress notes and other clinical documentation.", version: "future"),
                new ApiDescriptor(name: 'esig', title: "Electronic Signature API", description: "Electronically sign documents and orders.", version: "future"),
                new ApiDescriptor(name: 'clio', title: "CliO API", description: "Add clinical observations to the patient record.", version: "future"),
                new ApiDescriptor(name: 'order-management', title: "Order Management API", description: "Update and validate orders.", version: "future"),
        ]

        return new ModelAndView("/api/index", [apis: apis.groupBy { it.version }, appService: appService, paramService: paramService])
    }

    // TODO: this is a candidate for the mvc:view-controller xml config
    @RequestMapping(value = "authentication", method = RequestMethod.GET)
    String authentication() {
        // falls through to view
        return "/api/authentication"
    }

    // TODO: this is a candidate for the mvc:view-controller xml config
    @RequestMapping(value = "dateRange", method = RequestMethod.GET)
    String dateRange() {
        // falls through to view
        return "/api/dateRange"
    }

    @RequestMapping(value = "resource", method = RequestMethod.GET)
    Map resource(@RequestParam String id) {
        GPathResult wadlXml = new XmlSlurper().parseText(getWadlText())
        def resourceElements = wadlXml.resources.depthFirst().findAll { it.name() == 'resource'}
        def resources = resourceElements.collect {new ResourceDescriptorOld(wadlXml, it)}
        ResourceDescriptorOld resourceDescriptor = resources.find { it.id == id }
        if (!resource) throw new NotFoundException("resource '${}' not found")

        Map mimeType2FormatParam = [:]
        Map mimeTypes = grailsApplication.config.grails.mime.types
        mimeTypes.entrySet().each { Entry entry ->
            if (entry.value instanceof Collection) {
                def vals = entry.value as Collection
                vals.each {
                    mimeType2FormatParam.put(it, entry.key)
                }
            } else {
                mimeType2FormatParam.put(entry.value, entry.key)
            }
        }

        return [resourceDescriptor: resourceDescriptor, mimeTypes: mimeType2FormatParam]
    }

    /**
     * Automatic listing of all @RequestMapping or WS endpoints.
     *
     * @return
     */
    @RequestMapping(value="endpoints", method=RequestMethod.GET)
    ModelAndView endpoints() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods()
        List<ResourceDescriptor> resources = [];
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods) {
            for (String pattern : entry.getKey().patternsCondition.patterns) {
                // TODO: set parameter type (xsd:string, xsd:int, hl7:datetime), etc.
                List<ParamDescriptor> params = []
                for (MethodParameter p: entry.getValue().getMethodParameters()) {
                    p.initParameterNameDiscovery()

                    ParamDescriptor param = null
                    if (p.hasParameterAnnotation(PathVariable.class)) {
                        PathVariable annotation = p.getParameterAnnotation(PathVariable.class);
                        RequestMapping requestMapping = entry.getValue().getMethodAnnotation(RequestMapping.class);
                        UriTemplate uriTemplate = new UriTemplate(pattern);
                        param = new ParamDescriptor(name: annotation.value() ?: uriTemplate.getVariableNames().get(p.getParameterIndex()), style: 'template', required: true)
                    } else if (p.hasParameterAnnotation(RequestParam.class)) {
                        RequestParam annotation = p.getParameterAnnotation(RequestParam.class)
                        // TODO: parameterName isn't set with @RequestParam default (method argument name is used), find out where Spring is looking that up
                        param = new ParamDescriptor(name: annotation.value() ?: p.parameterName, required: annotation.required())
                    }
                    if (param) params << param
                }
                // TODO: set return type content-type, etc.
                ResourceDescriptor resource = new ResourceDescriptor(path: pattern,
                        methods: entry.getKey().methodsCondition.methods.toList(),
                        params: params
                )
                resources << resource
            }
        }
        return ModelAndViewFactory.contentNegotiatingModelAndView(JsonCCollection.create(resources));
    }

//    def wadl = {
    //        withFormat {
    //            xml {
    //                render(text: getWadlText(), contentType: 'application/xml', encoding: 'UTF-8')
    //            }
    //        }
    //    }

    @RequestMapping(value = "vpr", method = RequestMethod.GET)
    Map vpr() {
        List<ResourceDescriptor> resources = getVprResourceDescriptors()
        def resourcesByType = resources.groupBy { it.type }
        return [resourcesByType: resourcesByType]
    }

    private List<ResourceDescriptor> getVprResourceDescriptors() {
        String apiName = "vpr"
        String apiVersion = "1"
        List resources = []

        List entityClasses = [
                Allergy,
                Document,
                Encounter,
                HealthFactor,
                Immunization,
                Medication,
                Observation,
                Order,
                Patient,
                Problem,
                Procedure,
                Result,
                Task,
                VitalSign
        ]
        entityClasses = entityClasses.collect { ConverterUtil.getDomainClass(it.name) }

        // individual domain resources
        entityClasses.each {
            String domain = GrailsNameUtils.getScriptName(it.clazz)
            def domainResourceDescriptor = new ResourceDescriptor(
                    apiName: apiName,
                    apiVersion: apiVersion,
                    type: "${it.name}",
                    domain: domain,
                    name: 'foo',
                    path: "/${apiName}/${apiVersion}/{pid}/${domain}/show/{uid}",
                    title: "This resource represents a single ${domain}.",
            )
            domainResourceDescriptor.params << new ParamDescriptor(name: 'uid', style: 'template', title: '')
            resources << domainResourceDescriptor

            def latestDomainResourceDescriptor = new ResourceDescriptor(
                    apiName: apiName,
                    apiVersion: apiVersion,
                    type: "${it.name}",
                    domain: domain,
                    name: 'Latest',
                    path: "/${apiName}/${apiVersion}/{pid}/${domain}/latest",
                    title: 'foo',
            )
            resources << latestDomainResourceDescriptor
        }

        // list resources
        Map queryNamesForClass = [:]
        entityClasses.each {
            queryNamesForClass[it] = ['all']
            queryNamesForClass[it].addAll(patientDomainService.getNamedQueries(it))
        }

        queryNamesForClass.each { entry ->
            entry.value.each { queryName ->
                String domain = GrailsNameUtils.getScriptName(entry.key.clazz)
                def domainListResourceDescriptor = new ResourceDescriptor(
                        apiName: apiName,
                        apiVersion: apiVersion,
                        type: "${entry.key.name} List",
                        domain: domain,
                        name: 'foo',
                        path: "/${apiName}/${apiVersion}/{pid}/${domain}/${queryName}",
                        title: 'foo',
                )
                domainListResourceDescriptor.params << new ParamDescriptor(
                        name: "dateRange",
                        title: "A date range with which to limit results returned.")
                domainListResourceDescriptor.params << new ParamDescriptor(
                        name: "count",
                        type: 'xsd:integer',
                        title: "The page size for a paged collection.",
                        description: 'Defaults to 1000.')
                domainListResourceDescriptor.params << new ParamDescriptor(
                        name: "startIndex",
                        type: 'xsd:integer',
                        title: "An integer specifying the starting point when paging through a list of items.",
                        description: "Defaults to 0.")
                resources << domainListResourceDescriptor
            }
        }
//        resources.addAll(PatientDomainController.DOMAIN_ALIASES.keySet())

        resources = resources.sort { it.type }
    }

    def entities = {
        // TODO: maybe centralize this list?
        List entityClasses = [
                Allergy,
                Document,
                Encounter,
                HealthFactor,
                Immunization,
                Medication,
                Observation,
                Order,
                Patient,
                Problem,
                Procedure,
                Result,
                Task,
                VitalSign
        ]
        entityClasses = entityClasses.collect { ConverterUtil.getDomainClass(it.name) }

        [entityClasses: entityClasses]
    }

    def schemata = {
        if (!params.domain) throw new BadRequestException("missing 'domain' parameter")


    }

    private String getWadlText() {
        XmlTemplateEngine engine = new XmlTemplateEngine()
        def binding = [serverURL: grailsApplication.config.grails.serverURL.toString()]
        def wadlTemplate = engine.createTemplate(new InputStreamReader(grailsApplication.mainContext.getResource("/WEB-INF/wadl/vpr.wadl").inputStream, 'UTF-8')).make(binding)
        return wadlTemplate.toString()
    }
}

class ApiDescriptor {
    String name
    String title
    String description
    String version = "0.7"
}

class ParamDescriptor {
    String name
    String type = "xsd:string"
    String style = "query"
    String title
    String description
    private boolean required = false
    List options

    void setStyle(String style) {
        this.style = style
        if (this.style == 'template') {
            this.required = true
        }
    }

    boolean isRequired() {
        return required
    }

    void setRequired(boolean required) {
        this.required = required
    }
}

class ResourceDescriptor {
    String apiName
    String apiVersion
    String id
    String type
    String domain
    String name
    String title
    String description
    String path
    List params = [
            new ParamDescriptor(name: 'pid', style: 'template', title: ''),
            new ParamDescriptor(name: 'format', style: 'query', title: 'the preferred response format')
    ]
    Map usageExamples = [:]
    List methods
}

class MethodDescriptor {
    String name = "GET"
}

@Deprecated
class ResourceDescriptorOld {

    final GPathResult root
    final GPathResult resourceElement

    private GPathResult resourcesElement
    private List parameters
    private List methods

    ResourceDescriptorOld(GPathResult root, GPathResult resourceElement) {
        this.root = root
        this.resourceElement = resourceElement
    }

    String getId() {
        resourceElement.@id.toString() ?: null
    }

    String getName() {
        resourceElement.doc?.@title.toString() ?: null
    }

    String getDescription() {
        def desc = resourceElement.doc.depthFirst().find { it.'@class' == 'description'}
        if (desc) return desc.text()

        resourceElement.doc.text() ?: null
    }

    String getPath() {
        def pathElements = resourceChain.collect { it.@path }
        return getResourcesElement().@base.toString() + pathElements.join('/')
    }

    List getOptionalPaths() {
        []
    }

    List getParams() {
        if (parameters) return parameters

        List params = []
        resourceChain.each { r ->
            def parentParams = r.children().findAll { it.name() == 'param' }
            parentParams.each { params.add(it) }
        }
        getTypeDefinitionElements().each { typeDefinition ->
            def typeDefParams = typeDefinition.depthFirst().findAll { it.name() == 'param' }
            typeDefParams.each { params.add(it) }
        }
        return parameters = params
    }

    List getMethods() {
        if (this.methods) return methods

        List methods = []
        def childMethods = resourceElement.children().findAll { it.name() == 'method' }
        childMethods.each { m ->
            if (m.@href.toString()) {
                String methodId = m.@href.toString().substring(1)
                methods.add(getMethodElement(methodId))
            } else {
                methods.add(m)
            }
        }
        getTypeDefinitionElements().each { typeDefinition ->
            def typeDefMethods = typeDefinition.children().findAll { it.name() == 'method' }
            typeDefMethods.each { m ->
                if (m.@href.toString()) {
                    String methodId = m.@href.toString().substring(1)
                    methods.add(getMethodElement(methodId))
                } else {
                    methods.add(m)
                }
            }
        }
        return this.methods = methods
    }

    List getRepresentations() {
        def representations = []
        getMethods().each { m ->
            List responses = m.children().findAll {r -> r.name() == 'response' && r.@status == '200' }.list()
            responses.each { r ->
                List reps = r.children().findAll { it.name() == 'representation'}.list()
                representations.addAll(reps)
            }
        }
        return representations
    }

    List getUsageExamples() {
        def usageElement = resourceElement.doc.depthFirst().find { it.'@class' == 'usageExamples'}
        if (!usageElement) return null
        def trList = usageElement.tr.list()
        List usages = trList.collect { it ->
            [example: it.td[0].text().trim(), description: it.td[1].text().trim()]
        }
        return usages
    }

    private def getMethodElement(String id) {
        root.method.find { it.@id == id }
    }

    private List getTypeDefinitionElements() {
        if (!resourceElement.@setStyle) return []

        List typeIds = resourceElement.@setStyle.toString().split().toList().collect { it.startsWith('#') ? it.substring(1) : it }
        def typeDefinitions = root.'resource_type'.findAll { typeIds.contains(it.@id) }
        return typeDefinitions.list()
    }

    private def getResourcesElement() {
        if (resourcesElement) return resourcesElement

        def e = resourceElement
        while (!e.name().equals('resources')) {
            e = e.parent()
        }
        return resourcesElement = e
    }

    private List getResourceChain() {
        List chain = []
        def e = resourceElement
        while (e.name().equals('resource')) {
            chain.add(0, e)
            e = e.parent()
        }
        return chain
    }
}
