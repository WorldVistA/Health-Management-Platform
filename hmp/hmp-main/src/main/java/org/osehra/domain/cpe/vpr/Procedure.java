package org.osehra.cpe.vpr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPatientObject;
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;
import org.osehra.cpe.vpr.pom.IPatientObject;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Procedure extends AbstractPatientObject implements IPatientObject {
    private String kind;
//    private String summary;
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
    private String typeName;
    private String typeCode;
    private PointInTime dateTime;
    private String category;
    private Integer bodySite;
    private String status;
    private String reason;
    //private Encounter encounter;
    private String encounterUid;
	// fields added for Consults
    private String consultProcedure;
    private String service;
    private String orderUid;

    private LinkedHashSet<ProcedureProvider> providers;

    private LinkedHashSet<ProcedureResult> results;
//    private Set<UidLink> results;
	private LinkedHashSet<ProcedureLink> links;

    private LinkedHashSet<Modifier> modifiers;
    
    /**
     * Added for radiology support
     */
    private String imagingTypeUid;
    private String locationUid;
    private Boolean hasImages;
    private String imageLocation;
    private Boolean verified;

    @JsonCreator
    public Procedure(Map<String, Object> data) {
		super(data);
	}
    
    public Procedure()
    {
    	super(null);
    }

    public String getLocalId() {
        return localId;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public PointInTime getDateTime() {
        return dateTime;
    }

    public String getCategory() {
        return category;
    }

    public Integer getBodySite() {
        return bodySite;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getConsultProcedure() {
        return consultProcedure;
    }

    public String getService() {
        return service;
    }

    public String getOrderUid() {
        return orderUid;
    }

    public Set<ProcedureProvider> getProviders() {
        return providers;
    }

    public Set<ProcedureResult> getResults() {
        return results;
    }

    public Set<ProcedureLink> getLinks() {
        return links;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getKind() {
        // we could potentially move this kind of logic to a "KindService(s)" if
        // that is less smelly
    	if(kind!=null)
    	{
    		return kind;
    	}
        if (category.equals("C")) {
            return "Consult";
        } else if (category.equals("RA")) {
            return "Imaging";
        }

        return "Procedure";
    }

    public String getSummary() {
        return typeName!=null?typeName:"";
    }

    public List getTaggers() {
        // if (uid)
        // return manualFlush { Tagger.findAllByUrl(uid) }
        // else
        // return []
        return null;
        // TODO - fix this.
    }

    public String getEncounterUid() {
		return encounterUid;
	}

	public String getImagingTypeUid() {
		return imagingTypeUid;
	}

	public String getLocationUid() {
		return locationUid;
	}

	public Boolean getHasImages() {
		return hasImages;
	}

	public String getImageLocation() {
		return imageLocation;
	}

	public Boolean getVerified() {
		return verified;
	}

    public void addToProviders(ProcedureProvider provider) {
        if (providers == null) {
            providers = new LinkedHashSet<ProcedureProvider>();
        }
        providers.add(provider);
    }

    public void removeFromProviders(ProcedureProvider provider) {
        if (providers == null) return;
        providers.remove(provider);
    }

    public void addToResults(ProcedureResult result) {
        if (results == null) {
            results = new LinkedHashSet<ProcedureResult>();
        }
        results.add(result);
        result.setProcedure(this);
    }

    public void removeFromResults(ProcedureResult result) {
        if (results == null) return;
        results.remove(result);
    }

    public void addToLinks(ProcedureLink link) {
        if (links == null) {
        	links = new LinkedHashSet<ProcedureLink>();
        }
        links.add(link);
    }

    public void addToModifiers(Modifier modifier) {
        if (modifiers == null) {
            modifiers = new LinkedHashSet<Modifier>();
        }
        modifiers.add(modifier);
    }

    public void removeFromModifiers(Modifier modifier) {
        if (modifiers == null) return;
        modifiers.remove(modifier);
    }

    @JsonIgnore
    public void loadLinkData(IGenericPatientObjectDAO dao) {
    	if(results!=null)
    	{	
    		for(ProcedureResult res: results)
    		{
    			res.loadDocumentBody(dao);
    		}
    	}
    }
}
