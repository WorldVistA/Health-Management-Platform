package org.osehra.cpe.vpr;

import java.util.Collections;
import java.util.Map;

import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;

public class PatientFlag extends AbstractPOMObject{

	private Long id;
	private Patient patient;

    private String name;
    private String text;

    public PatientFlag() {
    	super(Collections.<String, Object> emptyMap());
    }
    
    @JsonCreator
    public PatientFlag(Map<String, Object> vals) {
    	super(vals);
    }
    
    public String toString() {
        return name;
    }

	public Long getId() {
		return id;
	}

    @JsonBackReference("patient-flag")
	public Patient getPatient() {
		return patient;
	}

	void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}
}
