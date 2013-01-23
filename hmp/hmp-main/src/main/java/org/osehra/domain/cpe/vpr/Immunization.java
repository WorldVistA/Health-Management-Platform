package org.osehra.cpe.vpr;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;
import org.osehra.cpe.vpr.pom.AbstractPatientObject;
import org.osehra.cpe.vpr.pom.IPatientObject;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Immunization extends AbstractPatientObject implements IPatientObject {
    private String summary;
    private String localId;
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
    private String name;
    private PointInTime administeredDateTime;
    private Boolean contraindicated; 
    private String location;
    private String seriesName;
    private String seriesUid;
    private String reactionName;
    private String reactionUid;
    private String comments;
    private String cptCode;
    private String cptName;
    private Clinician performer;
    private String performerUid;
    private String encounterUid;

    public String getPerformerUid() {
		return performerUid;
	}

	public String getEncounterUid() {
		return encounterUid;
	}

	@JsonCreator
	public Immunization(Map<String, Object> data) {
    	super(data);
	}
    
    public Immunization()
    {
    	super(null);
    }

	public String getSummary() {
        return summary!=null?summary:name;
    }

	public String getLocalId() {
		return localId;
	}

	public String getName() {
		return name;
	}

	public PointInTime getAdministeredDateTime() {
		return administeredDateTime;
	}

    public Boolean getContraindicated() {
        return contraindicated;
    }

	public String getLocation() {
		return location;
	}

	public String getSeriesUid() {
		return seriesUid;
	}

	public String getReactionUid() {
		return reactionUid;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public String getReactionName() {
		return reactionName;
	}

	public String getComments() {
		return comments;
	}

	public String getCptCode() {
		return cptCode;
	}

	public String getCptName() {
		return cptName;
	}

	public Clinician getPerformer() {
		return performer;
	}

	public String getKind() {
        return "Immunization";
    }

    public List getTaggers() {
//        if (uid)
//            return manualFlush { Tagger.findAllByUrl(uid) }
//        else
//            return []
    	//TODO - fix this 
    	return null;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }
}
