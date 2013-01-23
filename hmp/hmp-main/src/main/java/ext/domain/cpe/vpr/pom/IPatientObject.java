package org.osehra.cpe.vpr.pom;

import java.util.List;
import java.util.Map;

public interface IPatientObject extends IPOMObject {
	List<Map<String, Object>> getIDX();
	
	// events --------------------------------
	void clearEvents();
	List<PatientEvent.Change> getModifiedFields();
	List<PatientEvent<IPatientObject>> getEvents(); // TODO: This should probably be T and each POM Object should declare T.
	boolean isModified();
	
	// getters -------------------------------
	String getPid();
}
