package EXT.DOMAIN.cpe.vpr.web

import EXT.DOMAIN.cpe.jsonc.JsonCCollection
import EXT.DOMAIN.cpe.jsonc.JsonCResponse
import EXT.DOMAIN.cpe.param.ParamService
import EXT.DOMAIN.cpe.vpr.dao.ISyncErrorDao
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO

import grails.util.GrailsNameUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

import EXT.DOMAIN.cpe.vpr.*

import static org.springframework.web.bind.annotation.RequestMethod.GET
import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView

@Controller
class PatientController {

    @Autowired
    LinkService linkService

    @Autowired
    FeedService feedService

    @Autowired
    IPatientDAO patientDao

    @Autowired
    IGenericPatientObjectDAO patientRelatedDao

    @Autowired
    ISyncErrorDao syncErrorDao
	
	@Autowired
	ParamService paramService

	@Autowired
	IAppService appService

    @RequestMapping(value = ["/patient/list", "/patient/index"], method = GET)
    ModelAndView list(Pageable pageable, @RequestParam(required = false) String format, HttpServletRequest request) {
        Page<Patient> page = patientDao.findAll(pageable);

        JsonCCollection cr = JsonCCollection.create(page)
        cr.setSelfLink(request.requestURI)

        List<Class> domainClasses = VprConstants.PATIENT_RELATED_DOMAIN_CLASSES;
        Map countsByPatient = [:]
        Map errorsByPatient = [:]
        for (Patient pt: page.content) {
            countsByPatient[pt] = ["Total": 0, "Errors": 0]
            for (Class domainClass: domainClasses) {
				int countForDomainClass = 0;
				try{
					countForDomainClass = patientRelatedDao.countByPID(domainClass, pt.getPid())
				}catch(IllegalArgumentException iae){ // not all classes mapped to domains
					continue;
				}
                countsByPatient[pt][GrailsNameUtils.getShortName(domainClass)] = countForDomainClass
                countsByPatient[pt]["Total"] += countForDomainClass
            }
            int errorCount = syncErrorDao.countByPatientId(pt.pid)
            countsByPatient[pt]["Errors"] = errorCount
            errorsByPatient[pt] = (errorCount > 0)
        }

        if (!format || format == "html") {
            return new ModelAndView("/patient/list", [ appService: appService, paramService: paramService,
					patientInstanceList: cr.items,
                    max: cr.itemsPerPage,
                    offset: cr.startIndex,
                    patientInstanceTotal: cr.totalItems,
                    countedDomainClasses: domainClasses.collect {GrailsNameUtils.getShortName(it)}])
        } else {
            return contentNegotiatingModelAndView(cr)
        }
    }

    // TODO: do this with a ViewDef
    @RequestMapping(value = "/patient/summary", method = GET)
    ModelAndView summary(Pageable pageable, HttpServletRequest request) {
        Page<Patient> ptList = patientDao.findAll(pageable)
        def itemList = []
        List<Class> domainClasses = VprConstants.PATIENT_RELATED_DOMAIN_CLASSES;
        Map countsByPatient = [:]
        Map errorsByPatient = [:]

        ptList.each {Patient pt ->
            int totalItemCount = 0

            countsByPatient[pt] = [:]
            //domainClasses.each { Class domainClass ->
			for (Class domainClass: domainClasses) {
				int countForDomainClass = 0
				try{
					countForDomainClass = patientRelatedDao.countByPID(domainClass, pt.getPid())
				}catch(IllegalArgumentException iae){ // not all classes mapped to domains
					continue;
				}

                countsByPatient[pt][GrailsNameUtils.getShortName(domainClass)] = countForDomainClass
                totalItemCount += countForDomainClass
            }
            int errorCount = syncErrorDao.countByPatientId(pt.pid)
            errorsByPatient[pt] = (errorCount > 0)

            itemList << [patient: pt, itemCount: totalItemCount, counts: countsByPatient[pt], totalErrorCount: errorCount, errors: errorsByPatient[pt]]
        }

        JsonCCollection cr = JsonCCollection.create(itemList)
        cr.setSelfLink(request.requestURI);

        return contentNegotiatingModelAndView(cr);
    }

    @RequestMapping(value = "/vpr/{apiVersion}/{pid}", method = GET)
    ModelAndView vprShow(@PathVariable String apiVersion, @PathVariable String pid) {
        Patient pt = getPatient(pid);
        return contentNegotiatingModelAndView(JsonCResponse.create(pt));
    }

    private Patient getPatient(String pid) {
        Patient pt = patientDao.findByAnyPid(pid)
        if (!pt) {
            throw new PatientNotFoundException(pid)
        }
        return pt;
    }

    // TODO: consider moving this to SyncController?
    @RequestMapping(value = "/patient/syncErrors", method = GET)
    ModelAndView syncErrors(@RequestParam String pid, @RequestParam(required = false) String format, Pageable pageable) {
        Patient pt = patientDao.findByAnyPid(pid)
        if (!pt) {
            throw new PatientNotFoundException(pid)
        }
        if (!format || format == "html") {
            return new ModelAndView("/patient/syncErrors", [patient: pt]);
        } else {
            return contentNegotiatingModelAndView(getSyncErrorsForPatient(pt.getPid(), pageable));
        }
    }

    private JsonCCollection getSyncErrorsForPatient(String pid, Pageable pageable) {
        Page<SyncError> syncErrors = syncErrorDao.findAllByPatientId(pid, new PageRequest(pageable.pageNumber, pageable.pageSize, Direction.DESC, "dateCreated"))
        JsonCCollection response = JsonCCollection.create(syncErrors)
        List<Map> patientSyncErrors = response.items.collect { SyncError error ->
            [patient: error.patient, id: error.id, item: error.item, dateCreated: error.dateCreated, json: error.json, message: error.message, stackTrace: error.stackTrace]
        }
        response.data.items = patientSyncErrors
        return response
    }

    @RequestMapping(value = "/patient/menu/{id}", method = GET)
    ModelAndView menu(@PathVariable String id) {
        Patient pt = patientDao.findByAnyPid(id)
        if (!pt) {
            throw new PatientNotFoundException(id)
        } else {
            return new ModelAndView("/patient/menu", [patientInstance: pt, appService: appService, paramService: paramService])
        }
    }
}
