package org.osehra.cpe.vpr.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.osehra.cpe.auth.UserContext
import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.jsonc.JsonCResponse
import org.osehra.cpe.param.ParamService
import org.osehra.cpe.vista.rpc.RpcTemplate
import org.osehra.cpe.vista.util.VistaStringUtils
import org.osehra.cpe.vpr.AppService
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.Task
import org.osehra.cpe.vpr.PatientFacility
import org.osehra.cpe.vpr.Treatment
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.pom.POMUtils
import org.osehra.cpe.vpr.pom.jds.JdsGenericPatientObjectDAO
import org.osehra.cpe.vpr.sync.vista.IVistaVprPatientObjectDao
import org.osehra.cpe.vpr.vistasvc.CacheMgr
import org.osehra.cpe.vpr.vistasvc.CacheMgr.CacheType
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.osehra.cpe.vpr.UserInterfaceRpcConstants.VPR_UI_CONTEXT
import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView
import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.stringModelAndView

@RequestMapping(value = ["/chart/**", "/vpr/chart/**"])
@Controller
public class ChartController {

    @Autowired
    AppService appService

    @Autowired
    ParamService paramService

    @Deprecated
    @Autowired
    RpcTemplate rpcTemplate

    @Autowired
    IPatientDAO patientDao

    @Autowired
    UserContext userContext

    @Autowired
    IGenericPatientObjectDAO genericJdsDAO

    @Autowired
    IVistaVprPatientObjectDao vprPatientObjectDao

    @RequestMapping(value = "patientChecks", method = RequestMethod.GET)
    ModelAndView patientChecks(@RequestParam String pid, HttpServletResponse response) {
        String rpcResult = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPRFPTC CHKS", pid)
        return stringModelAndView(rpcResult, "application/json");
    }

    @RequestMapping(value = "/orderingControl", method = RequestMethod.GET)
    ModelAndView orderingControl(HttpServletRequest request) {
//        def duz = getDuz()
        def params = [:]
        params["command"] = request.getParameterMap().get("command")[0].toString()
        if (request.getParameterMap().containsKey("uid")) params["uid"] = request.getParameterMap().get("uid")[0].toString()
        if (request.getParameterMap().containsKey("patient")) params["patient"] = request.getParameterMap().get("patient")[0].toString()
        if (request.getParameterMap().containsKey("snippet")) params["snippet"] = request.getParameterMap().get("snippet")[0].toString()
        if (request.getParameterMap().containsKey("name")) params["name"] = request.getParameterMap().get("name")[0].toString()
        params["location"] = '240'
        params["provider"] = '1089'
        params["panelNumber"] = '1'
//        params["patient"] = '10103'
//        request.getParameterMap().location = '240'
//        request.getParameterMap().provider = '1089'
//        request.getParameterMap().panelNumber = '1'
        //params.command = "listQuickOrders"
        String rpcResult = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPRCORD RPC", [params])
        return stringModelAndView(rpcResult, "application/json");
    }

    @RequestMapping(value = "/getReminderList", method = RequestMethod.GET)
    ModelAndView getReminderList(HttpServletRequest request) {
        def params = [:]
        params["command"] = "getReminderList"
//        params["user"] = userContext.currentUser.getUid()
        params["location"] = ""
        String rpcResult = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPRCRPC RPC", [params])
        return stringModelAndView(rpcResult, "application/json");

    }

    @RequestMapping(value = "/evaluateReminder", method = RequestMethod.GET)
    ModelAndView evaluateReminder(HttpServletRequest request) {
		
        def params = [:]
   		String dfn = null;
        String patientId
        params["command"] = "evaluateReminder"
        if (request.getParameterMap().containsKey("uid")) params["uid"] = request.getParameterMap().get("uid")[0].toString()
        if (request.getParameterMap().containsKey("patientId")) patientId = request.getParameterMap().get("patientId")[0].toString()
		if (request.getParameterMap().containsKey("dfn")) dfn = request.getParameterMap().get("dfn")[0].toString()

		if (dfn == null) {
		Patient pt = patientDao.findByVprPid(patientId)
		if(pt!=null) {
			for(PatientFacility fc: pt.getFacilities()) {
				if(fc.code.equals(temp["facilityCode"])) {
					dfn = fc.localPatientId;
				}
			}
        }
		}
        params["patientId"] = dfn
        String rpcResult = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPRCRPC RPC", [params])
        return stringModelAndView(rpcResult, "application/json");

    }

    @RequestMapping(value = "/orderingControl", method = RequestMethod.POST)
    ModelAndView postOrderingControl(HttpServletRequest request) {
//        def duz = getDuz()
        def params = [:]
        params["command"] = request.getParameterMap().get("command")[0].toString()
        if (request.getParameterMap().containsKey("uid")) params["uid"] = request.getParameterMap().get("uid")[0].toString()
        if (request.getParameterMap().containsKey("patient")) params["patient"] = request.getParameterMap().get("patient")[0].toString()
        if (request.getParameterMap().containsKey("snippet")) params["snippet"] = request.getParameterMap().get("snippet")[0].toString()
        if (request.getParameterMap().containsKey("name")) params["name"] = request.getParameterMap().get("name")[0].toString()
        if (request.getParameterMap().containsKey("qoIen")) params["qoIen"] = request.getParameterMap().get("qoIen")[0].toString()
        if (request.getParameterMap().containsKey("orderAction")) params["orderAction"] = request.getParameterMap().get("orderAction")[0].toString()
//        if (request.getParameterMap().containsKey("name")) params["name"] = request.getParameterMap().get("name")[0].toString()
        params["location"] = '240'
        params["provider"] = '1089'
        params["panelNumber"] = '1'
//        params["patient"] = '10103'
//        request.getParameterMap().location = '240'
//        request.getParameterMap().provider = '1089'
//        request.getParameterMap().panelNumber = '1'
        //params.command = "listQuickOrders"
        String rpcResult = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPRCORD RPC", [params])
        return stringModelAndView(rpcResult, "application/json");

    }
	
