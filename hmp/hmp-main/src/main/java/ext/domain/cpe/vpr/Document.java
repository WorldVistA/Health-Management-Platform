package EXT.DOMAIN.cpe.vpr;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * TODOC: document this class
 * <p/>
 * For HITSP, advance directives are kept in a separate place. If crisis &
 * warning notes are not associated with a specific encounter, Documents related
 * to procedures are stored as results (Clinical Procedures, Surgical Reports,
 * Laboratory Reports).
 */
public class Document extends AbstractPatientObject implements IPatientObject {
    private String author;
    private String urgency;
    private PointInTime enteredDateTime;
    private String attending;
	private String kind;
    private String summary;
    private ArrayList<DocumentText> text;

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

    private String localId;

    /**
     * Reference to the encounter to which this document is related.
     */
//    private Encounter encounter;
    private String encounterUid;
    private String encounterName;
    /**
     * Date/time of this document
     */
    private PointInTime referenceDateTime;
    /**
     * i.e. Progress Note, Discharge Summary
     */
    private String documentTypeCode;
    private String documentTypeName;
    private String documentClass;
    private String localTitle;
    private Map<String, Object> nationalTitle;
    // LOINC nationalTitleCode
    /**
     * For VistA: subject text
     */
    private String subject;
    /**
     * current document status, For VistA: status in TIU
     */
    private String status;

    /**
     * XML text of the full note, including sections and addenda.
     */
    private String content;
    
    public Document() {
        super(null);
    }

    @JsonCreator
    public Document(Map<String, Object> vals) {
        super(vals);
    }

    public List<DocumentText> getText() {
        return text;
    }
    
    public String getFacilityName() {
		return facilityName;
	}

	public String getFacilityCode() {
		return facilityCode;
	}

    public String getAuthor() {
        if(text == null) {
        	return null;
        }
        String result = null;
        for (DocumentText txt : text) {
        	ArrayList<DocumentClinician> clinicians = txt.getClinicians();
        	if(clinicians != null) {
        		for(DocumentClinician clinician: clinicians) {
        			 if (clinician.getRole() == "A") {
                         return clinician.getClinician().getName();
                     }
        		}
        	}
        }
        return result;
    }

    public String getCosigner() {
        if(text == null) {
        	return null;
        }
        String result = null;
        for (DocumentText txt : text) {
        	ArrayList<DocumentClinician> clinicians = txt.getClinicians();
        	if(clinicians != null) {
        		for(DocumentClinician clinician: clinicians) {
        			 if (clinician.getRole() == "X") {
                         return clinician.getClinician().getName();
                     }
        		}
        	}
        }
        return result;
    }

    public String getSummary() {
        return localTitle;
    }

	public String getKind() {
		// return kind;
		return getDocumentTypeName();
	}

    public List getTaggers() {
        // if (uid != null)
        // return manualFlush { Tagger.findAllByUrl(uid) }
        // else
        // return []
        // TODO - resolve this.
        return null;
    }

    public String getLocalId() {
        return localId;
    }

    public PointInTime getReferenceDateTime() {
        return referenceDateTime;
    }

    public String getEncounterUid() {
		return encounterUid;
	}

	public String getEncounterName() {
		return encounterName;
	}

	public String getDocumentTypeCode() {
		return documentTypeCode;
	}

	public Map<String, Object> getNationalTitle() {
		return nationalTitle;
	}

	public String getDocumentTypeName() {
		return documentTypeName;
	}

	public String getDocumentClass() {
        return documentClass;
    }

    public String getLocalTitle() {
        return localTitle;
    }

    public String getUrgency() {
		return urgency;
	}

	public PointInTime getEnteredDateTime() {
		return enteredDateTime;
	}

	public String getAttending() {
		return attending;
	}
// Taken out because nationalTitle breaks JSON importing; Complex JSON data doesn't play nice with String data type.
//    public String getNationalTitle() {
//        return nationalTitle;
//    }
//
//    public void setNationalTitle(String nationalTitle) {
//        this.nationalTitle = nationalTitle;
//    }

    public String getSubject() {
        return subject;
    }

    public String getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    public Set<DocumentClinician> getClinicians() {
    	Set<DocumentClinician> clinicians = new LinkedHashSet<DocumentClinician>();
    	if(text != null) {
    		for (DocumentText txt: text) {
    			if(txt.getClinicians() != null) {
    				for(DocumentClinician clinician: txt.getClinicians()) {
    					clinicians.add(clinician);
    				}
    			}
    		}
    	}
        return clinicians;
    }
}
