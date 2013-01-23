package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.auth.HmpUserDetails;
import org.osehra.cpe.feed.atom.Link;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.UidUtils;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.queryeng.Query.HQLQuery;
import org.osehra.cpe.vpr.queryeng.Query.QueryMode;
import org.osehra.cpe.vpr.queryeng.Query.StaticQuery;
import org.osehra.cpe.vpr.queryeng.dynamic.columns.ViewDefDefColDef;
import org.osehra.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;
import org.osehra.cpe.vpr.ws.link.PatientRelatedSelfLinkGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * ColDefs define the columns available in a ViewDef.
 * 
 * ColDefs may contribute column/field data to results or it may just be a "virtual column" of other results, or both.
 * ColDefs may contribute more than one column/field worth of data to the results.
 * ColDefs may be attached to other queries to map data from multiple queries into a single result.
 * ColDefs can have generic meta data that may be used by UI applications for rendering/display preferences.
 * 
 * TODO: need to find a way to automate dealing with dotted column names by diving into sub-maps.  It works in groovy eval
 * but should be generalized across all ColDefs in plain java.
 * 
 * TODO: experiment with a coldef that would render asynch in ExtJS outside of the primary data query (ie a sparkline image next to a vital)
 */
public abstract class ColDef {
	private Map<String, Object> metadata = new HashMap<String,Object>();

	private String key;
	private Query query;

	public ColDef(String key, Query query) {
		this.key = key;
 		this.query = query;
        setMetaData("dataIndex", key);
	}
	
	public Map<String, Object> getColumnMetaData(ViewDef qd) {
		return metadata;
	}

	public ColDef setMetaData(Map<String,Object> map) {
		metadata.putAll(map);
		return this;
	}
	
	public ColDef setMetaData(String key, Object val) {
		metadata.put(key, val);
		return this;
	}

    public ColDef removeMetaData(String key) {
        metadata.remove(key);
        return this;
    }

	/**
	 * Render this column/cell into the results
	 * 
	 * @param vd
	 * @param pkval The PK value for the resulting row that is being rendered (ie you should always use it to insert results) 
	 * @param fkval 
	 * @param results The results-in-progress being rendered.  
	 */
	public abstract void render(ViewDefRenderer task);

