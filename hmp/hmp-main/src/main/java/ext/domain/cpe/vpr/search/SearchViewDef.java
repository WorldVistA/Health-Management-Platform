package EXT.DOMAIN.cpe.vpr.search;

import EXT.DOMAIN.cpe.vpr.queryeng.Query;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewParam;
import org.apache.solr.client.solrj.SolrServer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value="EXT.DOMAIN.cpe.vpr.search.SearchViewDef")
@Scope("prototype")
public class SearchViewDef extends ViewDef {

    private SolrServer solrServer;


    public SearchViewDef() {
		declareParam(new ViewParam.PatientIDParam());

        Query.SOLRTextQuery q = new Query.SOLRTextQuery("uid", solrServer, "", Query.QueryMode.ONCE);

        addColumns(q);

        addQuery(q);
    }
}
