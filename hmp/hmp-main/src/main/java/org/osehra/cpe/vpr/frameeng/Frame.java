package org.osehra.cpe.vpr.frameeng;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.feed.atom.Link;
import org.osehra.cpe.vpr.HMPApp;
import org.osehra.cpe.vpr.Medication;
import org.osehra.cpe.vpr.Observation;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.PatientAlert;
import org.osehra.cpe.vpr.Result;
import org.osehra.cpe.vpr.UidUtils;
import org.osehra.cpe.vpr.frameeng.FrameAction.ObsDateRequestAction;
import org.osehra.cpe.vpr.frameeng.FrameAction.ObsRequestAction;
import org.osehra.cpe.vpr.frameeng.FrameAction.RetractAction;
import org.osehra.cpe.vpr.frameeng.FrameJob.FrameTask;
import org.osehra.cpe.vpr.frameeng.IFrameEvent.InvokeEvent;
import org.osehra.cpe.vpr.frameeng.IFrameTrigger.InvokeTrigger;
import org.osehra.cpe.vpr.frameeng.IFrameTrigger.MedOrderedTrigger;
import org.osehra.cpe.vpr.frameeng.IFrameTrigger.NewObsTrigger;
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.pom.PatientEvent;
import org.osehra.cpe.vpr.pom.jds.JdsTemplate;
import org.osehra.cpe.vpr.queryeng.HMPAppInfo;
import org.osehra.cpe.vpr.queryeng.Table;
import org.osehra.cpe.vpr.queryeng.ViewDef;
import org.osehra.cpe.vpr.queryeng.ViewParam;
import org.osehra.cpe.vpr.queryeng.ViewParam.AsArrayListParam;
import org.osehra.cpe.vpr.queryeng.ViewParam.SimpleViewParam;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria;
import org.osehra.cpe.vpr.termeng.Concept;
import org.osehra.cpe.vpr.termeng.TermEng;
import org.osehra.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * 
 * Things we need:
 * - Dynamic Frames (Triggers with variables...)
 * - Frame resources (similar to viewDef resources)
 * 
 * TODO: Migrate validate() and getAppInfo() up from ViewDefs into here?
 * TODO: Since frame instances are re-used, could resources (dao, etc.) be initalized? 
 * -- Maybe constructor takes a FrameInitalizer object that has a getResources()
 * -- Maybe instead of constructor, we have a init(FrameInitalizer) method.
 * -- Maybe we call it a SystemEvent(type=initalize)
 *  
 * @since 8/27/2012
 */
public abstract class Frame implements IFrame, HMPApp {
	
	public static class FrameReference {
		String authors;
		String title;
		String source;
		String pmid;
		URL href;
		
		@Override
		public String toString() {
			return authors + ". " + title + ". " + source + ".";
		}
	}
	
	public static class FrameInitException extends RuntimeException {
		private static final long serialVersionUID = 6874524095916747774L;
		private IFrame frame;
		
		public FrameInitException(String msg, IFrame f) {
			super(msg);
			this.frame = f;
		}
		
		public FrameInitException(IFrame frame, Throwable cause) {
			super("Error while initalizing frame: " + frame.getID(), cause);
			this.frame = frame;
		}
	}
	
	public static class FrameExecException extends Exception {
		private static final long serialVersionUID = 4151010493860383366L;
		private IFrame frame;

		public FrameExecException(String msg, Throwable cause) {
			super(msg, cause);
		}
		
		public FrameExecException(String msg, IFrame f) {
			super(msg);
			this.frame = f;
		}
		
		public FrameExecException(String msg, Throwable cause, IFrame f) {
			this(msg, cause);
			this.frame = f;
		}
	}
	
	// Triggers ---------------------------------------------------------------
	private String id;
	private String name;
	private String type;
	private URI resource;
	private Map<String, Object> meta = new HashMap<String, Object>();
	private List<IFrameTrigger<?>> triggers = new ArrayList<IFrameTrigger<?>>();
	private Set<ViewParam> params = new LinkedHashSet<ViewParam>();
	private List<FrameReference> refs = new ArrayList<FrameReference>();
	private Map<String, Object> appinfo;
	private boolean initalized = false;
	
	public IFrameTrigger<?> evalTriggers(IFrameEvent<?> event) {
		for (IFrameTrigger<?> trig : getTriggers()) {
			if (trig.eval(event)) {
				return trig;
			}
		}
		return null;
	}
	
