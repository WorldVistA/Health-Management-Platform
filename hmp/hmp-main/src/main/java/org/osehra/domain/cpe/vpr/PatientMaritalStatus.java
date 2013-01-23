package org.osehra.cpe.vpr;

import java.util.Map;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;

public class PatientMaritalStatus extends AbstractPOMObject{

	private Long id;
    private Patient patient;
    private String code;
    private String name;
    private PointInTime fromDate;  // TODO: maybe model from/through dates as IntervalOfTime (need hibernate custom type to do so)?
    private PointInTime thruDate;
    
    public PatientMaritalStatus() {
    	super(null);
    }
    
    @JsonCreator
    public PatientMaritalStatus(Map<String, Object> vals) {
    	super(vals);
    }
    
    public Long getId() {
		return id;
	}

    @JsonBackReference("patient-marital-status")
	public Patient getPatient() {
		return patient;
	}

	void setPatient(Patient patient) {
		this.patient = patient;
	}

	public PointInTime getFromDate() {
		return fromDate;
	}

	public PointInTime getThruDate() {
		return thruDate;
	}

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }
}
