package org.osehra.cpe.vpr.web

import static org.osehra.cpe.vpr.UserInterfaceRpcConstants.CONTROLLER_RPC_URI
import org.osehra.cpe.auth.UserContext
import org.osehra.cpe.param.ParamService
import org.osehra.cpe.vista.rpc.RpcOperations
import org.osehra.cpe.vpr.pom.POMUtils
import org.osehra.cpe.vpr.vistasvc.CacheMgr
import org.osehra.cpe.vpr.vistasvc.CacheMgr.CacheType

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@RequestMapping("/param")
@Controller
public class ParamController {
	
	@Autowired
	ParamService svc;
	
	@RequestMapping(value = "/get/{id}")
	@ResponseBody 
	String get(@PathVariable String id,
	   @RequestParam (value='instance', required=false) String instance,
	   @RequestParam (value='key', required=false) String key,
	   @RequestParam (value='default', required=false) String defaultVal) {
	   
	   if (key) {
		   return svc.getUserParamVal(id, key, instance) ?: defaultVal;
	   }
	   return svc.getUserParam(id, instance) ?: defaultVal;
	}

			   
   /**
	* Updates specified key/value's in the parameter.  Unspecified, existing values are not altered/removed.
	*/
   @RequestMapping(value = "/set/{id}")
   public String set(@PathVariable String id,
	   @RequestParam (value='instance', required=false) String instance,
	   @RequestParam Map params) {
	   svc.setUserParamVals(id, instance, params);
	   return "redirect:/param/get/${id}"
   }
	   
   @RequestMapping(value = "/replace/{id}")
   public String replace(@PathVariable String id,
	   @RequestParam (value='instance', required=false) String instance,
	   @RequestParam Map params) {
	   svc.clearUserParam(id);
	   svc.setUserParamVals(id, instance, params);
	   return "redirect:/param/get/${id}"
   }
	
	   
   /**
	* Stores the body of the post as the new contents of the param. Any existing values are replaced.
	*/
   @RequestMapping(value = "/put/{id}", method = RequestMethod.POST)
   public String put(@PathVariable String id, @RequestParam (value='instance', required=false) String instance, @RequestBody Map data) {
	   svc.setUserParamVals(id, instance, data);
	   return "redirect:/param/get/${id}"
   }
   
   @RequestMapping(value = "/list/{id}")
   @ResponseBody
   public String list(@PathVariable String id) {
	   return svc.getUserParamInstanceIDs(id);
   }
   
   @RequestMapping(value = "/delete/{id}")
   @ResponseBody
   public String delete(@PathVariable String id,
	   @RequestParam (required=false) String instance) {
	   svc.clearUserParam(id, instance);
	   return ""
   }
}
