package org.osehra.cpe.vpr;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPatientObject;
import org.osehra.cpe.vpr.pom.IPatientObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;


public class Observation extends AbstractPatientObject implements IPatientObject{

	private static final String CLINICAL_OBSERVATION = "Clinical Observation";

    private String facilityCode;

    private String facilityName;

    private String localId;

    private String typeCode;

    private String typeName;

    private String result;

    private String units;

    private String interpretation;

    private PointInTime observed;

    private PointInTime resulted;

    private String resultStatus;

    private String methodCode;

    private String methodName;

    private String bodySiteCode;

    private String bodySiteName;

    private String locationCode;
    private String locationName;

    private String comment;

    private String vaStatus;

    private String qualifierText;
    
    private List<ObservationQualifier> qualifiers;

	public Observation() {
		super(null);
	}

	@JsonCreator
	public Observation(Map<String, Object> vals) {
		super(vals);
	}

    // organizers

    public void addToQualifiers(ObservationQualifier qualifier) {
    	if(this.qualifiers == null) {
    		qualifiers = new ArrayList<ObservationQualifier>();
    	}
    	qualifiers.add(qualifier);
    }
    
    public String getQualifierText() {
        StringBuffer x = new StringBuffer();
        if(qualifiers == null) {
        	return null;
        }
        for (ObservationQualifier qualifier : qualifiers) {
			x.append(qualifier.getType());
			x.append(": ");
			x.append(qualifier.getName());
			x.append(" ");
		}
        return x.toString().trim();
    }

    public void setQualifierText(String qualifierText) {
    	this.qualifierText = getQualifierText();
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

	public String getLocalId() {
		return localId;
	}

	public String getKind() {
		return CLINICAL_OBSERVATION;
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

	public String getInterpretation() {
		return interpretation;
	}

	public PointInTime getObserved() {
		return observed;
	}

	public PointInTime getResulted() {
		return resulted;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public String getMethodCode() {
		return methodCode;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getBodySiteCode() {
		return bodySiteCode;
	}

	public String getBodySiteName() {
		return bodySiteName;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getComment() {
		return comment;
	}

	public String getVaStatus() {
		return vaStatus;
	}

	public List<ObservationQualifier> getQualifiers() {
		return qualifiers;
	}

	@Override
	public String getSummary() {
        StringBuffer x = new StringBuffer();
        x.append(typeName);
        x.append(" ");
        x.append(result);
        
        if (units != null) {
            x.append(" ");
        	x.append(units);
        }
        if (interpretation != null && (!interpretation.equals('N'))){
        	x.append(" (" + interpretation + ")");
        }
        return x.toString();
    }

}
