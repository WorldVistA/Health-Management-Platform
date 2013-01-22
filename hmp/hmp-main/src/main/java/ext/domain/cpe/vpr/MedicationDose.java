package EXT.DOMAIN.cpe.vpr;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;

import java.util.Map;

public class MedicationDose extends AbstractPOMObject {
	private Long id;

    /**
     * The amount of medication to be given.  It is preferred that dose units conform to UCUM.
     * @see "HITSP/C154 8.08 Dose
     * For VistA, this is free text and combines dose and units.
     */    
    private String dose;

    /**
     * Units are stored with the dose, but if they are available explicitly, they are stored here.
     */
    private String units;

    /**
     * The rate at which to administer an IV (i.e., ml/hr -- this is a continuous IV in VistA) or
     * The amount over time to infuse (i.e., infuse over 20 minutes -- for an intermittant IV in VistA).
     */
    private String ivRate;

    private Medication med;
    
    /**
     * The amount of time the dose should be administered, if specified.
     * For IV Orders, used for the "infuse over" time (intermittant IV's).
     * @see "HITSP/C154 8.06 Duration
     */
    private String duration;

    /**
     * Coded value for how the medication is received by the patient.  NCI Concept code for route preferred.
     * @see "HITSP/C154 8.07
     * For VistA this is empty.
     */
    private String routeCode;

    /**
     * Textual name for how the medication is received by the patient.
     * @see "HITSP/C154 8.07
     * In VistA this is the name from MEDICATION ROUTES file (51.2).
     */
    private String routeName;

    /**
     * Expression that describes the schedule in a computable way and can be used to construct an HL7 effectiveTime value.
     * @see "HITSP/C154 8.03, 8.04, 8.05
     *
     * For data from VistA, this is currently empty.
     */
    private String timingExpression;

    /**
     * Free text schedule name.
     * @see "HITSP/C154 8.03, 8.04, 8.05
     * For VistA, this may match a name in the ADMINISTRATION SCHEDULE file (51.1).
     */
    private String scheduleName;

    /**
     * When the dose should start.  For complex doses, this may express when that specific dose begins.
     * This is not calculated for pending orders.  For outpatient orders, it is only approximate.
     * @see "HITSP/C154 8.03
     */
    private PointInTime start;

    /**
     * When the dose should stop.  For complex doses, this may express when that specific dose ends.
     * This is not calculated for pending orders.  For outpatient orders, it is only approximate.
     * @see "HITSP/C154 8.03
     */
    private PointInTime stop;

    /**
     * Specifies total dose, total number of doses, or total volume limit.
     * In VistA, this is used primarily for IV orders
     * @see "HITSP/C154 8.10 Dose Restriction
     */
    private String restriction;

    /**
     * Expresses any precondition that must be met before giving the dose.
     * @see "HITSP/C154 8.25 Dose Indicator
     */
    private String precondition;

    /**
     * Should contain a SNOMED CT code that represents the body site.
    * @see "HITSP/C154 8.09 Site
    */
    private String bodySite;

    /**
     * If this dose is related to a specific order or suborder, that may be identified here.
     * An example would be a VA inpatient complex dose which is split into distinct orders for each dose.
     */
    private String relatedOrder;

    /**
     * The duration of this dose if it is part of a taper or split dose (complex dose).
     * The duration is expressed in HL7 2.4 terms:  M:Minutes, H:Hours, D:Days, W:Weeks, L:Months.
     * For example:  6H is six hours, 10D is ten days, 1L is one month, etc.
     */
    private String complexDuration;

    /**
     * The conjunction indicates whether this dose should run in parallel with the subsequent dose
     * ('and' or 'asynchronous') or in sequence before the subsequent dose ('then' or 'synchronous').
     * This is currently stored in VistA terms:  A:and, T:then, X:except
     * This can be mapped to HL7 2.4 terms where: 'A' is 'A' for Asynchronous (parallel) and
     * 'T' is 'S' for synchronous (sequential) and 'X' is ???.
     */
    private String complexConjunction;

    /**
     * The the start time in minutes for this dose from the initial start time of the order.
     * This is used to manage the start and stop intervals in a relative way.  In particular,
     * this can be used to provide start/stop information while the order is still in a
     * pending state.
     */
    private Integer relativeStart;

    /**
     * Similar to the relativeStart field -- provides a stop time relative to the begin of the
     * order.
     */
    private Integer relativeStop;

    public MedicationDose() {
    	super(null);
    }

    @JsonCreator
	public MedicationDose(Map<String, Object> vals) {
		super(vals);
	}

    public Long getId() {
		return id;
	}

	public String getDose() {
		return dose;
	}

	public String getUnits() {
		return units;
	}

	public String getIvRate() {
		return ivRate;
	}

    @JsonBackReference("medication-dosage")
	public Medication getMed() {
		return med;
	}

	void setMed(Medication med) {
		this.med = med;
	}

	public String getDuration() {
		return duration;
	}

	public String getRouteCode() {
		return routeCode;
	}

	public String getRouteName() {
		return routeName;
	}

	public String getTimingExpression() {
		return timingExpression;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public PointInTime getStart() {
		return start;
	}

	public PointInTime getStop() {
		return stop;
	}

	public String getRestriction() {
		return restriction;
	}

	public String getPrecondition() {
		return precondition;
	}

	public String getBodySite() {
		return bodySite;
	}

	public String getRelatedOrder() {
		return relatedOrder;
	}

	public String getComplexDuration() {
		return complexDuration;
	}

	public String getComplexConjunction() {
		return complexConjunction;
	}

	public Integer getRelativeStart() {
		return relativeStart;
	}

	public Integer getRelativeStop() {
		return relativeStop;
	}

	/**
     * Display relative start date in string format.
     */
    public String getStartDateString() {
        if (start != null && stop != null) {
        	return null;
        }
        if (med.getMedType().equals(CodeConstants.SCT_MED_TYPE_GENERAL)) {
        	return null;  // only outpatient for now
        }
        return (relativeStart != null) ? buildOutputString(relativeStart) : null;
    }

    /**
     * Display relative stop date in string format.
     */
    public String getStopDateString() {
       if (start!= null && stop != null) {
    	   return null;
       }
       if (med.getMedType().equals(CodeConstants.SCT_MED_TYPE_GENERAL)) {
    	   return null;  // only outpatient for now
       }
       return (relativeStop != null) ? buildOutputString(relativeStop) : null;
    }


    //format the output string
    public String  buildOutputString(Integer totalMinutes) {
        Integer days = totalMinutes / 1440;
        Integer hours = (totalMinutes - (days * 1440)) / 60;
        Integer minutes = totalMinutes - (days * 1440) - (hours * 60);

        String result = "";
        if (days > 0) result = result + " " + days.toString() + (days > 1 ? " days" : " day");
        if (hours > 0) result = result + " " + hours.toString() + (hours > 1 ? " hours" : " hour");
        if (minutes > 0) result = result + " " + minutes.toString() + (minutes > 1 ? " minutes" : " minute");

        return result.length()>0?"Start +" + result : "Start";
    }
}

