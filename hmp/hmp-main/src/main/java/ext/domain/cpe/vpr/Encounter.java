package org.osehra.cpe.vpr;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPatientObject;
import org.osehra.cpe.vpr.pom.IPatientObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * This describes the interactions between the patient and clinicians.
 * Interaction includes both in-person and non-in-person encounters such as
 * telephone and email communication.
 *
 * @see <a
 *      href="http://wiki.hitsp.org/docs/C83/C83-3.html#_Ref232966055">HITSP/C83
 *      Encounter</a>
 */
public class Encounter extends AbstractPatientObject implements IPatientObject {

    private String kind;
    private String summary;

    public Encounter() {
        super(null);
    }

    @JsonCreator
    public Encounter(Map<String, Object> vals) {
        super(vals);
    }

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
     * Free text describing the Encounter Type/emCode)
     *
     * @see "HITSP/C154 16.03 Encounter Free Text Type"
     */
    private String typeName;
    /**
     * This is a coded value describing the type of the Encounter
     * <p/>
     * Should be a CPT-4 code in the range 99200-99299 (E&M Code)
     *
     * @see "HITSP/C154 16.02 Encounter Type"
     */
    private String typeCode;
    /**
     * This is used to categorize patients by the site where the encounter
     * occurred , e.g., Emergency, Inpatient, or Outpatient.
     *
     * @see "HITSP/C154 16.10 Patient Class"
     */
//	private PatientClass patientClass;
    private String patientClassCode;
    private String patientClassName;
    /**
     * The date and time of the Encounter
     *
     * @see "HITSP/C154 16.04 Encounter Date/Time"
     */
    private PointInTime dateTime;
    /**
     * The duration of the Encounter
     *
     * @see "HITSP/C154 16.04 Encounter Date/Time"
     */
    private String duration;
    /**
     * For VistA: distinguishes appointments, past visits, telecom, etc.
     */
//	private EncounterCategory category;
    private String categoryCode;
    private String categoryName;
    /**
     * For VistA: the service field from the SPECIALTY file (42.4)
     */
    private String service;
    /**
     * C80 specifies a subset of SNOMED CT for this
     */
    private String specialty;
    /**
     * VistA Stop Code associated from the location
     */
    private String stopCode;
    /**
     * VistA Stop Code Name
     */
    private String stopCodeName;
    /**
     * VistA Appointment Status
     */
    private String appointmentStatus;
    /**
     * VistA Hospital Location name.
     *
     * @see "HITSP/C154 16.11 In Facility Location"
     */
    private String locationUid;
    /**
     * VistA Hospital Location name.
     *
     * @see "HITSP/C154 16.11 In Facility Location"
     */
    private String locationName;
    /**
     * Name of current room and/or bed (includes EDIS rooms)
     */
    private String roomBed;
    /**
     * Indicates the rationale for the encounter
     *
     * @see "HITSP/C154 16.13 Reason for Visit"
     */
    private String reason;
    /**
     * Coded rationale for the encounter. SNOMED CT for this.
     *
     * @see "HITSP/C154 16.13 Reason for Visit"
     */
    private String reasonCode;
    /**
     * Discharge Disposition (sometimes called Discharge Status) is the
     * persons anticipated location or status following the encounter (e.g.
     * death, transfer to home/hospice/snf/AMA)
     *
     * @see "HITSP/C154 16.09 Discharge Disposition"
     */
//	private DischargeDisposition disposition;
    private String dispositionCode;
    private String dispositionName;
    /**
     * Identifies where the patient was admitted.
     *
     * @see "HITSP/C154 16.06 Admission Source"
     */
//	private AdmissionSource source;
    private String sourceCode;
    private String sourceName;
    /**
     * Names and other information for the persons or organizations that
     * performed or hosted the Encounter
     *
     * @see "HITSP/C154 16.05 Encounter Provider"
     */
    private LinkedHashSet<EncounterProvider> providers;
    /**
     * Indicates this is a consult
     */
    private String referrerUid;
    private String referrerName;
    /**
     * Reference to an encounter that encompasses this one.
     */
    private Encounter parent;

    private PatientStay stay;

    private Set<Map<String, Object>> documentUids;

    //
    @JsonManagedReference("encounter-provider")
    public EncounterProvider getPrimaryProvider() {
        if (providers == null) return null;
//		if(primaryProvider == null && providers != null) {
        for (EncounterProvider provider : providers) {
            if (provider.getPrimary() != null && provider.getPrimary()) {
                return provider;
            }
        }
//		}
        return null;
    }

    public String getSummary() {
//		String s = "";
//		if (service != null) {
//			s += service + ": ";
//		}
//		s += location;
//		return s;
        return summary;
    }

    // we could potentially move this kind of logic to a "KindService(s)" if
    // that is less smelly
    private static final Map<String, String> categoryCodeToKind;

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("AP", "Appointment");
        aMap.put("NS", "No-Show Appointment");
        aMap.put("NH", "Admission");
        aMap.put("AD", "Admission");
        aMap.put("TC", "Visit");
        aMap.put("OV", "Visit");
        aMap.put("CR", "Visit");
        categoryCodeToKind = Collections.unmodifiableMap(aMap);
    }

    public String getKind() {
        String kind = null;
        if (categoryCode != null) {
            kind = categoryCodeToKind.get(categoryCode);
        }
        return (kind != null) ? kind : "Unknown";
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

    public String getTypeName() {
        return typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getPatientClassCode() {
        return patientClassCode;
    }

    public String getPatientClassName() {
        return patientClassName;
    }

    public PointInTime getDateTime() {
        return dateTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getService() {
        return service;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getStopCode() {
        return stopCode;
    }

    public String getStopCodeName() {
        return stopCodeName;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public String getLocationUid() {
        return locationUid;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getRoomBed() {
        return roomBed;
    }

    public String getReason() {
        return reason;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public String getDispositionCode() {
        return dispositionCode;
    }

    public String getDispositionName() {
        return dispositionName;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    @JsonManagedReference("encounter-provider")
    public Set<EncounterProvider> getProviders() {
        return providers;
    }

    public String getReferrerUid() {
        return referrerUid;
    }

    public String getReferrerName() {
        return referrerName;
    }

    public Encounter getParent() {
        return parent;
    }

    public PatientStay getStay() {
        return stay;
    }

    public void addToProviders(EncounterProvider provider) {
        if (provider == null) throw new IllegalArgumentException();

        if (this.providers == null) {
            this.providers = new LinkedHashSet<EncounterProvider>();
        }
        this.providers.add(provider);
        provider.setEncounter(this);
    }

    public void removeFromProviders(EncounterProvider provider) {

    }

    public Set<Map<String, Object>> getDocumentUids() {
        return documentUids;
    }

    public void setDocumentUids(Set<Map<String, Object>> documentUids) {
        this.documentUids = documentUids;
    }

}
