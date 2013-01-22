package EXT.DOMAIN.cpe.vpr;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Problem extends AbstractPatientObject implements IPatientObject {

    private static final String PROBLEM = "Problem";

    private String localId;
    private String predecessor;
    private String successor;
    private Patient patient;
    private String facilityCode;
    private String facilityName;
    private String locationCode;
    private String locationName;
    private String service;
    private String providerName;
    private String providerCode;
    private String problemType;
    private String problemText;
    private String code; // what code does this refer to?
    private String icdCode;
    private String icdName;
    private String statusCode;
    private String statusName;
    private String acuityCode;
    private String acuityName;
    private String history;
    private Boolean unverified;
    private Boolean removed;
    private PointInTime entered;
    private PointInTime updated;
    private PointInTime onset;
    private PointInTime resolved;
    private Boolean serviceConnected;
    private List<ProblemComment> comments;

    public Problem() {
        super(null);
    }

    @JsonCreator
    public Problem(Map<String, Object> vals) {
        super(vals);
    }

    public String getLocalId() {
        return localId;
    }

    public String getPredecessor() {
        return predecessor;
    }

    public String getSuccessor() {
        return successor;
    }

    public String getLocationCode() {
		return locationCode;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getService() {
        return service;
    }

	public String getProblemType() {
        return problemType;
    }

    public String getProviderName() {
		return providerName;
	}

	public String getProviderCode() {
		return providerCode;
	}

    public String getProblemText() {
        return problemText;
    }

    public String getCode() {
        return code;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getAcuityCode() {
		return acuityCode;
	}

	public String getAcuityName() {
		return acuityName;
	}

	public String getHistory() {
        return history;
    }

    public Boolean getUnverified() {
        return unverified;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public PointInTime getEntered() {
        return entered;
    }

    public PointInTime getUpdated() {
        return updated;
    }

    public PointInTime getOnset() {
        return onset;
    }

    public PointInTime getResolved() {
        return resolved;
    }

    public Boolean getServiceConnected() {
    	return (serviceConnected == null)?Boolean.FALSE:serviceConnected;
    }

    public List<ProblemComment> getComments() {
        return comments;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getKind() {
        // we could potentially move this kind of logic to a "KindService(s)" if that is less smelly
        return PROBLEM;
    }

    @Override
    public String getSummary() {
        return problemText;
    }

    public List getTaggers() {
//        if (uid)
//            return manualFlush { Tagger.findAllByUrl(uid) }
//        else
//            return []
        return null;
        //TODO - fix this
    }

    public String getIcdCode() {
        return icdCode;
    }

    public String getIcdName() {
        return icdName;
    }
}
