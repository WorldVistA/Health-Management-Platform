package org.osehra.cpe.vpr;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;

import org.osehra.cpe.vpr.pom.AbstractPOMObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Support includes the patient's sources of support, such as immediate family, relatives and/or guardians. This includes
 * next of kin, caregivers, support organizations, and key contacts relative to healthcare decisions. Support providers
 * may include providers of healthcare related services, such as a personally controlled health record, or registry of
 * emergency contacts.
 *
 * @see <a href="http://wiki.hitsp.org/docs/C83/C83-3.html#_Ref232942923">HITSP/C83 Support</a>
 */
public class PatientSupport extends AbstractPOMObject{
	
	private Long id;
	private Long version;
	private Patient patient;
    private String name;
    private Address address;
    private String relationshipName;
    private String relationshipCode;
    private String contactTypeCode;
    private String contactTypeName;
    private Set<Telecom> telecoms;
    
    public PatientSupport() {
    	super(null);
    }

    @JsonCreator
	public PatientSupport(Map<String, Object> vals) {
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

    @JsonBackReference("patient-support")
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

    public String getRelationshipCode() {
        return relationshipCode;
    }

    public void setRelationshipCode(String relationshipCode) {
        this.relationshipCode = relationshipCode;
    }

    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    public String getContactTypeCode() {
        return contactTypeCode;
    }

    public void setContactTypeCode(String contactTypeCode) {
        this.contactTypeCode = contactTypeCode;
    }

    public String getContactTypeName() {
        return contactTypeName;
    }

    public void setContactTypeName(String contactTypeName) {
        this.contactTypeName = contactTypeName;
    }

    public Set<Telecom> getTelecoms() {
		return telecoms;
	}

	public void setTelecoms(Set<Telecom> telecoms) {
		this.telecoms = telecoms;
	}

    public void addToTelecoms(Telecom telecom) {
        if (this.telecoms == null) {
            this.telecoms = new HashSet<Telecom>();
        }
        this.telecoms.add(telecom);
    }

	public String toString() {
        return name;
    }

//    static hasMany = [telecoms: Telecom]
//
//    static constraints = {
//        name(blank: false)
//        address(nullable: true)
//        relationship(nullable: true)
//    }
//
//    static mapping = {
//        address lazy: false
//        relationship lazy: false
//        contactType lazy: false
//        telecoms lazy: false
//    }
}
