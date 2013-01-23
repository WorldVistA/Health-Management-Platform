package org.osehra.cpe.vpr;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonView;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;
import org.osehra.cpe.vpr.pom.JSONViews;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;

public class PatientExposure extends AbstractPOMObject{
	private Long id;
	private Patient patient;
    private String code;
    private String name;
    private String vuid;

    public PatientExposure() {
    	super(null);
    }
    
    @JsonCreator
    public PatientExposure(Map<String, Object> vals) {
    	super(vals);
    }
    
    public Long getId() {
		return id;
	}

    @JsonBackReference("patient-exposure")
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getVuid() {
		return vuid;
	}

	@Override
	@JsonView(JSONViews.WSView.class) // dont store in DB
	public String getSummary() {
		return this.uid + " " + this.name;
	}
}