	// Initalization/Validation -----------------------------------------------
	/**
	 * Idea here is to be sort of a secondary constructor, capabilities include:
	 * 1) let frames initalize resources into member variables (dao's, templates, etc.)
	 * 2) let frames be 'reinialized' at some point if necessary?
	 * 3) let frames declare additional triggers dyanmically (based on user parameter values, etc.)
	 * 4) let bridge frames do additional stuff they may need to (open connections, load metadata, etc.)
	 *  
	 * TODO: should this be an event instead of a task/job?
	 * TODO: How to ensure persisted configuration variables are initalized/load into task params?
	 * 
	 * Frame registry should be responsible for ensuring this is called.
	 * @param task
	 */
	public final void init(FrameTask task) {
		try {
			doInit(task);
		} catch (Exception ex) {
			throw new FrameInitException(this, ex);
		}
		this.initalized = true;
	}
	
	protected boolean isInitalized() {
		return this.initalized;
	}
	
	public void validate(FrameTask task) throws FrameInitException {
		if (!isInitalized()) {
			throw new FrameInitException("Frame was not initalized", this);
		}
	}
	
	protected void doInit(FrameJob task) throws Exception {
		// does nothing by default...
	}
	
	// Public getters (IFrame Interface) --------------------------------------
	
	public List<IFrameTrigger<?>> getTriggers() {
		return this.triggers;
	}
	
	public String getID() {
		return (String) getAppInfo().get("id");
	}
	
	public String getName() {
		return (String) getAppInfo().get("name");
	}
	
	public List<FrameReference> getReferences() {
		return refs;
	}
	
	public URI getResource() {
		return this.resource;
	}
	
	public Map<String, Object> getMeta() {
		return this.meta;
	}
	
	public Set<ViewParam> getParamDefs() {
		return params;
	}
	
	public Set<ViewParam> getParamDefs(Class<?> clazz) {
		Set<ViewParam> ret = new LinkedHashSet<ViewParam>();
		for (ViewParam p : getParamDefs()) {
			if (clazz.isAssignableFrom(p.getClass())) {
				ret.add(p);
			}
		}
		return ret;
	}
	
	public Map<String, Object> getParamDefaultVals() {
		Map<String, Object> ret = new HashMap<String, Object>();
		// default params initialization
		for(ViewParam p : getParamDefs()) {
			Map<String, Object> vals = p.getDefaultValues();
			if (vals != null) {
				ret.putAll(vals);
			}
		}
		return ret;
	}
	
	@Override
	public Map<String, Object> getAppInfo() {
		if (appinfo == null) {
			Map<String, Object> viewInfo = getParamDefaultVals();
			HashMap<String, Object> ret = new HashMap<String, Object>();
			
			// get the annotation, use it to fill in any values not declared in the param
			HMPAppInfo annotation = getClass().getAnnotation(HMPAppInfo.class);
			Component annotation2 = getClass().getAnnotation(Component.class);
			
			// get the name from: 1) declared name, 2) ViewInfoParam, 3) annotation, 4) class name
			String name = this.name;
			if (name == null) {
				name = (String) viewInfo.get("view.name");
			}
			if (name == null && annotation != null) {
				name = annotation.title();
			}
			if (name == null) {
				name = getClass().getName();
			}
			
			// get the ID from: 1) declared ID, 2) @Component annotation, 3) class name
			String id = this.id;
			if (id == null && annotation2 != null) {
				id = annotation2.value();
			}
			if (id == null) {
				id = getClass().getName();
			}
			
			String type = "org.osehra.cpe.frame";
			if (this.type != null) {
				type = this.type;
			} else if (annotation != null) {
				type = annotation.value();
			} else if (this instanceof ViewDef) {
				type = "org.osehra.cpe.viewdef";
			}
			
			// return the results
			ret.put("type", type);
			ret.put("name", name);
			ret.put("id", id);
			ret.put("code", ret.get("id"));
			ret.put("resource", this.resource);
			appinfo = ret;
		}
		
		return appinfo;
	}

	
	// protected setters ------------------------------------------------------
	
	protected <T extends IFrameTrigger<?>> T addTrigger(T trig) {
		if (isInitalized()) {
			throw new IllegalStateException("Cannot add triggers after a frames been initalized.");
		}
		this.triggers.add(trig);
		return trig;
	}
	
