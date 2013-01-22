package EXT.DOMAIN.cpe.vpr.queryeng;

import EXT.DOMAIN.cpe.vpr.VprConstants;
import EXT.DOMAIN.cpe.vpr.queryeng.query.JDSQuery;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDefCriteria;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;
import EXT.DOMAIN.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.ProfileDocsViewDef")
@Scope("prototype")
public class ProfileDocsViewDef extends ViewDef {

	@Autowired 
	public ProfileDocsViewDef(OpenInfoButtonLinkGenerator linkgen, Environment environ)
	{
		declareParam(new ViewParam.ViewInfoParam(this, "Profile Documents"));
		declareParam(new ViewParam.PatientIDParam());
		
		// list of fields that are not displayable as columns and a default user column set/order
		String displayCols = "kind,summary";
		String requireCols = "kind,summary";
		String hideCols = "uid,content,selflink";
        
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, "", ""));

    	QueryDef querydef = new QueryDef();
    	querydef.fields().alias("typeName","localTitle").alias("referenceDateTime","dateTime");
    	
    	Query q1 = new JDSQuery("uid", querydef, "/vpr/{pid}/index/cwad") {
        	protected Map<String, Object> mapRow(RenderTask renderer, Map<String, Object> row) {
        		Map<String, Object> ret = super.mapRow(renderer, row);
        		if(ret.containsKey("content") && ret.get("content") != null)
        		{
        			ret.put("summary", ProfileDocsViewDef.this.buildSummaryContentFromFullContent(ret.get("content").toString())); 
        		}
        		else if(ret.containsKey("products") && ret.containsKey("reactions") && ret.containsKey("kind"))
        		{
        			ret.put("summary", ProfileDocsViewDef.this.buildSummaryContentFromAllergyData(ret.get("products"), ret.get("reactions"), ret.get("kind")));
        		} else if (ret.containsKey("documentTypeName") && ret.get("documentTypeName").equals("ALERT")) {
        			ret.put("summary", ret.get("title"));
        		}
        		return ret;
        	}
        };
        
        // Only show profile documents.
        //querydef.addCriteria(QueryDefCriteria.where("kind").in("Advance Directive", "Crisis Note", "Allergy/Adverse Reaction", "Clinical Warning"));
		querydef.addCriteria(QueryDefCriteria.where("pid").is(":pid"));
		addColumns(q1, "uid", "content", "summary", "kind");
        getColumn("summary").setMetaData("text", "Content Summary").setMetaData("flex", 1);
        getColumn("uid").setMetaData("text", "uid");
        getColumn("kind").setMetaData("text", "Type");
        addColumn(new ColDef.UidClassSelfLinkColDef("selfLink"));
		addQuery(q1);
	}

	protected Object buildSummaryContentFromAllergyData(Object products, Object reactions, Object kind) {
		StringBuilder rslt = new StringBuilder();
		rslt.append(kind.toString());
		rslt.append(": ");
		
		if(products instanceof List && ((List)products).size()>0)
		{
			Object product = ((List)products).get(0);
			rslt.append(product instanceof String ? product : product instanceof Map ? ((Map)product).get("name"):"");
			for(int i = 1; i<((List)products).size(); i++)
			{
				rslt.append(", ");
				product = ((List)products).get(i);
				rslt.append(product instanceof String ? product : product instanceof Map ? ((Map)product).get("name"):"");
			}
		}
		rslt.append(" can cause the following: ");
		if(reactions instanceof List && ((List)reactions).size()>0)
		{
			Object reaction = ((List)reactions).get(0);
			rslt.append(reaction instanceof String ? reaction : reaction instanceof Map ? ((Map)reaction).get("name") : "");
			for(int i = 1; i<((List)reactions).size(); i++)
			{
				rslt.append(", ");
				rslt.append(((List)reactions).get(i));
			}
		}

		return rslt.toString();
	}

	/*
	 * Quick-and-dirty parsing to brief crisis / advance note critical data to show in list.
	 */
	protected Object buildSummaryContentFromFullContent(String content) {
		String rslt = content;
		if(content.contains("STATUS: "))
		{
			rslt = content.substring(content.indexOf("STATUS: "));
			rslt = rslt.substring(rslt.indexOf("\r"));
			rslt = rslt.trim();
		}
		return rslt;
	}
}
