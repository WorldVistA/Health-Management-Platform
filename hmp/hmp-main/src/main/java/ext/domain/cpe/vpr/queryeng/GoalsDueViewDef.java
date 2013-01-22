package EXT.DOMAIN.cpe.vpr.queryeng;

import EXT.DOMAIN.cpe.vpr.frameeng.FrameJob;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRegistry;
import EXT.DOMAIN.cpe.vpr.frameeng.Goal;
import EXT.DOMAIN.cpe.vpr.frameeng.Goal.GoalStatus;
import EXT.DOMAIN.cpe.vpr.frameeng.IFrame;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameJob.FrameTask;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.ActionColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.HL7DTMColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.QueryColDef;
import EXT.DOMAIN.cpe.vpr.queryeng.Query.ViewDefQuery;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value="EXT.DOMAIN.cpe.vpr.queryeng.GoalsDueViewDef")
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
