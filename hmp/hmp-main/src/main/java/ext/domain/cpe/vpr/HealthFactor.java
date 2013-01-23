package org.osehra.cpe.vpr;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPatientObject;
import org.osehra.cpe.vpr.pom.IPatientObject;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * This describes the interactions between the patient and clinicians. Interaction includes both in-person and non-in-person encounters such as telephone and email communication.
 *
 * @see <a href="http://wiki.hitsp.org/docs/C83/C83-3.html#_Ref232966055">HITSP/C83 Encounter</a>
 */
public  class HealthFactor extends AbstractPatientObject implements IPatientObject {

	private static final String HEALTH_FACTOR = "Health Factor";

	private String summary;
	
    /**
     * The facility where the encounter occurred
     * @see "HITSP/C154 16.17 Facility ID"
     * @see "HITSP/C154 16.18 Facility Name"
     */
//    private PatientFacility facility;
    /**
     * The facility where the encounter occurred
     *
     * @see "HITSP/C154 16.17 Facility ID"
     */
    private String facilityCode;
    /**
     * The facility where the encounter occurred
     *
     * @see "HITSP/C154 16.18 Facility Name"
     */
    private String facilityName;
    /**
     * VistA visit number for this encounter, if applicable.
     */
    private String localId;
    /**
     * Free text field name of the Health Factor
     */
    private String name;
    /**
     * This is a coded value describing the type of the Encounter
     * <p>
     * Should be a CPT-4 code in the range 99200-99299 (E&M Code)
     * @see "HITSP/C154 16.02 Encounter Type"
     */
    private String comment;
    /**
     * The date and time of the HealthFactor
     */
    private PointInTime recorded;
    /**
     * The encounter the Health Factor is associated to.
     */
    private String encounterUid;

    @JsonCreator
	public HealthFactor(Map<String, Object> vals) {
		super(vals);
	}
	
	public HealthFactor() {
		super(null);
	}
    
    public String getKind() {
        return HEALTH_FACTOR;
    }

    public List getTaggers() {
//        if (uid)
//            return manualFlush { Tagger.findAllByUrl(uid) }
//        else
//            return []
    	//TODO - fix this.
    	return null;
    }

	@Override
	public String getSummary() {
		return summary;
	}

	public String getFacilityCode() {
		return facilityCode;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public String getLocalId() {
		return localId;
	}

	public String getName() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	public PointInTime getRecorded() {
		return recorded;
	}

	public String getEncounterUid() {
		return encounterUid;
	}
}
