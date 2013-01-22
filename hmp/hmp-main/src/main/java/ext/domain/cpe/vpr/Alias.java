package EXT.DOMAIN.cpe.vpr;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonView;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.JSONViews;

public class Alias extends AbstractPOMObject {

	private Long id;
	private Long version;
	private String fullName;
	private String familyName;
	private String givenNames;
	private Patient patient;
	
	public Alias() {
		super(null);
	}
	
	@JsonCreator
	public Alias(Map<String, Object> vals) {
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getGivenNames() {
		return givenNames;
	}

	public void setGivenNames(String givenNames) {
		this.givenNames = givenNames;
	}

    @JsonBackReference("patient-alias")
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	@Override
	@JsonView(JSONViews.WSView.class) // dont store in DB
	public String getSummary() {
		return this.fullName;
	}
	
}
