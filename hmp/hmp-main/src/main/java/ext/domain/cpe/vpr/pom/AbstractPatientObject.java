package org.osehra.cpe.vpr.pom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import org.osehra.cpe.vpr.pom.PatientEvent.Change;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractPatientObject extends AbstractPOMObject implements IPatientObject {
	protected Map<String,Object> fCurDataMap;
	protected List<POMIndex<?>> indexes; // extracted indexes from annotations
	protected String pid;
	
	@JsonCreator
	public AbstractPatientObject(Map<String, Object> vals) {
		super(vals);
		
		// initalize the data and clear events, since objects are not considered modified
		// unless updates are made after the constructor
		if (vals != null) {
			validate();
			clearEvents();
		}
	}
	
	// global getters ------------------------
	
	/**
	 * All domain objects have a patient identifier
	 * Equivalent to getPatient().getUID();
	 */
	@JsonProperty("pid")
	public String getPid() {
		return pid;
	}
	
	// indexing ------------------------------
	
	/**
	 * Based on the declared index metadata, construct a list of indexed values.
	 * Subclasses can override or append to this list as needed.
	 */
	@JsonView(JSONViews.JDBView.class) // only include this node when serializing to disk
	public List<Map<String, Object>> getIDX() {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		if (indexes == null) {
			// lazy-load index metadata
			indexes = POMIndex.extractIndexes(this.getClass());
		}
		
		for (POMIndex<?> idx : indexes) {
			idx.calcValue(this, ret);
		}
		return ret;
	}
	
	protected void validate() {
		// values for UID and PID are required
//		if (uid == null || pid == null) {
//			// TODO: Is there a better exception class to use? Or make our own?
//			throw new IllegalArgumentException("UID and PID are required");
//		}
	}
	
	// events --------------------------------
	protected void generateEvents(List<PatientEvent<IPatientObject>> events, List<Change> changes) {
		// extenders can implement this as a convinient way to inject custom events
	}
	
	public void clearEvents() {
		fCurDataMap = getData(JSONViews.EventView.class);
		modified = false;
	}
	
	/**
	 * Returns a list of fields that have been modified since object construction and/or the last
	 * call to clearEvents().  Returns null if no changes.
	 */
	@JsonIgnore
	public List<Change> getModifiedFields() {
		if (fCurDataMap == null) return null;
		Map<String, Object> data = getData(JSONViews.EventView.class);
		ArrayList<PatientEvent.Change> changes = new ArrayList<Change>();
		for (String f : POMUtils.getMapChangedFields(fCurDataMap, data)) {
			Serializable oldVal = fCurDataMap.containsKey(f) ? fCurDataMap.get(f).toString() : null;
			Serializable newVal = (data.containsKey(f)) ? data.get(f).toString() : null;
			changes.add(new PatientEvent.Change(f, oldVal, newVal));
		}
		return changes;
	}
	
	@JsonIgnore
	public boolean isModified() {
		return modified;
	}
	
	/**
	 * This will return a list of events that represent changes made to this
	 * object since the last time clearEvents() was called (note that clearEvents() is
	 * called as part of the object initalization).
	 * 
	 * Its likely that after calling this method, you will want to "reset" the events by
	 * calling clearEvents() immediately afterward.
	 * @return
	 */
	@JsonIgnore
	public List<PatientEvent<IPatientObject>> getEvents() {
		List<PatientEvent<IPatientObject>> ret = new ArrayList<PatientEvent<IPatientObject>>();
		List<PatientEvent.Change> changes = getModifiedFields();
		
		// create the appropriate event(s)
		if (changes == null || changes.size() > 0) {
			PatientEvent.Type t = changes == null ? PatientEvent.Type.CREATE : PatientEvent.Type.UPDATE;
			ret.add(new PatientEvent<IPatientObject>(this, t, changes));
		}
		
		// let subclasses add/remove/modify events
		generateEvents(ret, changes);
		
		return ret;
	}
}
