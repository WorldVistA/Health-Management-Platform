package org.osehra.cpe.vpr.web

import org.osehra.cpe.auth.UserContext
import org.osehra.cpe.datetime.IntervalOfTime
import org.osehra.cpe.jsonc.JsonCCollection
import org.osehra.cpe.jsonc.JsonCResponse
import org.osehra.cpe.vpr.LastViewed
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.dao.ILastViewedDao
import org.osehra.cpe.vpr.pom.AbstractPOMObject
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.service.IPatientDomainService
import org.osehra.cpe.vpr.service.PatientDomainService

import grails.validation.ValidationException
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView

/**
 * Handles nearly all VPR web service requests for individual entities in a domain as well as collections of entities in a domain.
 * <p />
 * Individual entities are fetched by default with:
 * <code>/{apiVersion}/{pid}/{domain}/show/{uid}</code>
 * <p />
 * Collections of entities are fetched by default with:
 * <code>/{apiVersion}/{pid}/{domain}/{queryName}</code>
 * <p />
 */
@Controller
class PatientDomainController {

    @Autowired
    IPatientDomainService patientDomainService

    @Autowired
    IPatientDAO patientDao

	@Autowired
	ILastViewedDao lastViewedDao

    @Autowired
    IGenericPatientObjectDAO genericPatientRelatedDao;
	
	@Autowired
	UserContext userContext

    private static Logger log = LoggerFactory.getLogger(PatientDomainController);

    /**
     * The show action retreives an individual VPR entity and returns the requested representation in JSON or XML.
     * <p />
     * Required request parameters:
     * <dl>
     *     <dt>pid</dt>
     *     <dd>Patient identifier.</dd>
     *     <dt>domain</dt>
     *     <dd>The domain of the VPR entity.</dd>
     *     <dt>uid</dt>
     *     <dd>Unique id of a VPR entity.</dd>
     * </dl>
     *
     * @param ptCmd command object that is bound to the required <code>pid</code> and <code>domain</code> request parameters.
     * @param itemCmd command object that is bound to the required <code>uid</code> request parameter.
     *
     * @throws ValidationException
     * @throws PatientNotFoundException
     * @throws UnknownDomainException
     * @throws UidNotFoundException
     * @throws BadRequestException
     *
     * @see <a href="http://www.grails.org/doc/latest/guide/single.html#6.8%20Content%20Negotiation">Grails Content Negotiation</a>
     */
    @RequestMapping(value = "/vpr/{apiVersion}/{pid}/{domain}/show/**", method = RequestMethod.GET)
    ModelAndView show(@PathVariable String apiVersion,
                      @PathVariable String pid,
                      @PathVariable String domain,
                      @RequestParam(required = false) String format,
					  HttpServletRequest request) {
	    String uri = URLDecoder.decode(request.getRequestURI());
	    String uid = uri.substring(uri.indexOf("urn:"));
	    if (!uid) {
		  throw new BadRequestException("'uid' parameter is required");
	    }
			  
		Patient pt = getPatient(pid); // validate patient
        Class domainClass = patientDomainService.getDomainClass(domain)

        def item = genericPatientRelatedDao.findByUID(domainClass, uid);  // a justified use of groovy 'def'! ;)
        if (!item) {
            throw new UidNotFoundException(domainClass, uid)
        }
		
		if(item instanceof AbstractPOMObject)
		{
			((AbstractPOMObject)item).loadLinkData(genericPatientRelatedDao);
		}
		/*
		 * This piece added to register each detail item as viewed as the authenticated user views it.
		 * TODO: Need a "Mark All Viewed" somehow, perhaps by domain, perhaps globally.
		 */
		LastViewed lv = lastViewedDao.findByUidAndUserId(uid, userContext.getCurrentUser().uid)
		if(lv == null)
		{
			lv = new LastViewed();
			lv.uid = uid;
			lv.userId = userContext.getCurrentUser().uid;
			lastViewedDao.save(lv);
		}
        if (format == "html") {
            return new ModelAndView('/patientDomain/' + domain, [patient: pt, item: item])
        } else {
            return contentNegotiatingModelAndView(JsonCResponse.create(item))
        }
    }

