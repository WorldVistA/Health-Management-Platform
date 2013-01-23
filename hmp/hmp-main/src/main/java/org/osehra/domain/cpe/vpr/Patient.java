package org.osehra.cpe.vpr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPatientObject;
import org.osehra.cpe.vpr.pom.PatientEvent;
import org.osehra.cpe.vpr.pom.POMIndex.MultiValueJDSIndex;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.Period;

/**
 * Demographics information for the patient.
 * 
 * @see <a
 *      href="http://wiki.hitsp.org/docs/C83/C83-3.html#_Ref232942628">HITSP/C83
 *      Personal Information</a>
 */
public class Patient extends AbstractPatientObject{
	
	private Long version;
	private String icn;
	private String familyName;
	private String givenNames;
	private String genderCode;
	private String genderName;
	private PointInTime dateOfBirth;
	private PointInTime died;
	private boolean sensitive = false;
	private String briefId;
	private String ssn;
	private String religionCode;
	private String religionName;
	private Veteran veteran;
	private PointInTime lastUpdated;
	private String domainUpdated;

	private Set<Alias> aliases;
	private Set<Address> addresses;
	private Set<PatientDisability> disabilities;
	private SortedSet<PatientFacility> facilities;
	private Set<PatientFlag> flags;
	private Set<Telecom> telecoms;
	private Set<PatientLanguage> languages;
	private Set<PatientEthnicity> ethnicities;
	private Set<PatientRace> races;
	private Set<PatientMaritalStatus> maritalStatuses;
	private Set<PatientExposure> exposures;
	private Set<PatientSupport> supports;

	public Patient(){
		super(null);
	}
	
	@JsonCreator
	public Patient(Map<String, Object> vals) {
		super(vals);
	}

	public void addToRaces(PatientRace race) {
		if (races == null) {
			races = new HashSet<PatientRace>();
		}
		races.add(race);
		race.setPatient(this);
	}

	public void addToEthnicities(PatientEthnicity ethnicity) {
		if (ethnicities == null) {
			ethnicities = new HashSet<PatientEthnicity>();
		}
		ethnicities.add(ethnicity);
		ethnicity.setPatient(this);
	}

	public void addToLanguages(PatientLanguage language) {
		if (languages == null) {
			languages = new HashSet<PatientLanguage>();
		}
		languages.add(language);
		language.setPatient(this);
	}

	public void addToDisabilities(PatientDisability disability) {
		if (disabilities == null) {
			disabilities = new HashSet<PatientDisability>();
		}
		disabilities.add(disability);
		disability.setPatient(this);
	}

	public void addToFacilities(PatientFacility patientFacility) {
		if (facilities == null) {
			facilities = new TreeSet<PatientFacility>();
		}
		facilities.add(patientFacility);
		patientFacility.setPatient(this);
	}

	public void addToMaritalStatuses(PatientMaritalStatus maritalStatus) {
		if (maritalStatuses == null) {
			maritalStatuses = new HashSet<PatientMaritalStatus>();
		}
		maritalStatuses.add(maritalStatus);
		maritalStatus.setPatient(this);
	}

	public void addToAddresses(Address address) {
		if (addresses == null) {
			addresses = new HashSet<Address>();
		}
		addresses.add(address);
	}

	public void addToAliases(Alias alias) {
		if (aliases == null) {
			aliases = new HashSet<Alias>();
		}
		aliases.add(alias);
		alias.setPatient(this);
	}

	public void addToTelecoms(Telecom telecom) {
		if (telecoms == null) {
			telecoms = new HashSet<Telecom>();
		}
		telecoms.add(telecom);
	}

	public void addToFlags(PatientFlag flag) {
		if (flags == null) {
			flags = new HashSet<PatientFlag>();
		}
		flags.add(flag);
		flag.setPatient(this);
	}

	public void addToSupports(PatientSupport support) {
		if (supports == null) {
			supports = new HashSet<PatientSupport>();
		}
		supports.add(support);
		support.setPatient(this);
	}

	public void addToExposures(PatientExposure exposure) {
		if (exposures == null) {
			exposures = new HashSet<PatientExposure>();
		}
		exposures.add(exposure);
		exposure.setPatient(this);
	}

