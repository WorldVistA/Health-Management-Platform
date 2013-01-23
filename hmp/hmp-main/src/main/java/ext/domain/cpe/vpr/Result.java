package org.osehra.cpe.vpr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPatientObject;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.osehra.cpe.vpr.termeng.Concept;
import org.osehra.cpe.vpr.termeng.TermEng;

import java.util.*;

/**
 * This includes data about current and historical test results from laboratory or other diagnostic testing performed on the patient.
 * <p/>
 *
 * @see ResultOrganizer
 * @see <a href="http://wiki.hitsp.org/docs/C83/C83-3.html#_Ref232965713">HITSP/C83 Test Result</a>
 */
public class Result extends AbstractPatientObject implements IPatientObject {

    private String summary;

    private String localId;

    /**
     * The facility where the result occurred
     *
     * @see "HITSP/C154 16.17"
     */
    private String facilityCode;

    /**
     * The facility where the result occurred
     *
     * @see "HITSP/C154 16.18"
     */
    private String facilityName;

    private String groupName;

    private String groupUid;

    /**
     * Specific category of this set of results. <example> Ex: Laboratory
     */
    //private ResultCategory category;
    private String categoryCode;

    private String categoryName;

    /**
     * Status for this observation, e.g., complete, preliminary.
     *
     * @see "HITSP/C154 15.04 Result Status"
     */

    private String resultStatusCode;

    private String resultStatusName;

    /**
     * The biologically relevant date/time for the observation.
     *
     * @see "HITSP/C154 15.02 Result Date/Time"
     */
    private PointInTime observed;

    private PointInTime resulted;

    /**
     * Textual name of specimen.
     */
    private String specimen;

    /**
     * Order number, if known.
     */
    private String orderId;

    /**
     * Uid of encounter, if known.
     */
    private String encounterUid;

    private String comment;

    /**
     * Reference to a collection of results
     */
    private Set<ResultOrganizer> organizers;

    /**
     * A coded representation of the observation performed.
     * <p/>
     * LOINC for lab, CPT for radiology
     *
     * @see "HITSP/C154 15.03 Result Type"
     */
    private String typeCode;

    /**
     * Readable name of test/exam.
     */
    private String typeName;

    /**
     * local/print/display name of the test (ie: NA for Sodium)
     */
    private String displayName;

    /**
     * The value of the result
     *
     * @see "HITSP/C154 15.05 Result Value"
     */
    private String result;
    /**
     * The units of measurement of the result
     *
     * @see "HITSP/C154 15.05 Result Value"
     */
    private String units;
    /**
     * An abbreviated interpretation of the observation, e.g., normal, abnormal, high, etc.
     *
     * @see "HITSP/C154 15.06 Result Interpretation"
     */
    private String interpretationCode;

    private String interpretationName;
    /**
     * The low reference value for the observation
     *
     * @see "HITSP/C154 15.07 Result Reference Range"
     */
    private String low;
    /**
     * The high reference value for the observation
     *
     * @see "HITSP/C154 15.07 Result Reference Range"
     */
    private String high;

    // LOINC code
    private String method;

    // SNOMED CT code, if possible
    private String bodySite;

    /**
     * Report text.
     * <p/>
     * (radiology, pathology, micro, etc.)
     */
    private String document;
    
	private Set<String> lncCodes;
	private Set<Map<String,String>> lncCodes2;

    public Result() {
        super(null);
    }

    @JsonCreator
    public Result(Map<String, Object> vals) {
        super(vals);
    }

    public String getLocalId() {
        return localId;
    }