	/**
	 * @return Primary query associated with the column.  May be null.
	 */
	public Query getQuery() {
		return query;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String toString() {
		return getClass().getSimpleName() + ": " + this.key;
	}
	
	public static class SubTableColDef extends ColDef {
		public SubTableColDef(String key, Query q) {
			super(key, q);
		}

		public void render(ViewDefRenderer renderer) {
			// Since the query is likely going to be re-run every row, we need to clone
			// it otherwise in the end all the rows will point to the last execution.
			Query q = getQuery();
			q.exec(renderer);
			StaticQuery q2 = new StaticQuery(q.getPK());
			q2.setFK(q.getFK());
			for (int i = 0; i <= q.size(); i++) {
				Map<String, Object> row = q.getRowIdx(i);
				if (row != null) {
					q2.addRow(q.getRowIdx(i));
				}
			}
			renderer.addResult(getKey(), q2);
		}		
	}
	
	/**
	 * TODO: this column should probably go away, this is too much ExtJS in Java.
	 * Longer term strategy would be to have a GridAdvisor type ExtJS object to handle some of this.
	 */
	public static class TemplateColDef extends ColDef {
		private String template;

		public TemplateColDef(String key, String template) {
			super(key, null);
			this.template = template;
			setMetaData("xtype", "templatecolumn");
			setMetaData("tpl", this.template);
            removeMetaData("dataIndex");
		}

		@Override
		public void render(ViewDefRenderer renderer) {
			// this is for metadata only, so no additions to result set are necessary.
		}
	}
	
	@Deprecated 
	public static class QueryColDef extends ColDef {
		protected String source;

		public QueryColDef(Query q, String sourceTarget) {
			super(sourceTarget, q);
			this.source = sourceTarget;
		}

		public QueryColDef(Query q, String source, String target) {
			super(target, q);
			this.source = source;
		}
		
		@Override
		public void render(ViewDefRenderer renderer) {
			Object val = getQuery().getCell(renderer.querypkval, this.source);
			if (val != null) {
				renderer.addResult(getKey(), val);
			}
		}
	}
	
	/**
	 * Date/Time handling column.  Parses the HL7DateTime from the database
	 * into a PointInTime class (which the JSON renderer knows how to convert into a JavaScript date)
	 */
	public static class HL7DTMColDef extends QueryColDef {

		public HL7DTMColDef(Query q, String sourceTarget) {
			this(q, sourceTarget, sourceTarget);
		}
		
		public HL7DTMColDef(Query q, String source, String target) {
			super(q, source, target);
			setMetaData("width", 125);
			setMetaData("xtype", "hl7dtmcolumn");
		}
		
		@Override
		public void render(ViewDefRenderer renderer) {
			Object val = getQuery().getCell(renderer.querypkval, this.source);
			
			// TODO: Exception handling?
			if (val != null) {
              renderer.addResult(getKey(), val.toString());
//				PointInTime pit = HL7DateTimeFormat.parse(val.toString());
//				if (pit != null) {
//					renderer.addResult(getKey(), pit);
//				}
			}
		}
	}
	
	/**
	 * 
	 * @author Jim
	 */
	public static class LastViewedColDef extends ColDef {
		/**
		 * Try to only do the query once per instance of this ColDef.
		 * This lvq will hold the query for all calls to this ColDef's render() method.
		 */
		private Query lvq = null;
		private final String hql = "SELECT lv.uid AS uid, lv.userId AS userId FROM LastViewed lv " +
				"WHERE lv.userId = :userId AND lv.uid IN (:uids)";
		

		public LastViewedColDef(String key) {
			super(key, null);
			// default metadata that goes to ExtJS.
			setMetaData("xtype", "wasviewedcolumn");
		}

		@Override
		public void render(ViewDefRenderer renderer) {
			if(lvq==null)
			{
				Set<Object> uids = renderer.primary.getUniqueValues("uid");
				
				renderer.setParam("userId", getAuthenticatedUserId());
				renderer.setParam("uids", uids);
				lvq = new HQLQuery("uid", hql, QueryMode.ONCE);
				lvq.exec(renderer);
			}
			Object uid = renderer.results.getCell(renderer.resultspkval, "uid");
			if(uid==null){return;}
			Boolean wasViewed = (lvq.getRow(uid.toString())!=null);

			renderer.results.setCell(renderer.resultspkval, getKey(), wasViewed);
			
		}
		
		/*
		 * I don't know why @Autowired doesn't seem to work in this case.
		 * Maybe the Spring autowiring scope doesn't extend to this package or something.
		 */
		protected String getAuthenticatedUserId()
		{
			SecurityContext context = SecurityContextHolder.getContext();
		    if (context == null) return null;
		    Authentication auth = context.getAuthentication();
		    if (auth == null) return null;
		    if (!auth.isAuthenticated()) return null;
		   // if (authenticationTrustResolver.isAnonymous(auth)) return null;
		    if (auth.getPrincipal() instanceof HmpUserDetails)
		        return ((HmpUserDetails) auth.getPrincipal()).getUid();
		    else
		        return null;
		}
	}
	
	public static class ActionColDef extends ColDef {

		public ActionColDef(String key) {
			super(key, null);
			setMetaData("xtype", "rowactioncolumn");
		}

		@Override
		public void render(ViewDefRenderer task) {
		}
	}
	
	/**
	 * Custom column that generates a infobutton URL.
	 * 
	 * Looks up most of the appropriate values from the current data set, and fills it in by
	 * fetching the full patient object for age and gender.
	 * 
	 * Should work for both single patient data sets and multi-patient data sets.
	 */
	@Deprecated // use InfobuttonQuery instead
	public static class InfoBtnLinkColDef extends ColDef {
		private HashMap<String, String> config = new HashMap<String, String>();
		private OpenInfoButtonLinkGenerator generator;

		public InfoBtnLinkColDef(OpenInfoButtonLinkGenerator gen, String key, String pidField, String codeField,
				String textField, String searchContext, String searchCodeSet) {
			super(key, null);
			this.generator = gen; // TODO: Try to get this via renderer.getResource() instead
			this.config.put("field.pid", pidField);
			this.config.put("field.code", codeField);
			this.config.put("field.text", textField);
			this.config.put("search.context", searchContext);
			this.config.put("search.codeset", searchCodeSet);
			
			// default metadata that goes to ExtJS.
			setMetaData("xtype", "infobuttoncolumn");
		}
		
		public void render(ViewDefRenderer renderer) {
			// we need the patient domain object. Using the params as temporary storage so we don't
			// have to look it up for every row (since all rows usually are for the same PID)
			Object pid = renderer.results.getCell(renderer.resultspkval, this.config.get("field.pid"));
			Patient pat = (Patient) renderer.getParamObj("tmp_pat_" + pid);
			if (pat == null && pid != null) {
				IPatientDAO dao = renderer.getResource(IPatientDAO.class);
				pat = dao.findByVprPid(pid.toString());
				renderer.setParam("tmp_pat_" + pid, pat);
			}
					
			// map to pass to link generator, start with patient data
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (pat != null) {
				map.put("age", pat.getAge());
				map.put("gender", pat.getGenderCode());
			}
			map.put("context", this.config.get("search.context"));
			map.put("searchCodeSet", this.config.get("search.codeset"));
			map.put("searchText", renderer.results.getCell(renderer.resultspkval, this.config.get("field.text")));
			map.put("searchCode", renderer.results.getCell(renderer.resultspkval, this.config.get("field.code")));
			
			// TODO: hacky fix the search code+gender code to remove the URN:XXX: values so infobuttons work
			Object code = map.get("searchCode");
			if (code != null && code.toString().startsWith("urn:")) {
				map.put("searchCode", code.toString().split(":")[2]);
			}
			code = map.get("gender");
			if (code != null && code.toString().startsWith("urn:")) {
				map.put("gender", code.toString().split(":")[3]);
			}

			// run the link generator, if it returns a value, add it to the results.
			Link link = generator.generateLinkFromMap(map);
			if (link != null) {
				renderer.addResult(getKey(), link.getHref());
			}
		}
	}
	
	public static class UidClassSelfLinkColDef extends ColDef {

	    UidClassSelfLinkColDef(String key) {
	        super(key, null);
	    }

	    @Override
		public void render(ViewDefRenderer renderer) {
	    	Class<?> clazz = null;
	        Object uid = renderer.results.getCell(renderer.resultspkval, "uid");//.toString();
	        Object pid = renderer.results.getCell(renderer.resultspkval, "pid");//.toString();
	        if(pid==null)
	        {
	        	pid = renderer.getParamStr("pid");
	        }
	        if (uid != null)
	        {	
				clazz = UidUtils.getDomainClassByUid(uid.toString());
		        renderer.results.setCell(renderer.resultspkval, getKey(), PatientRelatedSelfLinkGenerator.getSelfHref(pid.toString(), clazz, uid.toString()));
	        } 
	   }

	}
	
	public static class DynamicClassSelfLinkColDef extends ColDef {
	    private String domainClassColName;

	    DynamicClassSelfLinkColDef(String key, String domainClassColName) {
	        super(key, null);
	        this.domainClassColName = domainClassColName;
	    }

	    @Override
		public void render(ViewDefRenderer renderer) {
	    	Class<?> clazz = null;
	        Object pid = renderer.results.getCell(renderer.resultspkval, "pid");//.toString();
	        if(pid==null)
	        {
	        	pid = renderer.getParamStr("pid");
	        }
	        Object uid = renderer.results.getCell(renderer.resultspkval, "uid");//.toString();
	        if (pid != null && uid != null)
				try {
					clazz = Class.forName(renderer.results.getCell(renderer.resultspkval, domainClassColName).toString());
			         renderer.results.setCell(renderer.resultspkval, getKey(), PatientRelatedSelfLinkGenerator.getSelfHref(pid.toString(), clazz, uid.toString()));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
	   }

	}
	
	public static class DeferredViewDefDefColDef extends ColDef {

		public ViewDefDefColDef cdef;
		public String keyCol;

		public DeferredViewDefDefColDef(ViewDefDefColDef cdef, String keyCol, String dataIndex) {
			super(dataIndex, null);
			this.cdef = cdef;
			this.keyCol = keyCol;
		}

		@Override
		public void render(ViewDefRenderer task) {
			task.results.setCell(task.resultspkval, getKey(), "Loading...");
		}
		
	}
}
