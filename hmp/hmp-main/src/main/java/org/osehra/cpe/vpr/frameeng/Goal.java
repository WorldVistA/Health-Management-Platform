package org.osehra.cpe.vpr.frameeng;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.Immunization;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.Result;
import org.osehra.cpe.vpr.VitalSign;
import org.osehra.cpe.vpr.frameeng.FrameJob.FrameTask;
import org.osehra.cpe.vpr.frameeng.Goal.GoalStatus.DueStatus;
import org.osehra.cpe.vpr.frameeng.IFrameTrigger.InvokeTrigger;
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.queryeng.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Days;
import org.springframework.stereotype.Component;

public abstract class Goal extends Frame {
	protected  IGenericPatientObjectDAO dao;
	private InvokeTrigger<IPatientObject> actionTrig;
	
	@Override
	protected void doInit(FrameJob task) throws Exception {
		dao = task.getResource(IGenericPatientObjectDAO.class);
		actionTrig = addTrigger(new IFrameTrigger.InvokeTrigger<IPatientObject>(this, IPatientObject.class, "org.osehra.cpe.vpr.rowaction"));
	}
	
	protected <T extends IPatientObject> T findOne(Class<T> clazz, String url, Map<String, Object> params) {
		List<T> results = dao.findAllByQuery(clazz, url, params);
		if (results != null && results.size() >= 1) {
			return results.get(0);
		}
		return null;
	}
	
	@Override
	public void exec(FrameTask task) throws Exception {
		if (actionTrig.isTriggerOf(task)) {
			rowAction(task);
		} else {
			evalGoal(task);
		}
	}
	
	public abstract void evalGoal(FrameTask task) throws Exception;
	
	/** 
	 * helper method for contributing row actions to goals
	 */
	protected void rowAction(FrameTask task) {
		// nothing by default.
	}
	
	@Component(value="org.osehra.cpe.vpr.goals.PotassiumGoal")
	public static class PotassiumGoal extends Goal {
		private static String SUMMARY = "%s: <b style=\"color: %s; font-weight: bold;\">%s</b>";
		@Override
		public void evalGoal(FrameTask task) throws Exception {
			Result r = findOne(Result.class, "/vpr/{pid}/last/lab-type?range=POTASSIUM", task.getParams());
			GoalStatus g = new GoalStatus(365);
			g.focus = "Serum Potassium";
			g.guidelines = "Annually";
			if (r != null) {
				Number val = r.getResultNumber();
				if (val != null) {
					String color = (val.intValue() >= 200) ? "red" : "blue";
					g.relevant_data = String.format(SUMMARY, r.getDisplayName(), color, r.getSummary());
				}
				g.uid = r.getUid();
				g.last_done = r.getObserved();
			}
			task.addAction(g);
		}
	}
	
	@Component(value="org.osehra.cpe.vpr.goals.BloodPressureGoal")
	public static class BloodPressureGoal extends Goal {
		@Override
		public void evalGoal(FrameTask task) throws Exception {
			VitalSign res = findOne(VitalSign.class, "/vpr/{pid}/last/vs-type?range=BLOOD PRESSURE", task.getParams());
			GoalStatus g = new GoalStatus(365);
			g.focus = "B.P.";
			g.guidelines = "q visit";
			g.viewdef = "org.osehra.cpe.vpr.queryeng.VitalsViewDef";
			g.viewdef_title = "Last Vitals";

			if (res != null) {
				g.relevant_data = res.getSummary();
				g.uid = res.getUid();
				g.last_done = res.getObserved();
			}
			task.addAction(g);
		}
	}
	
	@Component(value="org.osehra.cpe.vpr.goals.CreatinineGoal")
	public static class CreatinineGoal extends Goal {
		private static String SUMMARY = "%s: <b style=\"color: %s; font-weight: bold;\">%s</b>";
		@Override
		public void evalGoal(FrameTask task) throws Exception {
			Result r = findOne(Result.class, "/vpr/{pid}/last/lab-type?range=CREATININE", task.getParams());
			GoalStatus g = new GoalStatus(365);
			g.focus = "Serum Creatinine";
			g.guidelines = "Annually";
			if (r != null) {
				Number val = r.getResultNumber();
				if (val != null) {
					String color = (val.intValue() >= 200) ? "red" : "blue";
					g.relevant_data = String.format(SUMMARY, r.getDisplayName(), color, r.getSummary());
				}
				g.uid = r.getUid();
				g.last_done = r.getObserved();
			}
			task.addAction(g);
		}
	}
	
