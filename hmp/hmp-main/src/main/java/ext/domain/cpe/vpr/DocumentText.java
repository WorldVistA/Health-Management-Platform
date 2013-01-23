package org.osehra.cpe.vpr;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;

import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

public class DocumentText extends AbstractPOMObject {
	
	private ArrayList<DocumentClinician> clinicians;
	private String content;
	private PointInTime dateTime;
	private String status;
    private String urgency;
    private PointInTime enteredDateTime;
    private String attending;
	
	@JsonCreator
	public DocumentText(Map<String, Object> vals) {
		super(vals);
	}
	
	public DocumentText() {
		super(null);
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
	
	public ArrayList<DocumentClinician> getClinicians() {
		return clinicians;
	}
	public String getContent() {
		return content;
	}
	public PointInTime getDateTime() {
		return dateTime;
	}
	public String getStatus() {
		return status;
	}
    public String getAuthor() {
    	String result = null;
    	if(clinicians != null) {
    		for(DocumentClinician clinician: clinicians) {
    			 if (clinician.getRole().equals("A")) {
                     return clinician.getName();
                 }
    		}
    	}
        return result;
    }
    
    public String getCosigner() {

    	if(clinicians != null) {
    		for(DocumentClinician clinician: clinicians) {
    			if (clinician.getRole() == "X") {
    				return clinician.getClinician().getName();
    			}
    		}
    	}
    	return null;
    }
}