    @RequestMapping(value = "/vpr/{apiVersion}/{pid}/{domain}/latest", method = RequestMethod.GET)
    ModelAndView latest(@PathVariable String apiVersion,
                        @PathVariable String pid,
                        @PathVariable String domain) {
        Patient pt = getPatient(pid); // validate patient
        Class domainClass = patientDomainService.getDomainClass(domain)

        def latest = genericPatientRelatedDao.findLatestByPatient(domainClass, pt);

        return contentNegotiatingModelAndView(JsonCResponse.create(latest))
    }

    /**
     *  The list action retrieves a filtered list of VPR entities and returns them in pages.
     *  <p />
     *   Required request parameters:
     * <dl>
     *     <dt>pid</dt>
     *     <dd>Patient identifier.</dd>
     *
     *     <dt>domain</dt>
     *     <dd>The domain of the VPR entity.  Valid values for the <code>domain</code> request parameter are:
     *          <ol>
     *              <li>The 'logical property name' of the Grails Domain Class.  For example, the allergy <code>Allergy</code> class' logical property name is <code>allergy<code></li>
     *              <li>The 'lower case hyphenated name' of the Grails Domain Class.  For example, the allergy <code>HealthFactor</code> class' lower case hyphenated name is <code>health-factor<code></li>
     *              <li>A value listed in the DOMAIN_ALIASES Map defined in this class.  For example, the <code>lab</code> is an alias for <code>result<code></li>
     *          </ol>
     *     </dd>
     *
     *     <dt>queryName</dt>
     *     <dd>TBD</dd>
     * </dl>
     * Optional request parameters:
     * <dl>
     *     <dt>dateRange</dt>
     *     <dd>A date range to restrict the list of items to.</dd>
     *
     *     <dt>startIndex</dt>
     *     <dd>TBD</dd>
     *     <dt>count</dt>
     *     <dd>BD</dd>
     * </dl>
     */
    @RequestMapping(value = "/vpr/{apiVersion}/{pid}/{domain}/{queryName}", method = RequestMethod.GET)
    ModelAndView list(@PathVariable String apiVersion,
                      @PathVariable String pid,
                      @PathVariable String domain,
                      @PathVariable String queryName,
                      @RequestParam(required = false) IntervalOfTime dateRange,
                      @RequestParam(required = false) String format,
                      Pageable pageable,
                      HttpServletRequest request) {
        Patient pt = getPatient(pid) // validate patient

        GrailsParameterMap params = new GrailsParameterMap(request)
        Page page = patientDomainService.queryForPage(pt, domain, dateRange, getRequestedQueryNames(domain, params), getRemainingRequestParams(params), pageable)

        JsonCCollection jsonc = JsonCCollection.create(page)
        // TODO: add self, next and previous links to the collection

        return contentNegotiatingModelAndView(jsonc)
    }

    private Map getRemainingRequestParams(GrailsParameterMap params) {
        List<String> skip = ['format', 'controller', 'action', 'pid', 'domain', 'dateRange', 'queryName', 'max', 'offset', 'start', 'page', 'limit', 'startIndex', 'count', '_dc']
        Set<String> remainingKeys = new HashSet<String>();
        for (String key : params.keySet()) {
            if (!skip.contains(key)) {
                remainingKeys.add(key);
            }
        }
        Map remainingRequestParams = params.subMap(remainingKeys)
        return remainingRequestParams
    }

    private Set<String> getRequestedQueryNames(String domain, GrailsParameterMap params) {
        Set<String> queryNames = new HashSet(params.list('queryName') ?: ['all'])
        // adjust included query names based on aliases
        if (PatientDomainService.DOMAIN_ALIASES.containsKey(domain)) {
            Map alias = PatientDomainService.DOMAIN_ALIASES[domain] as Map
            if (alias.queryName) {
                queryNames << alias.queryName
            }
        }
        return queryNames
    }

    private Patient getPatient(String pid) {
        Patient pt = patientDao.findByAnyPid(pid)
        if (!pt) throw new PatientNotFoundException(pid)
        return pt
    }
}
