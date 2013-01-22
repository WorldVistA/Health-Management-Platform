package EXT.DOMAIN.cpe.vpr;

import java.util.Map;

import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;

class PatientEthnicity extends AbstractPOMObject{

	private Long id;
	private Patient patient;
    private String code;
    private String name;
    private String vuid;
    
    public PatientEthnicity() {
    	super(null);
    }
    
    @JsonCreator	
    public PatientEthnicity(Map<String, Object> vals) {
    	super(vals);
    }
    
	public Long getId() {
		return id;
	}

    @JsonBackReference("patient-ethnicity")
	public Patient getPatient() {
		return patient;
	}

	void setPatient(Patient patient) {
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
}