	@Component(value="org.osehra.cpe.vpr.goals.CholesterolGoal")
	public static class CholesterolGoal extends Goal {
		private static String SUMMARY = "%s: <b style=\"color: %s; font-weight: bold;\">%s %s</b>";
		@Override
		public void evalGoal(FrameTask task) throws Exception {
			Result r1 = findOne(Result.class, "/vpr/{pid}/last/lab-type?range=CHOLESTEROL", task.getParams());
			Result r2 = findOne(Result.class, "/vpr/{pid}/last/lab-type?range=LDL CHOLESTEROL", task.getParams());
			
			GoalStatus g = new GoalStatus(365);
			g.focus = "Chol.";
			g.guidelines = "Annually";
			g.viewdef = "org.osehra.cpe.vpr.queryeng.LabViewDef";
			g.viewdef_title = "Cholesterol Results";
			g.viewdef_params = Table.buildRow("filter.typeNames", new String[] { "CHOLESTEROL", "LDL CHOLESTEROL", "HDL CHOLESTEROL"});

			if (r1 != null && r2 != null) {
				String color = (r1.isAbnormal() || r2.isAbnormal()) ? "red" : "blue";
				g.relevant_data = String.format(SUMMARY, r1.getDisplayName(), color, r1.getResult(), r1.getUnits());
				g.relevant_data += "<br/>";
				g.relevant_data += String.format(SUMMARY, r2.getDisplayName(), color, r2.getResult(), r2.getUnits());
				g.uid = r1.getUid();
				g.last_done = r1.getObserved();
			}
			task.addAction(g);
		}
	}
	
	@Component(value="org.osehra.cpe.vpr.goals.FluImmunizationGoal")
	public static class FluImmunizationGoal extends Goal {
		@Override
		public void evalGoal(FrameTask task) throws Exception {
			Immunization r = findOne(Immunization.class, "/vpr/{pid}/last/imm-name?range=FLU*", task.getParams());
			
			GoalStatus g = new GoalStatus(365);
			g.focus = "Flu Vacc.";
			g.guidelines = "Annually, unless egg allergic";
			g.viewdef = "org.osehra.cpe.vpr.queryeng.ImmunizationsViewDef";
			g.viewdef_title = "Immunization History";
			g.selfLink = "/frame/goal/vacc/" + task.getParamStr("pid");
			if (r != null) {
				g.relevant_data = r.getSummary();
				g.uid = r.getUid();
				g.last_done = r.getAdministeredDateTime();
			}
			task.addAction(g);
		}
	}
	
	@Component(value="org.osehra.cpe.vpr.goals.PneumoImmunizationGoal")
	public static class PneumoImmunizationGoal extends Goal {
		
		@Override
		protected void rowAction(FrameTask task) {
			String focus = task.getParamStr("focus");
			if (focus !=null && focus.equals("Pneum. Vacc.")) {
				task.addAction(new FrameAction.URLActionMenuItem("http://www.cdc.ext/vaccines/vpd-vac/pneumo/default.htm", "CDC: Pneumococcal Vaccine", "", ""));
			}
		}
		
