package org.osehra.cpe.vpr;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;

public class PatientDisability extends AbstractPOMObject{

	private Long id;
	private Patient patient;
	private String printName;
	private boolean serviceConnected;
	private int serviceConnectionPercent;
	private int vaCode;
	
	public PatientDisability() {
		super(null);
	}
	
	@JsonCreator
	public PatientDisability(Map<String, Object> vals) {
		super(vals);
	}
	
	public Long getId() {
		return id;
	}

    @JsonBackReference("patient-disability")
	public Patient getPatient() {
		return patient;
	}

	void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getPrintName() {
		return printName;
	}

	public boolean isServiceConnected() {
		return serviceConnected;
	}

	public int getServiceConnectionPercent() {
		return serviceConnectionPercent;
	}

	public int getVaCode() {
		return vaCode;
	}
}
