package EXT.DOMAIN.cpe.vpr.queryeng;

import EXT.DOMAIN.cpe.vpr.Procedure;
import EXT.DOMAIN.cpe.vpr.VprConstants;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.Query.QueryMode;
import EXT.DOMAIN.cpe.vpr.queryeng.query.JDSQuery;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDefCriteria;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;
import EXT.DOMAIN.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.lowagie.text.Document;

//@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.MergedDocumentsViewDef")
@Scope("prototype")
public abstract class MergedDocumentsViewDef extends ViewDef {
	protected Environment env = null;
//	@Autowired 
	public MergedDocumentsViewDef(OpenInfoButtonLinkGenerator linkgen, Environment environ)
	{
		env = environ;
//		declareParam(new ViewParam.ViewInfoParam(this, "Documents"));
		declareParam(new ViewParam.PatientIDParam());
		
		// list of fields that are not displayable as columns and a default user column set/order
		String displayCols = "LOCALTITLE,DATETIME,TYPE,SERVICE,AUTHOR,FACILITY,STATUS";
		String requireCols = "LOCALTITLE,DATETIME,Type,SERVICE,AUTHOR,FACILITY,STATUS";
		String hideCols = "UID,SELFLINK,DOMAINCLASS";
		String sortCols = "LOCALTITLE,DATETIME,AUTHOR,FACILITY";
		String groupCols = "LOCALTITLE,TYPE,SERVICE,AUTHOR,FACILITY";

        if (environ.acceptsProfiles(VprConstants.JSON_DATASTORE_PROFLE, VprConstants.MONGO_DATASTORE_PROFLE)) {
        	// Decapitalize!
    		displayCols = "localTitle,dateTime,kind,service,author,facilityName,status";
    		requireCols = "localTitle,dateTime,kind,service,author,facilityName,status";
    		hideCols = "uid,selfLink,domainClass";
//    		sortCols = "localTitle,dateTime,author,facilityName";
    		sortCols = "localTitle,dateTime,facilityName"; // Author sort does not work because it is called different things in different files that both contribute to this notesview JDS template.
    		groupCols = "localTitle,kind,service,author,facilityName";
        }
        
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));

		Query q1 = null;

        if (environ.acceptsProfiles(VprConstants.JSON_DATASTORE_PROFLE, VprConstants.MONGO_DATASTORE_PROFLE)) {
        	QueryDef querydef = new QueryDef();
        	querydef.fields().alias("typeName","localTitle").alias("referenceDateTime","dateTime");

    		declareParam(new ViewParam.SortParam("dateTime", false));
    		
        	for(final String[] filter: getInFilter())
        	{
        		Vector<String> c = new Vector<String>();
        		for(int i = 1; i<filter.length; i++)
        		{
        			c.add(filter[i]);
        		}
        		String fkey = filter[0];
        		//querydef.addOrAmendCriteria();
        		
        		declareParam(new ViewParam.ENUMParam("filter."+fkey, "", c).addMeta("multiple", true).addMeta("title", fkey+" filter"));
        		declareParam(new ViewParam.AsArrayListParam("filter."+fkey));
        		declareParam(new ViewParam.QuickFilterParam("qfilter."+fkey, "", c));
        		declareParam(new ViewParam.AsArrayListParam("qfilter."+fkey));

        		//querydef.addCriteria(QueryDefCriteria.where(fkey).in(c).and(fkey).in("?:qfilter."+fkey).and(fkey).in("?:filter."+fkey));
        		//querydef.addOrAmendCriteria(QueryDefCriteria.where(fkey).in("?:filter."+fkey));
        		querydef.addCriteria(QueryDefCriteria.where(fkey).in(new QueryDefCriteria.SpelRef(){
					@Override
					public boolean filterOut(Map<String, Object> params) {
						String[][] filterz = MergedDocumentsViewDef.this.getInFilter();
						return ! (filterz.length>0 && filterz[0].length>1); // Will always have at least the hard coded filter data.
					}
					@Override
					public Object evaluateWithParams(Map<String, Object> params) {
						Object qcoll = params.get("qfilter."+filter[0]);
						Object fcoll = params.get("filter."+filter[0]);
						Vector<String> rslt = new Vector<String>();
						for(int i = 1; i<filter.length; i++)
						{
							if(qcoll instanceof List && qcoll!=null && ((List<String>)qcoll).size()>0 && !((List<String>)qcoll).contains(filter[i]))
							{
								continue;
							}
							if(fcoll instanceof List && fcoll!=null && ((List<String>)fcoll).size()>0 && !((List<String>)fcoll).contains(filter[i]))
							{
								continue;
							}
							rslt.add(filter[i]);
						}
						return rslt;
					}
				}));
        	}
            q1 = new JDSQuery("uid", querydef, "/vpr/{pid}/index/notesview?order=#{getParamStr('sort.ORDER_BY')}") {
            	
            	protected Map<String, Object> mapRow(RenderTask renderer, Map<String, Object> row) {
            		Map<String, Object> ret = super.mapRow(renderer, row);
            		if(ret.containsKey("clinicians") && ret.get("clinicians") instanceof Iterable<?>)
            		{
            			ret.put("author", MergedDocumentsViewDef.this.getAuthorFromClinicians((Iterable<Map<Object, Object>>)ret.get("clinicians")));
            		} else if(ret.containsKey("providers"))
            		{
            			ret.put("author", MergedDocumentsViewDef.this.getAuthorFromProviders((Iterable<Map<Object, Object>>)ret.get("providers")));
            		}
            		return ret;
            	}
            };
    		
    		addColumns(q1, "uid", "localTitle", "kind", "author", "facilityName", "status", "service","domainClass");

    		addColumn(new HL7DTMColDef(q1, "dateTime")).setMetaData("text", "Date/Time");
            getColumn("localTitle").setMetaData("text", "Title").setMetaData("flex", 1);
            getColumn("uid").setMetaData("text", "uid");
            getColumn("kind").setMetaData("text", "Type");
            getColumn("author").setMetaData("text", "Author");
            getColumn("facilityName").setMetaData("text", "Facility");
            getColumn("status").setMetaData("text", "Status");
            getColumn("service").setMetaData("text", "Service");
            addColumn(new ColDef.UidClassSelfLinkColDef("selfLink"));
        }
        
		addQuery(q1);
	}

	private String buildWhereClause() {
		String rslt = "";
		String[][] fltr = getInFilter();
		if(fltr.length>0)
		{
			for(int k = 0; k<fltr.length; k++)
			{
				String[] subfltr = fltr[k];
				rslt += (k==0?" WHERE ":" AND ")+subfltr[0]+" IN (";
				for(int i = 1; i<subfltr.length; i++)
				{
					rslt += ((i>1?",'":"'") + subfltr[i] + "'");
				}
				rslt += ") ";
			}
		}
		return rslt;
	}

	protected abstract String[][] getInFilter();

	protected String[][] getAliases() {
		String[][] rslt = {
			{"typeName","localTitle"},
			{"referenceDateTime","dateTime"}
		};
		return rslt;
	}
	
	private String getAuthorFromProviders(Iterable<Map<Object, Object>> iterable) {
		Iterator<Map<Object, Object>> it = iterable.iterator();
		String rslt = null;
		while(it.hasNext() && rslt == null)
		{
			Map<Object, Object> next = it.next();
			
			if(next.get("providerName")!=null) // Per Mel, this is sufficient.
			{
				rslt = (rslt==null?next.get("providerName").toString():"--Multiple--");
			}
		}
		if(rslt == null)
		{
			rslt = "--None--";
		}
		return rslt;
	}

	private String getAuthorFromClinicians(Iterable<Map<Object, Object>> iterable) {
		Iterator<Map<Object, Object>> it = iterable.iterator();
		String rslt = null;
		while(it.hasNext() && rslt == null)
		{
			Map<Object, Object> next = it.next();
			
			if(next.get("role").toString().equalsIgnoreCase("A")) // Per Mel, this is sufficient.
			{
				rslt = next.get("name").toString();
			}
		}
		if(rslt == null)
		{
			rslt = "--None--";
		}
		return rslt;
	}
}