		@Override
		public void evalGoal(FrameTask task) throws Exception {
			Immunization r = findOne(Immunization.class, "/vpr/{pid}/last/imm-name?range=PNEUMO*", task.getParams());
			Patient p = findOne(Patient.class, "/vpr/{pid}", task.getParams());
			
			// compute the vaccination status
			PointInTime obs = (r != null) ? r.getAdministeredDateTime() : null;
			DueStatus duestatus = (r == null) ? DueStatus.NOT_DONE : DueStatus.NOT_DUE;
			String guideline = "19-64y: if risk factors";
			if (p.getAge() >= 65 && obs == null) {
				// >65, not vaccinated
				duestatus = DueStatus.OVERDUE;
				guideline = ">= 65y: Vaccinate w/ PPSV23";
			} else if (p.getAge() >= 65) {
				// due if >65 && vaccinated 5+ years ago
				if (obs.before(PointInTime.today().subtractYears(5))) duestatus = DueStatus.OVERDUE;
				guideline = ">= 65: revacc once after 5y";
			} else if (obs != null) {
				guideline = "19-64y: revacc at 5+ years if risk factors";
			}
			
			GoalStatus g = new GoalStatus(duestatus);
			g.focus = "Pneum. Vacc.";
			g.guidelines = guideline;
			g.viewdef = "org.osehra.cpe.vpr.queryeng.ImmunizationsViewDef";
			g.viewdef_title = "Immunization History";
			g.selfLink = "/frame/goal/vacc/" + task.getParamStr("pid");
			if (r != null) {
				g.relevant_data = r.getSummary();
				g.uid = r.getUid();
				g.last_done = r.getAdministeredDateTime();
			}
			task.addAction(g);
		}
	}
	
	public static class GoalStatus implements FrameAction {
		public static enum DueStatus {NOT_DONE,OVERDUE,NOT_DUE,DUE_SOON,NORMAL,ABNORMAL,MISC}
		
		private int days;
		private DueStatus status;
		
		public String uid;
		public String focus;
		// TODO: Structured status
		public String relevant_data;
		public PointInTime last_done;
		public String guidelines;
		public String selfLink;
		public String viewdef;
		public String viewdef_title;
		public Map<String, Object> viewdef_params;

		
		public GoalStatus(DueStatus status) {
			this.status = status;
		}
		
		public GoalStatus(int days) {
			this.days = days;
		}
		
		public Map<String, Object> toMap() {
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("uid", uid);
			ret.put("focus", focus);
			ret.put("status_code", getOverdueStatus());
			ret.put("status", getOverdueStatusStr());
			ret.put("relevant_data", relevant_data);
			ret.put("last_done", last_done);
			if (selfLink != null) ret.put("selfLink", selfLink);
			if (viewdef != null) ret.put("viewdef", viewdef);
			if (viewdef_title != null) ret.put("viewdef_title", viewdef_title);
			if (viewdef_params != null) ret.put("viewdef_params", viewdef_params);
			ret.put("guidelines", "<i>" + guidelines + "</i>");
			return ret;
		}
		
		protected static int daysSince(PointInTime observed) {
			PointInTime obs = null;
			PointInTime today = PointInTime.today();
			if (observed.isDateSet()) {
				obs = new PointInTime(observed.getYear(), observed.getMonth(), observed.getDate());
			} else if (observed.isMonthSet()) {
				obs = new PointInTime(observed.getYear(), observed.getMonth());
				today = new PointInTime(today.getYear(), today.getMonth());
			} else {
				obs = new PointInTime(observed.getYear());
				today = new PointInTime(today.getYear());
			}
			return Days.daysBetween(obs, today).getDays();
		}
		
		protected String getOverdueStatusStr() {
			DueStatus status = getOverdueStatus();
			int daysSince = (last_done != null) ? daysSince(last_done) : 0;
			int dueInDays = days - daysSince;
			if (status == DueStatus.NOT_DONE) {
				return "Not done";
			} else if (status == DueStatus.OVERDUE) {
				return "<b style=\"color: red; font-weight: bold;\" title=\"" + (daysSince - days) + "days overdue\">OVERDUE</b>";
			} else if (status == DueStatus.DUE_SOON) {
				return "<b style=\"color: gold; font-weight: bold;\" title=\"Due in " + dueInDays + "d\">DUE SOON</b>";
			} else {
				return "<span title=\"Due in " + dueInDays + "d\">Not Due</span>";
			}
		}
		
		protected DueStatus getOverdueStatus() {
			// if a expicit status was declared, return it, otherwise compute one
			if (this.status != null) return this.status;
			int daysSince = (last_done != null) ? daysSince(last_done) : 0;
			int dueInDays = days - daysSince;
			if (last_done == null && days == Integer.MAX_VALUE) {
				return DueStatus.NOT_DONE;
			} else if (daysSince >= days || last_done == null) {
				return DueStatus.OVERDUE;
			} else if (dueInDays <= 45) {
				return DueStatus.DUE_SOON;
			} else {
				return DueStatus.NOT_DUE;
			}
		}
		
	}

}
