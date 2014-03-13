package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.frameeng.FrameJob;
import org.osehra.cpe.vpr.frameeng.FrameRegistry;
import org.osehra.cpe.vpr.frameeng.Goal;
import org.osehra.cpe.vpr.frameeng.Goal.GoalStatus;
import org.osehra.cpe.vpr.frameeng.IFrame;
import org.osehra.cpe.vpr.frameeng.FrameJob.FrameTask;
import org.osehra.cpe.vpr.queryeng.ColDef.ActionColDef;
import org.osehra.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import org.osehra.cpe.vpr.queryeng.ColDef.QueryColDef;
import org.osehra.cpe.vpr.queryeng.Query.ViewDefQuery;
import org.osehra.cpe.vpr.viewdef.RenderTask;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value="org.osehra.cpe.vpr.queryeng.GoalsDueViewDef")
@Scope("prototype")
public class GoalsDueViewDef extends ViewDef {
	public GoalsDueViewDef() {
		super();
		declareParam(new ViewParam.ViewInfoParam(this, "Goals Due"));
		declareParam(new ViewParam.ENUMParam("conditions", "", "", "HTN", "DMII", "COPD"));
		declareParam(new ViewParam.PatientIDParam());
	}
	
	@Override
	protected void doInit(FrameJob task) throws Exception {
		FrameRegistry reg = task.getResource(FrameRegistry.class);
		List<IFrame> goals = reg.findAllByClass(Goal.class);
		for (IFrame g : goals) {
			addQuery(new GoalQuery((Goal) g));
		}
		
		Query q1 = getPrimaryQuery();
		addColumn(new ActionColDef("rowactions"));
		addColumns(q1, "focus", "status", "relevant_data");
		getColumn("status").setMetaData("width", 75);
		getColumn("relevant_data").setMetaData("width", 150);
		addColumn(new HL7DTMColDef(q1, "last_done")).setMetaData("width", 75);
		addColumn(new QueryColDef(q1, "guidelines")).setMetaData("width", 250);
	}
	
	@Override
	protected void postProcessHook(RenderTask task) {
		List<GoalStatus> goals = task.getParentTask().getActions(GoalStatus.class);
		for (GoalStatus goal : goals) {
			task.add(goal.toMap());
		}
	}
	
	
	public static class GoalQuery extends Query {
		private Goal goal;

		public GoalQuery(Goal goal) {
			super("focus", null);
			this.goal = goal;
		}

		@Override
		public void exec(RenderTask task) throws Exception {
			this.goal.exec(task.getParentTask());
		}
		
	}
	
}
