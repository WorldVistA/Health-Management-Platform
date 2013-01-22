package EXT.DOMAIN.cpe.vpr.web

import EXT.DOMAIN.cpe.auth.UserContext
import EXT.DOMAIN.cpe.vpr.LastViewed

import EXT.DOMAIN.cpe.vpr.UidUtils
import EXT.DOMAIN.cpe.vpr.dao.ILastViewedDao
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRunner
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Controller
import org.springframework.util.ClassUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import EXT.DOMAIN.cpe.vpr.pom.POMUtils
import com.fasterxml.jackson.databind.JsonNode

@Controller
class DetailController {

    @Autowired
    IGenericPatientObjectDAO patientRelatedDao
	
	@Autowired
	IPatientDAO patientDao

    @Autowired
    ILastViewedDao lastViewedDao

    @Autowired
    UserContext userContext
	
	@Autowired
	ApplicationContext ctx
	
	@Autowired
	FrameRunner runner

    @RequestMapping(value = ["/detail/**", "/vpr/detail/**"], method = RequestMethod.GET)
    ModelAndView renderDetail(HttpServletRequest request, HttpServletResponse response) {
        String uri = URLDecoder.decode(request.getRequestURI());
        String uid = (uri.indexOf("urn:")>-1?uri.substring(uri.indexOf("urn:")):null);
        if (!uid) {
			Map<String, Object> err = new HashMap<String, Object>();
			err.put("message", "Unique ID not found; Is this a summary view?");
			return new ModelAndView('/exception/detailNotFound', [error: err]);
            //throw new BadRequestException("'uid' parameter is required");
        }

        Class domainClass = UidUtils.getDomainClassByUid(uid);
        if (!domainClass)
            throw new BadRequestException("unknown domain class for " + uid)

        AbstractPOMObject item = patientRelatedDao.findByUID(domainClass, uid)
        if (!item)
            throw new UidNotFoundException(domainClass, uid)

		// Since this is a detail service, let's assume we need all linked objects inflated.
		item.loadLinkData(patientRelatedDao);
		
        String domain = ClassUtils.getShortNameAsProperty(item.class)

        LastViewed lv = lastViewedDao.findByUidAndUserId(uid, userContext.getCurrentUser().uid)
        if (lv == null) {
            lv = new LastViewed();
            lv.uid = uid;
            lv.userId = userContext.getCurrentUser().uid;
            lastViewedDao.save(lv);
        }
		
        return new ModelAndView('/patientDomain/' + domain, [item: item, ctx: ctx, runner: runner])
    }

    @RequestMapping(value = ["/medtabdetail", "/vpr/detail/medtabdetail"], method = RequestMethod.GET)
    ModelAndView renderMedTabDetail(HttpServletRequest request, HttpServletResponse response) {
        String uri = URLDecoder.decode(request.getRequestURI());
        String params = request.parameterMap.get("history").toString()
        JsonNode tmp = POMUtils.parseJSONtoNode(params)
        JsonNode value = tmp.path(0);
        ArrayList<AbstractPOMObject> item = new ArrayList<AbstractPOMObject>();
        for (int i = 0; i < value.size(); i++) {
            String uid = value.path(i).path("uid").textValue();
            String start = value.path(i).path("overallStart").textValue();
            String stop = value.path(i).path("overallStop").textValue();
            String status = value.path(i).path("vaStatus").textValue();
            String dose = value.path(i).path("dose").textValue();
            String anchor = dose + ' ' + status + ' ' + start + ' ' + stop
            if (!uid) {
			    Map<String, Object> err = new HashMap<String, Object>();
			    err.put("message", "Unique ID not found; Is this a summary view?");
			    return new ModelAndView('/exception/detailNotFound', [error: err]);
                //throw new BadRequestException("'uid' parameter is required");
            }
            Class domainClass = UidUtils.getDomainClassByUid(uid);
            if (!domainClass)
            throw new BadRequestException("unknown domain class for " + uid)

            AbstractPOMObject temp = patientRelatedDao.findByUID(domainClass, uid)
            if (!temp) throw new UidNotFoundException(domainClass, uid)
            temp.setData("anchorLink", anchor)
            item.add(temp)

        }

        return new ModelAndView('/patientDomain/medicationhistory', [items: item])
    }
}
