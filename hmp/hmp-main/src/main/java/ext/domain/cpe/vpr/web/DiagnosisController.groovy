package EXT.DOMAIN.cpe.vpr.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import EXT.DOMAIN.cpe.auth.UserContext;
import EXT.DOMAIN.cpe.vista.rpc.RpcTemplate;
import EXT.DOMAIN.cpe.vpr.Diagnosis
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO;
import EXT.DOMAIN.cpe.vpr.pom.POMUtils
import EXT.DOMAIN.cpe.vpr.pom.jds.JdsGenericPatientObjectDAO;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.*
import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.stringModelAndView
import static EXT.DOMAIN.cpe.vpr.UserInterfaceRpcConstants.VPR_UI_CONTEXT

@RequestMapping(value = ["/diagnosis/**", "/vpr/diagnosis/**"])
@Controller
class DiagnosisController {

    @Autowired
    RpcTemplate rpcTemplate

    @Autowired
    IPatientDAO patientDao

    @Autowired UserContext userContext

    @Autowired JdsGenericPatientObjectDAO genericJdsDAO

	@RequestMapping(value = "/submitDiagnosis", method = RequestMethod.POST)
	ModelAndView postAddTask(HttpServletRequest request) {

		def temp = [:]
		String pid
		String uid
		String newvalue

		if (request.getParameterMap().containsKey("pid")) pid = request.getParameterMap().get("pid")[0].toString()
		if (request.getParameterMap().containsKey("uid")) uid = request.getParameterMap().get("uid")[0].toString()
		if (request.getParameterMap().containsKey("value")) newvalue = request.getParameterMap().get("value")[0].toString()
		
		temp["pid"] = pid;
		temp["uid"] = uid;
		temp["diagnosis"] = newvalue;
		temp["ownerName"] = userContext.currentUser.getDisplayName()
		temp["ownerCode"] = userContext.currentUser.getUid()
		temp["assignToName"] = userContext.currentUser.getDisplayName()
		temp["assignToCode"] = userContext.currentUser.getUid()
		temp["facilityCode"] = userContext.currentUser.getDivision()
		temp["facilityName"] = userContext.currentUser.getDivisionName()
		
		String dfn = null;
		
		String rpcResult = "";
		
		Patient pt = patientDao.findByVprPid(pid)
		if(pt!=null) {
			dfn = pt.getLocalPatientIdForFacility(temp["facilityCode"])
			
			if(dfn!=null) {
				String json = POMUtils.toJSON(temp)
				def value = splitLargeStringIfNecessary(json,245)
				System.out.println(value)
				rpcResult = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPR PUT PATIENT DATA", dfn, "diagnosis", value)
				System.out.println(rpcResult)
				ObjectMapper jsonMapper = new ObjectMapper();
				JsonNode result = jsonMapper.readValue(rpcResult, JsonNode.class)
				if (result.path("success")) {
					JsonNode dataNode = result.path("data")
					uid = dataNode.path("uid").textValue()
					def vistaSysId = userContext.currentUser.vistaId
					//Patient pt = patientDao.findByLocalID(vistaSysId, patientId)
					temp["pid"] = pt.pid
					temp["uid"] = uid
					Diagnosis diagnosis = new Diagnosis();
					diagnosis.setData(temp);
					genericJdsDAO.save(diagnosis);
				}
			}
		} else {
			throw new PatientNotFoundException(patientId)
		}
		return stringModelAndView(rpcResult, "application/json");
	}

    protected static Object splitLargeStringIfNecessary(String value, int len) {
        if (value == null) {return "";}
        else if (value.length() <= len) {return value;}
        else {
            List<String> ret = new ArrayList<String>();
            while (value.length() > len) {
                ret.add(value.substring(0, len));
                value = value.substring(len);
            }
            if (value.length() > 0) {
                ret.add(value);
            }
            return ret;
        }
    }
}
