package EXT.DOMAIN.cpe.vpr.queryeng;

import EXT.DOMAIN.cpe.vpr.Allergy;
import EXT.DOMAIN.cpe.vpr.pom.jds.JdsOperations;
import EXT.DOMAIN.cpe.vpr.queryeng.query.JDSQuery;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.AllergiesViewDef")
@Scope("prototype")
public class AllergiesViewDef extends ViewDef {

    @Autowired
    public AllergiesViewDef(JdsOperations jdsTemplate, Environment environment) {
        // declare the view parameters
        declareParam(new ViewParam.ViewInfoParam(this, "Allergies"));
        declareParam(new ViewParam.PatientIDParam());
        declareParam(new ViewParam.AsArrayListParam("filter.typeCodes"));

		String displayCols = "Summary,Facility";
		String requireCols = "Summary,Facility";
		String hideCols = "uid,selfLink";
		String sortCols = "";
		String groupCols = "";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
        // Relevant allergies
		QueryDef querydef = new QueryDef();
		querydef.fields().alias("summary", "Summary").alias("facilityName", "Facility");
        Query q1 = new JDSQuery("uid", querydef, "/vpr/{pid}/index/allergy");

        addColumns(q1, "uid", "Summary", "Facility");

        getColumn("Summary").setMetaData("text", "Description");
        getColumn("Summary").setMetaData("flex", 1);

        getColumn("Facility").setMetaData("text", "Facility");

        addQuery(q1);
        addColumn(new DomainClassSelfLinkColDef("selfLink", Allergy.class));
    }
}

