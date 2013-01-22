package EXT.DOMAIN.cpe.vpr;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;

import java.util.List;
import java.util.Map;

public class VitalSign extends AbstractPatientObject implements IPatientObject {

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

    /**
     * Status for this observation, e.g., complete, preliminary.
     *
     * @see "HITSP/C154 14.04 Vital Sign Result Status"
     */
    private String resultStatusCode;

    private String resultStatusName;

    /**
     * The biologically relevant date/time for the observation.
     * <p/>
     * In VistA, this corresponds to the 'taken' time.
     *
     * @see "HITSP/C154 14.02 Vital Sign Result Date/Time"
     */
    private PointInTime observed;

    /**
     * In VistA, this corresponds to the 'entered' time.
     */
    private PointInTime resulted;

    /**
     * Reference to encounter, if known.
     */
    private Encounter encounter;

    private String locationCode;
    private String locationName;

    private String kind;
    private String summary;

    /**
     * Reference to a collection of vital signs
     */
    private VitalSignOrganizer organizer;
    private String organizerUid;

    /**
     * A coded representation of the observation performed.
     *
     * @see "HITSP/C154 14.03 Vital Sign Result Type"
     */
    private String typeCode;

    /**
     * Readable name of test/exam.
     */
    private String typeName;

    /**
     * The value of the result
     *
     * @see "HITSP/C154 14.05 Vital Sign Result Value"
     */
    private String result;
    /**
     * The units of measurement of the result
     *
     * @see "HITSP/C154 14.05 Vital Sign Result Value"
     */
    private String units;

    private String metricResult;

    private String metricUnits;

    /**
     * An abbreviated interpretation of the observation, e.g., normal, abnormal, high, etc.
     *
     * @see "HITSP/C154 14.06 Vital Sign Result Interpretation"
     */
    private String interpretationCode;

    private String interpretationName;

    /**
     * The low reference value for the observation
     *
     * @see "HITSP/C154 14.07 Vital Sign Result Reference Range"
     */
    private String low;
    /**
     * The high reference value for the observation
     *
     * @see "HITSP/C154 14.07 Vital Sign Result Reference Range"
     */
    private String high;

    // LOINC code
    private String method;

    // SNOMED CT code, if possible
    private String bodySite;

    @JsonCreator
    public VitalSign(Map<String, Object> vals) {
		super(vals);
	}
    
    public VitalSign() {
    	super(null);
    }

    /**
     * Report text.
     */
    private String document;

    public String getLocalId() {
        return localId;
    }

//      @JsonIgnore
    @JsonBackReference("vitalSignsOrganizer-vitalSign")
    public VitalSignOrganizer getOrganizer() {
        return organizer;
    }

    void setOrganizer(VitalSignOrganizer organizer) {
        this.organizer = organizer;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getResult() {
        return result;
    }

    public String getUnits() {
        return units;
    }

    public String getMetricResult() {
        return metricResult;
    }

    public String getMetricUnits() {
        return metricUnits;
    }

    public String getInterpretationCode() {
        return interpretationCode;
    }

    public String getInterpretationName() {
        return interpretationName;
    }

    public String getLow() {
        return low;
    }

    public String getHigh() {
        return high;
    }

    public String getMethod() {
        return method;
    }

    public String getBodySite() {
        return bodySite;
    }

    public String getDocument() {
        return document;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    /**
     * The biologically relevant date/time for the observation.
     * <p/>
     * In VistA, this corresponds to the 'taken' time.
     *
     * @see "HITSP/C154 14.02 Vital Sign Result Date/Time"
     */
    public PointInTime getObserved() {
        return observed;
    }

    /**
     * @see VitalSignOrganizer
     */
    public PointInTime getResulted() {
        return resulted;
    }

    /**
     * @see VitalSignOrganizer
     */
    public String getResultStatusCode() {
        return resultStatusCode;
    }

    public String getResultStatusName() {
        return resultStatusName;
    }

    public String getSummary() {
        StringBuffer s = new StringBuffer("");
    	if( typeName!=null ){
    		s.append(typeName);
    	}
    	
    	if(result != null){
    		s.append(" ");
    		s.append(result);
    	}
    	
        if (interpretationCode != null) {
            s.append(interpretationCode);
        }
        
        if (units != null) {
            s.append(" ");
            s.append(units);
        }
        return s.toString();
    }

    public String getKind() {
        return "Vital Sign";
    }

    public List getTaggers() {
//TODO - fix this    	
//        if (uid)
//            return manualFlush { Tagger.findAllByUrl(uid) }
//        else
//            return []
        return null;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public String getLocationName() {
        return locationName;
    }
    
    //Solr index will be created for typeName as qualified_name
    public String getQualifiedName() {
        return typeName;
    }

}
