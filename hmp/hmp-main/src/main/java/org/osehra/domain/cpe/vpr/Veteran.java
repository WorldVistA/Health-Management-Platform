package org.osehra.cpe.vpr;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;

import java.util.Map;

public class Veteran extends AbstractPOMObject{

	private Long id;
	private Long version;
	private String legacy;
	private Patient patient;
	private Integer lrdfn;
	private String serviceConnected; // TODO: verify this is a string?
	private String serviceConnectionPercent; // TODO: verify this is a string?

	// TODO Decide if we are keeping patient to object, reference chain error
	
	public Veteran() {
		super(null);
	}
	
    @JsonCreator
	public Veteran(Map<String, Object> vals) {
		super(vals);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getLegacy() {
		return legacy;
	}

	public void setLegacy(String legacy) {
		this.legacy = legacy;
	}

    @JsonBackReference("patient-veteran")
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Integer getLrdfn() {
		return lrdfn;
	}

	public void setLrdfn(Integer lrdfn) {
		this.lrdfn = lrdfn;
	}

	public String getServiceConnected() {
		return serviceConnected;
	}

	public void setServiceConnected(String serviceConnected) {
		this.serviceConnected = serviceConnected;
	}

	public String getServiceConnectionPercent() {
		return serviceConnectionPercent;
	}

	public void setServiceConnectionPercent(String serviceConnectionPercent) {
		this.serviceConnectionPercent = serviceConnectionPercent;
	}

	// static constraints = {
	// legacy(nullable:true)
	// lrdfn(nullable:true)
	// serviceConnected(nullable:true)
	// serviceConnectionPercent(nullable:true)
	// }
}