	@RequestMapping(value = "/addTreatment", method = RequestMethod.POST)
	ModelAndView postAddTreatment(
		@RequestParam(required=true) String patientId,
		@RequestParam(required=true) String description, 
		@RequestParam(required=true) String dateTime, 
		HttpServletRequest request) {

		def temp = [:]
		temp["description"] = description;
		temp["dateTime"] = PointInTime.fromDateFields(new SimpleDateFormat("MM/dd/yyyy").parse(dateTime));
			
		temp["facilityCode"] = userContext.currentUser.getDivision()
		temp["facilityName"] = userContext.currentUser.getDivisionName()
		
		String dfn = null;
		
		String rpcResult = "";
		
		Patient pt = patientDao.findByVprPid(patientId)
		if(pt!=null) {
			dfn = pt.getLocalPatientIdForFacility(temp["facilityCode"])
			
			if(dfn!=null) {
				String json = POMUtils.toJSON(temp)
				def value = VistaStringUtils.splitLargeStringIfNecessary(json)
				System.out.println(value)
				rpcResult = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPR PUT PATIENT DATA", dfn, "treatment", value)
				System.out.println(rpcResult)
				ObjectMapper jsonMapper = new ObjectMapper();
				JsonNode result = jsonMapper.readValue(rpcResult, JsonNode.class)
				if (result.path("success")) {
					JsonNode dataNode = result.path("data")
					String uid = dataNode.path("uid").textValue()
					def vistaSysId = userContext.currentUser.vistaId

					temp["pid"] = pt.pid
					temp["uid"] = uid
					Treatment treatment = new Treatment();
					treatment.setData(temp)
		
					genericJdsDAO.save(treatment)
				}
			}
		} else {
			throw new PatientNotFoundException(patientId)
		}
		return stringModelAndView(rpcResult, "application/json");
	}
	

    @RequestMapping(value = "/addTask", method = RequestMethod.POST)
    ModelAndView postAddTask(@RequestParam String patientId,
                             @RequestParam String taskName,
                             @RequestParam String type,
                             @RequestParam String description,
                             @RequestParam PointInTime dueDate,
                             @RequestParam(required = false) Boolean completed,
                             HttpServletRequest request) {
        Map<String, Object> taskVals = [
                pid: patientId,
                taskName: taskName,
                description: description,
                type: type,
                dueDate: dueDate,
                completed: completed,
                ownerName: userContext.currentUser.displayName,
                ownerCode: userContext.currentUser.uid,
                assignToName: userContext.currentUser.displayName,
                assignToCode: userContext.currentUser.uid,
                facilityCode: userContext.currentUser.division,
                facilityName: userContext.currentUser.divisionName
        ]

        Task task = vprPatientObjectDao.save(Task.class, taskVals);
		return contentNegotiatingModelAndView(JsonCResponse.create(request, task))
    }

    @RequestMapping(value = "patientSecurityLog", method = RequestMethod.GET)
    ModelAndView patientSecurityLog(@RequestParam String pid) {
        Patient pt = patientDao.findByAnyPid(pid)

        // throw 404 error if patient not found
        if (!pt) {
            throw new PatientNotFoundException(pid)
        }

        String dfn = pt.getLocalPatientIdForSystem(userContext.currentUser?.vistaId)

        Map params = [:]
        params["command"] = "logPatientAccess"
        params["patientId"] = dfn
        String rpcResult = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPRCRPC RPC", [params])
        return stringModelAndView(rpcResult, "application/json");
    }

    @RequestMapping(value = "/getPatientInfo", method = RequestMethod.GET)
    ModelAndView getPatientInfo(@RequestParam(required = true) String pid) {
        Patient pt = patientDao.findByAnyPid(pid)
        if (!pt) throw new PatientNotFoundException(pid)

        String patId = pt.getLocalPatientIdForSystem((userContext.currentUser?.vistaId))

        def data = CacheMgr.fetch("PATIENT_DEMOGRAPHICS", patId, CacheType.MEMORY);
        if (!data) {
            Map params = ["command": "getPatientInfo", "patientId": patId]
            data = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPRCRPC RPC", [params])
            CacheMgr.store("PATIENT_DEMOGRAPHICS", patId, data, CacheType.MEMORY);
        }

        if (data) {
            return stringModelAndView(data, "application/json");
        }
        throw new PatientNotFoundException(pid)
    }

    @RequestMapping(value = "/getPatientInfoDetails", method = RequestMethod.GET)
    ModelAndView getPatientInfoDetails(@RequestParam(required = true) String pid,
                                       @RequestParam(required = true) String domain) {
        // assumes cache has been loaded by /getPatientInfo
        Patient pt = patientDao.findByAnyPid(pid)
        String patId = pt.getLocalPatientIdForSystem((userContext.currentUser?.vistaId))
        def data = CacheMgr.fetch("PATIENT_DEMOGRAPHICS", patId, CacheType.MEMORY);
        def item = POMUtils.parseJSONtoMap(data);
        def body = ''
        if (domain == 'patientDemDetails') body = item?.patDemDetails?.text
        else if (domain == 'patientTeamDetails') body = item?.teamInfo?.text
        return new ModelAndView('/patientDomain/' + domain, [patient: pt, item: item, body: body])
    }
}