    public String getIcn() {
		return icn;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getGivenNames() {
		return givenNames;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public String getGenderName() {
		return genderName;
	}

	public PointInTime getDateOfBirth() {
		return dateOfBirth;
	}

	public PointInTime getDied() {
		return died;
	}

	public boolean isSensitive() {
		return sensitive;
	}

	public String getBriefId() {
		return briefId;
	}

	public String getSsn() {
		return ssn;
	}

    public String getReligionCode() {
		return religionCode;
	}

	public String getReligionName() {
		return religionName;
	}

	@JsonManagedReference("patient-veteran")
	public Veteran getVeteran() {
		return veteran;
	}

	public PointInTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(PointInTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getDomainUpdated() {
		return domainUpdated;
	}

    @JsonManagedReference("patient-alias")
	public Set<Alias> getAliases() {
		return aliases;
	}

    public Set<Address> getAddresses() {
		return addresses;
	}

    @JsonManagedReference("patient-disability")
	public Set<PatientDisability> getDisabilities() {
		return disabilities;
	}

    @JsonManagedReference("patient-facility")
	public SortedSet<PatientFacility> getFacilities() {
		return facilities;
	}

    @JsonManagedReference("patient-flag")
	public Set<PatientFlag> getFlags() {
		return flags;
	}

    public Set<Telecom> getTelecoms() {
		return telecoms;
	}

    @JsonManagedReference("patient-language")
	public Set<PatientLanguage> getLanguages() {
		return languages;
	}

    @JsonManagedReference("patient-ethnicity")
	public Set<PatientEthnicity> getEthnicities() {
		return ethnicities;
	}

    @JsonManagedReference("patient-race")
    public Set<PatientRace> getRaces() {
		return races;
	}

    @JsonManagedReference("patient-marital-status")
    public Set<PatientMaritalStatus> getMaritalStatuses() {
		return maritalStatuses;
	}

    @JsonManagedReference("patient-exposure")
    public Set<PatientExposure> getExposures() {
		return exposures;
	}

    @JsonManagedReference("patient-support")
    public Set<PatientSupport> getSupports() {
		return supports;
	}

    @JsonIgnore
	public Integer getAge() {
		return calculateAge(dateOfBirth, died);
	}

	public static Integer calculateAge(PointInTime dateOfBirth, PointInTime died) {
		if (dateOfBirth == null) {
			return null;
		}
		if (died != null) {
			return new Period(dateOfBirth, died).getYears();
		} else {
			return new Period(dateOfBirth, PointInTime.today()).getYears();
		}
	}

	@Override
	public String toString() {
		// TODO - convert from Groovy
		// Map ids = [:]
		// if (id) ids.id = id ids.pids
		// * = getPatientIds()
		// return "${this.class.name}${ids.toMapString()}"
		StringBuffer buff = new StringBuffer();
		buff.append(this.getClass().getName());
		Map<String, Object> ids = new HashMap<String, Object>();
		ids.put("pids", getPatientIds());
		buff.append(ids);
		return buff.toString();
	}
	
	public PatientFacility getHomeFacility() {
		if (facilities == null) {
			return null;
		}

		for (PatientFacility facility : facilities) {
			if (facility.isHomeSite()) {
				return facility;
			}
		}
		return null;
	}

    @JsonIgnore
	public String getFullName() {
		return familyName + ", " + givenNames;
	}

    public String getLocalPatientIdForSystem(String systemId) {
        for (PatientFacility f : facilities) {
            if (systemId.equalsIgnoreCase(f.getSystemId()) && f.getLocalPatientId() != null) {
                return f.getLocalPatientId();
            }
        }
        return null;
    }

    public String getLocalPatientIdForFacility(String facilityCode) {
        for (PatientFacility f : facilities) {
            if (facilityCode.equalsIgnoreCase(f.getCode()) && f.getLocalPatientId() != null) {
                return f.getLocalPatientId();
            }
        }
        return null;
    }

    @JsonIgnore
    @MultiValueJDSIndex(name="patient-ids", subfield="")
	public Set<String> getPatientIds() {
		List<String> ptIds = new ArrayList<String>();
		if (pid != null) {
			// as long as this turns into a urn:va:vpr:123-ish format we are ok here
			ptIds.add(pid);
		}
		if (icn != null) {
			ptIds.add(icn);
		}
		if (ssn != null) {
			// are SSN's unique in the va?
			ptIds.add(ssn);
		}

		if (facilities != null) {
			for (PatientFacility fac : facilities) {
				if (fac.getSystemId() != null && fac.getLocalPatientId() != null) {
					ptIds.add(fac.getSystemId() + ";" + fac.getLocalPatientId());
				}
				if (fac.getCode() != null && fac.getLocalPatientId() != null) {
					ptIds.add(fac.getCode() + ";" + fac.getLocalPatientId());
				}
			}
		}
		return new TreeSet<String>(ptIds);
	}

//	@Override
//	public String getFlagText(String name) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public boolean hasFlag(String name) {
//		// TODO Auto-generated method stub
//		return false;
//	}
}
