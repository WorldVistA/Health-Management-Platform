package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.Task;
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.editor.EditorOption;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.TasksViewDef")
@Scope("prototype")
public class TasksViewDef extends ViewDef {

    public TasksViewDef() {
        // declare the view parameters
        declareParam(new ViewParam.ViewInfoParam(this, "Tasks"));
        declareParam(new ViewParam.PatientIDParam());
        declareParam(new ViewParam.AsArrayListParam("filter.complete"));
		declareParam(new ViewParam.SortParam("dueDate", false));

		String displayCols = "summary,assignToName,dueDate,completed,facilityName";
		String requireCols = "summary,assignToName,dueDate,completed,facilityName";
		String hideCols = "uid,selfLink";
		String sortCols = "";
		String groupCols = "";
		declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
        // Relevant Immunizations
        Query q1 = new JDSQuery("uid", "/vpr/{pid}/index/task?order=#{getParamStr('sort.ORDER_BY')}");
        addColumns(q1, "uid", "summary", "assignToName", "dueDate", "completed", "facilityName");

        getColumn("summary").setMetaData("text", "Task");
        getColumn("summary").setMetaData("flex", 1).setMetaData("editOpt", new EditorOption("taskName","text"));

        addColumn(new HL7DTMColDef(q1, "dueDate")).setMetaData("text", "Due By");
        getColumn("dueDate").setMetaData("width", 80);

        getColumn("assignToName").setMetaData("text", "Assign To").setMetaData("editOpt", new EditorOption("assignToName","text"));
        getColumn("completed").setMetaData("text", "Completed").setMetaData("editOpt", new EditorOption("completed","boolean"));
        getColumn("facilityName").setMetaData("text", "Facility");

        addColumn(new DomainClassSelfLinkColDef("selfLink", Task.class));
        addQuery(q1);
    }
}