	protected void setID(String id) {
		this.id = id;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	protected void setResource(URI resource) {
		this.resource = resource;
	}
	
	protected void addMeta(String key, Object val) {
		this.meta.put(key, val);
	}
	
	protected void addMeta(Map<String, Object> data) {
		this.meta.putAll(data);
	}
	
	protected void addReference(FrameReference ref) {
		this.refs.add(ref);
	}
	
	protected void declareParam(ViewParam param) {
		// look for duplicate (equals) params and replace the existing one with the new one.
		// I kind of thought this should be handled automatically given the equals() implementation, alas, it does not.
		for (ViewParam p : params) {
			if (p.equals(param)) {
				params.remove(p);
				params.add(param);
				return;
			}
		}
		
		// no equal ViewParam exists yet, add it.
		params.add(param);
	}
	
	protected void declareParam(String key, Object defaultVal) {
		declareParam(new ViewParam.SimpleViewParam(key, defaultVal));
	}
	
	public abstract void exec(FrameTask task) throws Exception;
	
	public static class ProtocolFrame extends Frame {
		private String pid;
		private JsonNode document;
		private Map<IFrameTrigger, ObjectNode> triggerToGoals = new HashMap();

		public ProtocolFrame(JsonNode document) {
			this(null, document);
		}
		
		public ProtocolFrame(URI resource, JsonNode document) {
			this.document = document;
			if (this.document == null) return;
			setID(this.document.findValue("id").asText());
			setName(this.document.findValue("name").asText());
			setResource(resource);
			setType("org.osehra.cpe.protocol");
			this.pid = (this.document.has("pid")) ? this.document.findValue("pid").asText() : null;
			
			// setup the candidate-based triggers
			ArrayNode candidates = (ArrayNode) this.document.findValue("candidates");
			if (candidates != null) {
				for (JsonNode cand : candidates) {
					IFrameTrigger trig = buildTrigger(cand.get("trigger"));
					addTrigger(trig);
				}
			}
			
			// setup the goal-based triggers if this is a patient instance frame
			/*
			ArrayNode goals = (ArrayNode) this.document.findValue("goals");
			if (goals != null && this.pid != null) {
				for (JsonNode goal : goals) {
					IFrameTrigger trig = buildTrigger(goal.get("trigger"));
					triggerToGoals.put(trig, (ObjectNode) goal);
					addTrigger(new IFrameTrigger.PatientIDTriggerWrapper(this.pid, trig));
				}
			}
			*/
			
			// add everything else as metadata
			Iterator<Entry<String, JsonNode>> itr = this.document.fields();
			Set<String> ignoreList = new HashSet<String>();
			ignoreList.add("candidates");
			ignoreList.add("goals");
			
			while (itr.hasNext()) {
				Entry<String, JsonNode> obj = itr.next();
				if (!ignoreList.contains(obj.getKey())) {
					addMeta(obj.getKey(), obj.getValue().textValue());
				}
			}
		}
		
		private static IFrameTrigger buildTrigger(JsonNode trig) {
			if (trig != null) {
				String classStr = trig.get("class").asText();
				if (classStr.equals("org.osehra.cpe.vpr.frameeng.IFrameTrigger.NewVitalSignTrigger")) {
					ArrayNode params = (ArrayNode) trig.get("params");
					if (params != null && params.size() > 0) {
						return new IFrameTrigger.NewVitalSignTrigger(params.get(0).asText());
					}
				} else if (classStr.equals("org.osehra.cpe.vpr.frameeng.IFrameTrigger.LabResultRangeTrigger")) {
					ArrayNode params = (ArrayNode) trig.get("params");
					String name = null;
					Double lo = null;
					Double hi = null;
					if (params != null && params.size() > 0) {
						name = params.get(0).asText();
					}
					if (params != null && params.size() > 1) {
						lo = params.get(1).asDouble();
					}
					if (params != null && params.size() > 2) {
						hi = params.get(2).asDouble();
					}
					if (name != null && lo != null) {
						return new IFrameTrigger.LabResultRangeTrigger(name, lo);
					} else if (name != null && lo != null && hi != null) {
						return new IFrameTrigger.LabResultRangeTrigger(name, lo, hi);
					}
				}
			}
			throw new IllegalStateException("Unable to parse node into trigger def: " + trig);
		}
		
		@Override
		public void exec(FrameTask ctx) {
			// get the corresponding goal
			ObjectNode goal = this.triggerToGoals.get(ctx.getFrameTrigger());
			
			// what type trigger/goal?
			if (goal == null) {
				// this patient is a candidate for this protocol
				PatientEvent event = (PatientEvent) ctx.getTriggerEvent();
				ctx.addAction(new FrameAction.NewInstanceFrameAction(event.getPID()));
			} else if (goal != null && goal.findValue("type").asText().equals("obs_frequency")) {
				// TODO: re-evaluate protocol, invoke DiabetesViewDef
			}
		}
	}
	
	/**
	 * FrameParams could be:
	 * 1) system default: default lab value range, etc.
	 * 2) instance specific: AbnormalLabFrame could be instanciated multiple times and the abnormal range could be part of that instantiation
	 * 3) patient specific: peristed somewhere per-patient.
	 * 4) event/context data: passed in from URL or event payload
	 * TODO: params derived dynamically from context? PID from PatientEvent or CallEvent.getData().get('pid'), etc.
	 * TODO: Patient Population-specific values? 
	 */
	public abstract static class FrameParam extends ViewParam {
		
		public abstract static class PatientObjectParam extends FrameParam {
			/**
			 * TODO: Idea: instead of the common CallTrigger + PatientOBjectTrigger combo,
			 * what if this runtime parameter attempted to derive the triggering object dynamically
			 * from the successful trigger?  IF its a PatientObjectTrigger, then return trig.getSource()
			 * if its a CallEvent where event.getData().containsKey("pid") then use the generic
			 * DAO to fetch the value.
			 */
		}
	}
	
	@Component(value="org.osehra.cpe.vpr.frameeng.IV2POFrame")
	public static class IV2POFrame extends Frame {
		
		public IV2POFrame() {
			declareParam(new SimpleViewParam("candidateMedClasses", "")); /*h2 antagonissts, proton pump inhibitors, etc */
			addTrigger(new IFrameTrigger.InvokeTrigger<Medication>(this, Medication.class, "viewdefactions"));
			addTrigger(new IFrameTrigger.MedOrderedTrigger());
			addTrigger(new IFrameTrigger.InvokeTrigger<Medication>(this, Medication.class, "org.osehra.cpe.vpr.rowaction"));
		}

		@Override
		public void exec(FrameTask ctx) {
			IGenericPatientObjectDAO dao = ctx.getResource(IGenericPatientObjectDAO.class);
			IFrameEvent<Medication> evt = (IFrameEvent<Medication>) ctx.getTriggerEvent();
			Medication triggerMed = evt.getSource();
			String pid = triggerMed.getPid();
			
			// if no trigger med was found, or its not an active infusion med, quit.
			if (triggerMed == null || !triggerMed.getKind().equals("Infusion") || !triggerMed.getVaStatus().equals("ACTIVE")) {
				return;
			}
			
			// look for oral meds
			QueryDef qry = new QueryDef();
			qry.addCriteria(QueryDefCriteria.where("vaStatus").is("ACTIVE"));
			List<Medication> meds = dao.findAllByQuery(Medication.class, qry, Table.buildRow("pid", pid));
			
			Medication oralMed = null;
			for (Medication med : meds) {
				if (!med.getKind().equals("Infusion")) {
					oralMed = med;
					break;
				}
			}

			// if an oral med was found, issue an alert.
			if (oralMed != null) {
				if (evt instanceof InvokeEvent && ((InvokeEvent) evt).getEntryPoint().equals("org.osehra.cpe.vpr.rowaction")) {
					ctx.addAction(new FrameAction.OrderActionMenuItem("MODIFY", "Change route to PO", triggerMed.getUid()));
				} else {
					PatientAlert aa = new PatientAlert(this, "iv2po", pid, "IV2PO Switch Candidate", "This patient has active PO and IV meds, consider switching IV meds to PO to reduce infection risk, etc."); 
					aa.addLink(oralMed.getUid(), "REFERENCE");
					aa.addLink(triggerMed.getUid(), "TRIGGER");
					aa.addSubAction(new ObsRequestAction(pid, "Task someone to evaluate IV2PO switch on: <input type=\"text\" value=\"9/17/2012\" size=\"10\">", "?"));
					aa.addSubAction(new ObsRequestAction(pid, "Schedule PO med switch", "?"));
					aa.addSubAction(new ObsRequestAction(pid, "Patient not a PO candidate because: x.  Reevaluate in <input type=\"text\" size=\"2\"> days", "?"));
					aa.addSubAction(new ObsRequestAction(pid, "Patient not a PO candidate because: y.  Reevaluate in <input type=\"text\" size=\"2\"> days", "?"));
					ctx.addAction(aa);
				}
			}
		}
		
	}
	
	@Component(value="org.osehra.cpe.vpr.frameeng.TeratogenicMedsFrame")
	public static class TeratogenicMedsFrame extends Frame {
		// TODO: Externalize these?
		private static String TITLE = "Potentially Teratogenic Medication";
		private static String DESC = "<p>Concern has been raised about use of this medication during pregnancy.  Approximately 6% of US pregnancies are exposed to potentially teratogenic medications. </p>"
				+ "<p>1) Pregnancy status should be determined. Discuss use of this medication on the context of risks to the mother and child of untreated disease. Potential benefits may warrant use of the drug in pregnant women despite risks. </p>"
				+ "<p>2) The patient must be provided contraceptive counseling on potential risk vs. benefit of taking this medication if she were to become pregnant.  </p>";
		
		MedOrderedTrigger trig1 = addTrigger(new MedOrderedTrigger());
		InvokeTrigger<Medication> trig2 = addTrigger(new InvokeTrigger<Medication>(this, Medication.class, "viewdefactions"));
		NewObsTrigger trig3 = addTrigger(new NewObsTrigger("urn:sct:307429007", "urn:sct:236886002"));
		
		public TeratogenicMedsFrame() {
			setName(TITLE);
			
			FrameReference ref = new FrameReference();
			ref.authors = "Andrade SE, Gurwitz JH, Davis RL, et al";
			ref.title = "Prescription drug use in pregancy";
			ref.source = "Am J Obstet Gynecol. 2004;191(2):398-407";
			ref.pmid = "15343213";
			addReference(ref);
			
			ref = new FrameReference();
			ref.authors = "Lee E, Maneno MK, Smith L, et al";
			ref.title = "National patterns of medication use during pregnancy";
			ref.source = "Pharmacoepidemiol DrugSaf. 2006;15(8):537-45";
			ref.pmid = "16700083";
			addReference(ref);
		}

		@Override
		public void exec(FrameTask task) throws FrameExecException {
			TermEng eng = TermEng.getInstance();
			Patient pat = null;
			String pid = null;
			Medication med = null;
			

			if (trig1.isTriggerOf(task)) {
				PatientEvent<Medication> evt = trig1.getEventOf(task);
				pat = evt.getPatient();
				pid = evt.getPID();
				med = evt.getSource();
			} else if (trig2.isTriggerOf(task)) {
				InvokeEvent<Medication> evt = trig2.getEventOf(task);
				med = evt.getSource();
				pid = med.getPid();
				pat = task.getResource(IPatientDAO.class).findByAnyPid(pid);
			} else if (trig3.isTriggerOf(task)) {
				// retract alert(s) and return
				PatientEvent<Observation> evt = trig3.getEventOf(task);
				task.addAction(new RetractAction(evt.getPID(), getID()));
				return;
			}
			
			// TODO: if med is not active/pending/scheduled then retract
			
			
			if (pat == null) {
				IPatientDAO dao = task.getResource(IPatientDAO.class);
				pat = dao.findByAnyPid(pid);
			}
			
			// if pt is Male, under 12yo or over 50yo then quit.
			if (pat!=null && pat.getGenderCode()!=null && (pat.getGenderCode().equals("urn:va:pat-gender:M") ||
					pat.getAge() < 12 || pat.getAge() > 50)) {
				return;
			}

			// look for any mitigating factors, if found, quit.
			IGenericPatientObjectDAO dao = task.getResource(IGenericPatientObjectDAO.class);
			if (dao.findByUID("urn:va:::obs:urn:sct:307429007") != null || dao.findByUID("urn:va:::obs:urn:sct:236886002") != null) {
				return;
			}
			
			// med must be active/pending
			if (!med.isActive() && !med.isPending()) {
				return;
			}
			
			// TODO: How to check if FDA categories D or X
			
			// if this med CI'ed with Pregnancy in NDFRT, alert
			// TODO: move the concept matching into the trigger.
			Concept pregnancy = eng.getConcept("urn:ndfrt:N0000010195");
			Map<String,String> pregnancyRels = pregnancy.getRelationships();
			for (String code : med.getRXNCodes()) {
				for (String code2 : pregnancyRels.keySet()) {
					if (code.equals(code2)) {
						String parts[] = med.getUid().split(":");
						String id = "teratogenic:" + ((parts.length == 6) ? parts[4]+":"+parts[5] : "");
						PatientAlert aa = new PatientAlert(this, id, pid, TITLE, DESC);
						aa.addLink(med.getUid(), "TRIGGER");
						aa.addSubAction(new ObsDateRequestAction(pid, "Pt. is post-menopausal? Estimated date of last menstrual period?", "urn:sct:307429007" /*after menopause*/));
						aa.addSubAction(new ObsRequestAction(pid, "Pt. has had a hysterectomy?", "urn:sct:236886002" /*Hysterectomy*/));
						aa.addSubAction(new ObsRequestAction(pid, "Pt. has IUD?", "urn:sct:268460000" /*Intrauterine contraceptive device*/));
						aa.addSubAction(new ObsRequestAction(pid, "Pt. provided contraceptive counseling on risks.", "urn:sct:398780007" /*Contraception education*/));
						aa.addSubAction(new ObsDateRequestAction(pid, "Last menstrual period sensation?", "urn:sct:289899004" /*Finding of sensation of periods*/));
						task.addAction(aa);
					}
				}
			}
			

			
		}
		
	}
	
	@Component(value="org.osehra.cpe.vpr.frameeng.DrugAllergyFrame")
	public static class DrugAllergyFrame extends Frame {

		public DrugAllergyFrame() {
		}
		
		@Override
		public void exec(FrameTask ctx) {
			// TODO Auto-generated method stub
			
		}
	
	}
	
	@Component(value="org.osehra.cpe.vpr.frameeng.Vancomycin72HrFrame")
	public static class Vancomycin72HrFrame extends Frame {
		
		public Vancomycin72HrFrame() {
			addTrigger(new IFrameTrigger.MedOrderedTrigger("urn:rxnorm:11124" /* Vancomycin */));
		}

		@Override
		public void exec(FrameTask ctx) {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\nVANCO ORDER\n\n\n\n\n\n\n\n\n\n\n");
		}
		
	}
	
	@Component(value="org.osehra.cpe.vpr.frameeng.RenalDosingAdjustmentFrame")
	public static class RenalDosingAdjustmentFrame extends Frame {
		public RenalDosingAdjustmentFrame() {
			setName("Renal Drug Dosing Adjustment");
			declareParam(new SimpleViewParam("include.classes", "urn:vadc:AM400,urn:vadc:CV701" /*QUINOLONES,THIAZIDES/RELATED DIURETICS (TODO: Hacked in for demo)*/));
			declareParam(new SimpleViewParam("include.ingredients", "urn:ndfrt:N0000007606" /* Quinolones*/));
			declareParam(new AsArrayListParam("include.ingredients"));
			declareParam(new AsArrayListParam("include.classes"));
			declareParam(new SimpleViewParam("scr.threshold", 1.5f));
			addTrigger(new IFrameTrigger.InvokeTrigger<Medication>(this, Medication.class, "viewdefactions"));
			addTrigger(new IFrameTrigger.MedOrderedTrigger()); // trigger on all meds
			addTrigger(new IFrameTrigger.LabResultTrigger("CREATININE"));
			/* Triggering on: 
			 * Quinolones (ciprofloxacin: urn:ndfrt:N0000147503, gatifloxacin, gemifloxacin, grepafloxacin, levofloxacin, lomefloxacin, moxifloxacin, nalidixic acid, norfloxacin, ofloxacin)
			 * Others?
            Purine nucleosides				(acyclovir, cidofovir, famciclovir, ganciclovir, ribavirin, valacyclovir, valganciclovir)			
            Antigout agents				(allopurinol, colchicines, colchicines-probenecid, probenecid, sulfinpyrazone)			
            Adamantine antivirals				(amantadine, rimantidine)			
            aminoglycosides			
            All penicillins				(aminopenicillins, antipseudomonal penicillins, beta-lactamase inhibitors, natural penicillins, penicillinase resistant penicillins)			
            Beta-lactamase inhibitors				(amox-clav, amp-sulbact, pip-tazo, ticar-clav)			
            All cephalosporins			
            Quinolones				(ciprofloxacin, gatifloxacin, gemifloxacin, grepafloxacin, levofloxacin, lomefloxacin, moxifloxacin, nalidixic acid, norfloxacin, ofloxacin)			
            H2 antagonists				(cimetidine, famotidine, nizatidine, ranitidine)			
            Azole antifungals				(clotrimazole, fluconazole, itraconazole, ketoconazole, miconazole, posaconazole, voriconazole)			Carbapenems				(ertapenem, imipenem, meropenem)			Macrolides				(azithromycin, clarithromycin,dirithromycin, erythromycin, troleandomycin)			Echinocandins				(anidulafungin, caspofungin, micafungin)		</ul>      	</p>      	      	<p>Default excluded routes:		<ul>			TOP				VAG				EYE				OPT			</ul>      	</p>      </alert_description>      	      <trigger_events>              	<p>The renal drug screening alert is triggered to run anytime a drug is ordered, discontinued, put on hold, or canceled, or a SCr lab is updated.</p>                         </trigger_events>            <retract_events>              	<p>The renal drug screening alert is retracted when:</p>               		<ul>                             			A candidate medication order is canceled, discontinued or put on hold.                                 			When a newer alert fires updating the previous alert.                                                     			The patient's CrCl increases about the threshold value.                                     		</ul>      </retract_events>            <other_conditions>             	<p>The alert contains additional parameters that can be set at each site.  One parameter allows an age restriction to be turned on.  By default, the alert is configured to generate alerts on all patients but can be configured to ignore patients under 18 or who have an unknown age.  Another parameter allows the site to exclude patients with unknown CrCl values. Another parameter allows the site to set the CrCl threshold value. Another parameter sets the SCr threshold which is used if the CrCl cannot be calculated. Other parameters allows the site to exclude medications based on concept ids, medication text, or route.</p>      </other_conditions>",
			 * 
			 */
			
		}

		@Override
		public void exec(FrameTask ctx) {
			// TODO Auto-generated method stub
			IGenericPatientObjectDAO dao = ctx.getResource(IGenericPatientObjectDAO.class);
			JdsTemplate tpl = ctx.getResource(JdsTemplate.class);
			
			List<String> includeClasses = (List<String>) AsArrayListParam.toArrayList(ctx.getParamObj("include.classes"));
			List<String> includeIngredients = (List<String>) AsArrayListParam.toArrayList(ctx.getParamObj("include.ingredients"));
			
			// TODO: parameter calculation is not correct yet.
//			List<String> includeClasses = (List<String>) ctx.getParamObj("include.classes");
//			List<String> includeIngredients = (List<String>) ctx.getParamObj("include.ingredients");
			Float scrThreshold = (Float) ctx.getParamObj("scr.threshold");
			
			IFrameEvent<IPatientObject> evt = (IFrameEvent<IPatientObject>) ctx.getTriggerEvent();
			String pid = evt.getSource().getPid();
			
			// get triggering med (if any), or find any active candidate meds
			List<Medication> meds = new ArrayList<Medication>();
			if (ctx.getFrameTrigger() instanceof IFrameTrigger.MedOrderedTrigger) {
				meds.add((Medication) evt.getSource());
			} else if (evt instanceof InvokeEvent) {
				meds.add((Medication) evt.getSource());
			} else {
				meds = tpl.getForList(Medication.class, "/vpr/" + pid + "/index/medication/?filter=eq(medStatus,\"urn:sct:55561003\")");
			}
			
			// get triggering or most recent SCR (if any)
			Result scr = null;
			if (ctx.getFrameTrigger() instanceof IFrameTrigger.LabResultTrigger) {
				scr = (Result) ctx.getTriggerEvent().getSource();
			} else {
				scr = tpl.getForObject("/vpr/" + pid + "/last/lab-type?range=CREATININE*", Result.class);
			}
			
			// abort if the patient does not have renal insuficiency 
			if(scr==null) {return;}
			Number n = scr.getResultNumber();
			if (n == null || n.floatValue() <= scrThreshold) {
				return;
			}
			
			// abort if the patient does not have any candidate meds
			List<Medication> candidateMeds = new ArrayList<Medication>(meds);
			for (Medication med : meds) {
				if (!isCandidateMed(med, includeClasses, includeIngredients)) {
					candidateMeds.remove(med);
				}
			}
			if (candidateMeds.isEmpty()) {
				return;
			}
			
			// alert! generate alert and link to all relevant meds
			String desc = "The patients last SCR of: " + n + " is abnormal and indicates this drug may need renal dosage adjustment";
			PatientAlert alert = new PatientAlert(this, "renaldrugassesment", pid, "Renal Drug Assessment", desc);
			for (Medication med : candidateMeds) {
				alert.addLink(med.getUid(), "TRIGGER");
			}
			ctx.addAction(alert);
			
		}
		
		private boolean isCandidateMed(Medication med, List<String> includeClasses, List<String> includeIngredients) {
			Set<String> classes = med.getDrugClassCodes();
			for (String s : includeClasses) {
				if (classes.contains(s)) {
					return true;
				}
			}
			
			classes = med.getRXNCodes();
			for (String s : classes) {
				if (TermEng.getInstance().isa(s, includeIngredients)) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	@Component(value="org.osehra.cpe.vpr.frameeng.ActionButtonFrame")
	public static class ActionButtonFrame extends Frame {
		public ActionButtonFrame() {
			addTrigger(new IFrameTrigger.InvokeTrigger<IPatientObject>(this, IPatientObject.class, "org.osehra.cpe.vpr.rowaction"));
		}

		@Override
		public void exec(FrameTask task) {
			IGenericPatientObjectDAO dao = task.getResource(IGenericPatientObjectDAO.class);
			InvokeEvent<IPatientObject> evt = (InvokeEvent<IPatientObject>) task.getTriggerEvent();
			IPatientObject obj = evt.getSource();
			if (obj == null) {
				String uid = (String) evt.getParams().get("uid");
				obj = dao.findByUID(UidUtils.getDomainClassByUid(uid), uid);
			}
			if (obj instanceof Medication) {
				Medication med = (Medication) obj;
				if (med.getVaStatus().equals("ACTIVE")) {
					// TODO: if renewby is approaching, renew action?
					String renewby = (String) evt.getParams().get("renewBy");
					if (renewby != null) {
						PointInTime pit = new PointInTime(renewby);
						if (pit.after(PointInTime.now().subtractDays(30)) && pit.before(PointInTime.now().addDays(30))) {
							task.addAction(new FrameAction.OrderActionMenuItem("RENEW", "Renew this Med", med.getUid()));
						}
					}
					task.addAction(new FrameAction.OrderActionMenuItem("DISCONTINUE", "D/C this Active Med", med.getUid()));
					task.addAction(new FrameAction.OrderActionMenuItem("MODIFY", "Modify this Active Med", med.getUid()));
				} else if (med.getVaStatus().equals("PENDING")) {
					task.addAction(new FrameAction.OrderActionMenuItem("CANCEL", "Cancel this Pending Med", med.getUid()));
				} else if (med.getVaStatus().equals("DISCONTINUED")) {
					task.addAction(new FrameAction.OrderActionMenuItem("RENEW", "Renew this D/C'ed Med", med.getUid()));
				}
				
				
				if (med.getRXNCodes().contains("urn:ndfrt:N0000008142")) {
					task.addAction(new FrameAction.URLActionMenuItem("http://www.warfarindosing.org", "WarfarinDosing.org calculator", "", "Go to WarfarinDosing.org in a new window"));
				}
				
			} else if (obj instanceof Result) {
				Result lab = (Result) obj;
				if (lab.getTypeName().contains("CHOLESTEROL")) {
					task.addAction(new FrameAction.URLActionMenuItem("http://hp2010.nhlbihin.net/atpiii/calculator.asp", "Framingham Score Calculator"));
				}
			}
		}
		
	}
	
	@Component(value="org.osehra.cpe.vpr.frameeng.InfobuttonActionFrame")
	public static class InfobuttonActionFrame extends Frame {
		
		private IPatientDAO patdao;
		private IGenericPatientObjectDAO dao;
		private OpenInfoButtonLinkGenerator gen;
		private SAXReader reader = new SAXReader();

		public InfobuttonActionFrame() {
			// TODO: Declare a fetch timeout?
			addTrigger(new IFrameTrigger.InvokeTrigger<IPatientObject>(this, IPatientObject.class, "org.osehra.cpe.vpr.rowaction"));
		}
		
		@Override
		protected void doInit(FrameJob task) throws ParserConfigurationException {
			gen = task.getResource(OpenInfoButtonLinkGenerator.class);
			dao = task.getResource(IGenericPatientObjectDAO.class);
			patdao = task.getResource(IPatientDAO.class);
		}

		@Override
		public void exec(FrameTask task) throws Exception {
			// determine trigger object/patient
			InvokeEvent<IPatientObject> evt = (InvokeEvent<IPatientObject>) task.getTriggerEvent();
			IPatientObject obj = evt.getSource();
			if (obj == null) {
				String uid = (String) evt.getParams().get("uid");
				obj = dao.findByUID(UidUtils.getDomainClassByUid(uid), uid);
			}
			Patient pat = patdao.findByAnyPid(obj.getPid());
			
			// generate url
			String url = null;
			if (obj instanceof Medication) {
				Medication med = (Medication) obj;
				// TODO: generate a medication review infobutton link
			} else if (obj instanceof Result) {
				Result lab = (Result) obj;
				url = buildInfobuttonURL(gen, pat, lab.getTypeCode(), lab.getTypeName(), "LABRREV", "2.16.840.1.113883.6.1");
			}
			
			// fetch the URL
			if (url != null) {
				fetchInfobuttonURL(task, url + "&transform");
			}
		}
		
		private void fetchInfobuttonURL(FrameTask task, String url) throws DocumentException {
			Document doc = reader.read(url);
			Element root = doc.getRootElement();
			
			for (Iterator<Element> i = root.elementIterator("feed"); i.hasNext();) {
				Element feed = i.next();
				String title = feed.elementText("title");
				String subtitle = feed.elementText("subtitle");
				
				for (Iterator<Element> j = feed.elementIterator("entry"); j.hasNext();) {
					Element entry = j.next();
					String etitle = entry.elementText("title");
					String elink = entry.element("link").attributeValue("href");
					
					String hint = String.format("Open in new window: %s: %s (%s)", title, subtitle, etitle); 
					task.addAction(new FrameAction.URLActionMenuItem(elink, etitle, title, hint));
				}
			}
		}
		
		private String buildInfobuttonURL(OpenInfoButtonLinkGenerator gen,
				Patient pat, String searchCode, String searchText,
				String searchContext, String searchCodeSet) {
			// build the parameters for the link generator
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (pat != null) {
				map.put("age", pat.getAge());
				map.put("gender", pat.getGenderCode());
			}
			map.put("context", searchContext);
			map.put("searchCodeSet", searchCodeSet);
			map.put("searchText", searchText);
			map.put("searchCode", searchCode);
			
			// TODO: hacky fix the search code+gender code to remove the URN:XXX: values so infobuttons work
			Object code = map.get("searchCode");
			if (code != null && code.toString().startsWith("urn:")) {
				map.put("searchCode", code.toString().split(":")[2]);
			}
			code = map.get("gender");
			if (code != null && code.toString().startsWith("urn:")) {
				map.put("gender", code.toString().split(":")[3]);
			}
			
			// run the link generator, if it returns a value, add it to the results.
			Link link = gen.generateLinkFromMap(map);
			return (link != null) ? link.getHref() : null;
		}
	}
	
	public static abstract class AbstractMultiEventFrame extends Frame {
		/**
		 * Idea: the standard exec() method could use introspection to delegate to a 
		 * different exec() method with the exact event signature.  Might make authoring easier?
		 */
	}
}
