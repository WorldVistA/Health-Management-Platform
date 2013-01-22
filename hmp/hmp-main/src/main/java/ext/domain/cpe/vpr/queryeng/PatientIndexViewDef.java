package EXT.DOMAIN.cpe.vpr.queryeng;

import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.QueryColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.Query.QueryMode;

import java.sql.SQLException;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="EXT.DOMAIN.cpe.vpr.queryeng.PatientIndexViewDef")
public class PatientIndexViewDef extends ViewDef {

	@Autowired
    public PatientIndexViewDef(SolrServer solrServer) throws SQLException, Exception {
		declareParam(new ViewParam.ViewInfoParam(this, "Patient Index/Count"));
		declareParam(new ViewParam.PatientIDParam());
		declareParam(new ViewParam.DateRangeParam("recent", "2005..NOW"));
		
		Query q1 = new Query.SOLRFacetQuery("kind", solrServer, "pid:#{getParamStr('pid')}", "kind", QueryMode.ONCE);
		addColumn(new QueryColDef(q1, "kind").setMetaData("width", 75));
		addColumn(new QueryColDef(q1, "count", "total"));
		getColumn("total").setMetaData("text", "Tot");
		getColumn("total").setMetaData("width", 50);
		addQuery(q1);
		
		// TODO: Use SpringEL to make the date range dynamic
		Query q2 = new Query.SOLRFacetQuery("kind", solrServer, "pid:#{getParamStr('pid')} AND datetime:[#{getParamStr('recent.startHL7')} TO #{getParamStr('recent.endHL7')}]", "kind", QueryMode.ONCE);
		addColumn(new QueryColDef(q2, "count", "recent"));
		getColumn("recent").setMetaData("text", "Recent");
		getColumn("recent").setMetaData("width", 50);
		addQuery(q2);
	}
}
