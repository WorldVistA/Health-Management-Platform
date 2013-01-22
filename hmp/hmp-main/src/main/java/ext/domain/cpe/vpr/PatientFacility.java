package EXT.DOMAIN.cpe.vpr;

import java.util.Collections;
import java.util.Map;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;

public class PatientFacility extends AbstractPOMObject implements Comparable {

	private Long id;
    private Patient patient;
    private String code;
    private String name;
    private String systemId;
    private String localPatientId;
    private PointInTime earliestDate; // TODO look into modeling this as an IntervalOfTime
    private PointInTime latestDate;
    private boolean homeSite;
//    DateTimeZone timeZone
    
    public PatientFacility() {
    	super(null);
    }

    @JsonCreator    
    public PatientFacility(Map<String, Object> vals) {
    	super(vals);
    }
    
	public int compareTo(Object o) {
        return code.compareTo(((PatientFacility)o).getCode());
    }

    public Long getId() {
		return id;
	}

    @JsonBackReference("patient-facility")
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

	public String getSystemId() {
		return systemId;
	}

	public String getLocalPatientId() {
		return localPatientId;
	}

	public PointInTime getEarliestDate() {
		return earliestDate;
	}

	public PointInTime getLatestDate() {
		return latestDate;
	}

	public boolean isHomeSite() {
		return homeSite;
	}

	public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientFacility that = (PatientFacility) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (systemId != null ? !systemId.equals(that.systemId) : that.systemId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (systemId != null ? systemId.hashCode() : 0);
        return result;
    }
}
