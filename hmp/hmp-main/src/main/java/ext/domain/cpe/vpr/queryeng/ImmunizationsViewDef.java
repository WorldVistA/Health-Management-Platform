package EXT.DOMAIN.cpe.vpr.queryeng;

import EXT.DOMAIN.cpe.vpr.Immunization;
import EXT.DOMAIN.cpe.vpr.pom.jds.JdsOperations;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.query.JDSQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.ImmunizationsViewDef")
@Scope("prototype")
public class ImmunizationsViewDef extends ViewDef {

    @Autowired
    public ImmunizationsViewDef(JdsOperations jdsTemplate, Environment environment) {
        // declare the view parameters
        declareParam(new ViewParam.ViewInfoParam(this, "Immunizations"));
        declareParam(new ViewParam.PatientIDParam());
        declareParam(new ViewParam.AsArrayListParam("filter.typeCodes"));

		String displayCols = "summary,administeredDateTime,seriesName,reactionName";
		String requireCols = "summary,administeredDateTime,seriesName,reactionName";
		String hideCols = "uid,selfLink";
		String sortCols = "";
		String groupCols = "";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
        // Relevant Immunizations
        Query q1 = new JDSQuery("uid", "/vpr/{pid}/index/immunization");
        addColumns(q1, "uid", "summary", "administeredDateTime", "seriesName", "reactionName", "comments", "facilityName");

        getColumn("summary").setMetaData("text", "Description");
        getColumn("summary").setMetaData("flex", 1);

        addColumn(new HL7DTMColDef(q1, "administeredDateTime")).setMetaData("text", "Recorded Date");
        getColumn("administeredDateTime").setMetaData("width", 80);

        getColumn("seriesName").setMetaData("text", "Series");
        getColumn("reactionName").setMetaData("text", "Reaction");
        getColumn("comments").setMetaData("text", "Comments");
        getColumn("facilityName").setMetaData("text", "Facility");

        addColumn(new DomainClassSelfLinkColDef("selfLink", Immunization.class));
        addQuery(q1);
    }
}