    @JsonIgnore
    public Set<ResultOrganizer> getOrganizers() {
        return organizers;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getResult() {
        return result;
    }
    
    /**
     * Returns a Float or Integer if either can be parsed.
     */
    public Number getResultNumber() {
    	return POMUtils.parseNumber(getResult());
    }

    public String getUnits() {
        return units;
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

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    @JsonIgnore
    public ResultOrganizer getAccession() {
        if (organizers == null) return null;
        for (ResultOrganizer organizer : organizers) {
            if (organizer.getOrganizerType().equals("accession") ||
                    organizer.getOrganizerType().equals("GENERAL RADIOLOGY") ||
                    organizer.getOrganizerType().equals("panel")) {
                return organizer;
            }
        }
        return null;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupUid() {
        return groupUid;
    }

    /**
     * The biologically relevant date/time for the observation.
     *
     * @see ResultOrganizer
     * @see "HITSP/C154 15.02 Result Date/Time"
     */
    public PointInTime getObserved() {
        return observed;
    }

    public PointInTime getResulted() {
        return resulted;
    }

    public String getResultStatusCode() {
        return resultStatusCode;
    }

    public String getResultStatusName() {
        return resultStatusName;
    }

    public String getSpecimen() {
        return specimen;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getQualifiedName() {
        String qn = typeName;
        if (getSpecimen() != null) {
            qn += " (" + getSpecimen() + ")";
        }
        return qn;
    }

    public String getSummary() {
        String s = getQualifiedName() + " " + result;
        if (interpretationCode != null) {
            s += "<em>" + getDisplayInterpretationCode() + "</em>";
        }
        if (units != null) {
            s += " " + units;
        }
        return s;
    }
    
    public boolean isAbnormal() {
    	return (getInterpretationCode() != null);
    }

    private String getDisplayInterpretationCode() {
        return interpretationCodeDisplayValues.get(getInterpretationCode());
    }
//    public List getTaggers() {
////        if (uid)
////            return manualFlush { Tagger.findAllByUrl(uid) }
////        else
////            return []
//        return null;
//        //TODO - fix this
//    }


    public String getOrderId() {
        return orderId;
    }

    public String getComment() {
        return comment;
    }

    public String getEncounterUid() {
        return encounterUid;
    }
    
    public void addToOrganizers(ResultOrganizer o) {
        if (organizers == null) {
            organizers = new HashSet();
        }
        if (!organizers.contains(o)) {
            organizers.add(o);
            if (o.getResults() == null || !o.getResults().contains(this)) {
                o.addToResults(this);
            }
        }
    }

    public void removeFromOrganizers(ResultOrganizer o) {
        if (organizers == null) return;
        organizers.remove(o);
        if (o.getResults().contains(this))
            o.removeFromResults(this);
    }
    
    
    /*
     * getLNCCodes() is not compatable with indexing at the moment, so this builds on top of it
     */
    public Set<Map<String,String>> getLNCCodes2() {
    	if (this.lncCodes2 != null) {
    		return this.lncCodes2;
    	}
    	
    	TermEng eng = TermEng.getInstance();
    	if (eng == null) return null; // for unit test saftey
    	Set<Map<String,String>> ret = new LinkedHashSet<Map<String,String>>();
    	for (String s : getLNCCodes()) {
    		Map<String,String> m = new HashMap<String,String>();
    		m.put("uid", s);
    		m.put("description", eng.getDescription(s));
    		ret.add(m);
    	}
    	return this.lncCodes2 = ret;
    }
    
    public Set<String> getLNCCodes() {
    	// if they were already computed/stored, return it
    	if (this.lncCodes != null) {
    		return this.lncCodes;
    	}
    	
    	// otherwise compute the values
    	TermEng eng = TermEng.getInstance();
    	Set<String> ret = new LinkedHashSet<String>();
    	
    	// Index the original typeCode and VUID (if any) 
    	String typeCode = getTypeCode();
    	Object vuid = getProperty("vuid");
    	if (typeCode != null) {
    		ret.add(typeCode);
    	}
    	if (vuid != null) {
    		ret.add(vuid.toString());
    	}
    	
    	if (eng != null) {
   		Concept c = eng.getConcept(typeCode);
   		if (c != null) {
   			for (String s : c.getAncestorSet()) {
   				// Filter out some noisy ancestors
   				if (!s.equals("urn:src:V-LNC") && !s.startsWith("urn:lnc:MTHU")) {
   					ret.add(s);
   				}
   			}
   		}
    	}
    	this.lncCodes = ret;
    	return ret;
    }
    
    

    private static final Map<String, String> interpretationCodeDisplayValues;

    // we could potentially move this kind of logic to a "KindService(s)" if that is less smelly
    private static final Map<String, String> categoryCodeToKind;

    static {
        Map<String, String> codesToDisplayVals = new HashMap<String, String>();
        codesToDisplayVals.put("urn:hl7:observation-interpretation:LL", "L*");
        codesToDisplayVals.put("urn:hl7:observation-interpretation:L", "L");
        codesToDisplayVals.put("urn:hl7:observation-interpretation:H", "H");
        codesToDisplayVals.put("urn:hl7:observation-interpretation:HH", "H*");
        interpretationCodeDisplayValues = Collections.unmodifiableMap(codesToDisplayVals);

        Map<String, String> codesToKinds = new HashMap<String, String>();
        codesToKinds.put("CH", "Lab Test");
        codesToKinds.put("MI", "Microbiology");
        codesToKinds.put("CY", "Pathology");
        codesToKinds.put("RAD", "Imaging");
        codesToKinds.put("SP", "Surgical Pathology");
        categoryCodeToKind = Collections.unmodifiableMap(codesToKinds);
    }

    public String getKind() {
        // we could potentially move this kind of logic to a "KindService(s)" if that is less smelly
        //categoryCodeToKind[category?.code] ?: "Unknown"
        if (getCategoryName() == null) {
            return "Unknown";
        }
        return getCategoryName();
    }

    private Set<Modifier> modifiers;
}
