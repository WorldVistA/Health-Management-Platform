package EXT.DOMAIN.cpe.vpr.web;

import EXT.DOMAIN.cpe.jsonc.JsonCCollection;
import EXT.DOMAIN.cpe.jsonc.JsonCResponse;
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.JSONViews;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDefCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.DB;

import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView;

@Controller
public class MongoController {
	
	@Autowired
	IGenericPatientObjectDAO dao;
	
	@Autowired(required=false)
	DB db;
	
	// Index fetch ------------------------------------------------------------
	@RequestMapping(value = "/jds/{pid}/index/{indexName}/")
	public ModelAndView getByIndex(@PathVariable(value = "pid") String pid,
			@PathVariable(value = "indexName") String indexName,
			HttpRequest request) {
		return getByIndexWithTemplate(pid, indexName, null, request);
	}
	
	@RequestMapping(value = "/jds/{pid}/index/{indexName}/{template}")
	public ModelAndView getByIndexWithTemplate(@PathVariable(value = "pid") String pid,
			@PathVariable(value = "indexName") String indexName,
			@PathVariable(value = "template") String template,
			HttpRequest request) {
		// TODO: url vars: order,limit,template
		
		// parse the range param
		Object range = request.getParams().getParameter("range");
		String startRange = null, endRange = null;
		if (range != null && range.toString().length() > 0) {
			String[] parts = range.toString().split("\\..");
			if (parts.length > 0 && parts[0] != null) {
				startRange = parts[0];
			}
			if (parts.length > 1 && parts[1] != null) {
				endRange = parts[1];
			}
		}
		
		// build the query to pass to the DAO
		QueryDef qd = new QueryDef();
		qd.addCriteria(QueryDefCriteria.where("pid").is(pid));
		qd.namedIndexRange(indexName, startRange, endRange);
		List<IPatientObject> results = dao.findAllByQuery(IPatientObject.class, qd, new HashMap<String,Object>());
		JsonCResponse<?> ret = null;
		
		// if a template was specified, return only that data
		if (getJSONView(template) != null) {
			ArrayList<Object> tmp = new ArrayList<Object>();
			for (IPatientObject obj : results) {
				tmp.add(obj.getData(getJSONView(template)));
			}
			ret = JsonCCollection.create(tmp);
		} else {
			ret = JsonCCollection.create(results);
		}
		return contentNegotiatingModelAndView(ret);
	}
	
	// fetch by URN -----------------------------------------------------------
	
	@RequestMapping(value = "/jds/{uid:urn\\:.*}")
	public ModelAndView getByUID(@PathVariable(value = "uid") String uid) {
		return getByPIDAndUIDWithTemplate(null, uid, null);
	}
	
	@RequestMapping(value = "/jds/{uid:urn\\:.*}/{template}")
	public ModelAndView getByUIDWithTemplate(@PathVariable(value = "uid") String uid,
			@PathVariable(value = "template") String template) {
		return getByPIDAndUIDWithTemplate(null, uid, template);
	}
	
	@RequestMapping(value = "/jds/{pid}/{uid:urn\\:.*}")
	public ModelAndView getByPIDAndUID(@PathVariable(value = "pid") String pid,
			@PathVariable(value = "uid") String uid) {
		return getByPIDAndUIDWithTemplate(pid, uid, null);
	}
	
	@RequestMapping(value = "/jds/{pid}/{uid:urn\\:.*}/{template}")
	public ModelAndView getByPIDAndUIDWithTemplate(@PathVariable(value = "pid") String pid,
			@PathVariable(value = "uid") String uid,
			@PathVariable(value = "template") String template) {
		
		IPatientObject obj = dao.findByUID(IPatientObject.class, uid);
		JsonCResponse<?> ret = null;
		if (obj != null && (pid == null || obj.getPid().equals(pid))) {
			if (getJSONView(template) != null) {
				ret = JsonCCollection.create(obj.getData(getJSONView(template)));
			} else {
				ret = JsonCCollection.create(obj);
			}
		}
		return contentNegotiatingModelAndView(ret);
	}
	
	private static Class<JSONViews> getJSONView(String str) {
		if (str == null) return null;
		String className = JSONViews.class.getName() + "$" + str;
		try {
			Class<?> viewClass = Class.forName(className);
			if (JSONViews.class.isAssignableFrom(viewClass)) {
				return (Class<JSONViews>) viewClass;
			}
		} catch (ClassNotFoundException ex) {
			// ignore...
		}
		return null;
	}
	/*
	 * TODO: 
 ;;POST vpr/{pid?1.N} PUTOBJ^VPRJPR
 ;;PUT vpr/{pid?1.N} PUTOBJ^VPRJPR
 ;;GET vpr/{pid?1.N}/index/{indexName} INDEX^VPRJPR
 ;;GET vpr/{pid?1.N}/index/{indexName}/{template} INDEX^VPRJPR
 ;;GET vpr/{pid?1.N}/count/{countName} COUNT^VPRJPR
 ;;GET vpr/{pid?1.N}/{uid?1"urn:".E} GETOBJ^VPRJPR
 ;;GET vpr/{pid?1.N}/{uid?1"urn:".E}/{template} GETOBJ^VPRJPR
 ;;GET vpr/{pid?1.N}/find/{collection} FIND^VPRJPR
 ;;GET vpr/{pid?1.N}/find/{collection}/{template} FIND^VPRJPR
 ;;GET vpr/{pid?1.N} GETPT^VPRJPR
 ;;GET vpr/uid/{uid?1"urn:".E} GETUID^VPRJPR
 ;;GET vpr/uid/{uid?1"urn:".E}/{template} GETUID^VPRJPR
 ;;POST vpr PUTPT^VPRJPR
 ;;PUT vpr PUTPT^VPRJPR
 ;;GET vpr/all/count/{countName} ALLCOUNT^VPRJPR
 ;;GET vpr/all/index/{indexName} ALLINDEX^VPRJPR
 ;;GET vpr/all/index/{indexName}/{template} ALLINDEX^VPRJPR
 ;;GET vpr/all/find/{collectionName} ALLFIND^VPRJPR
 ;;GET vpr/pid/{icndfn} PID^VPRJPR
 ;;DELETE vpr/{pid?1.N}/{uid?1"urn:".E} DELUID^VPRJPR
 ;;DELETE vpr/{pid?1.N} DELPT^VPRJPR
 ;;DELETE vpr DELALL^VPRJPR
 ;;DELETE vpr/{pid?1.N}/collection/{collectionName} DELCOLL^VPRJPR
 ;;DELETE vpr/all/collection/{collectionName} ALLDELC^VPRJPR
	 */
}
