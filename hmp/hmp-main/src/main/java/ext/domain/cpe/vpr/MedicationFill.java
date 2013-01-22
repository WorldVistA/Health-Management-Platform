package EXT.DOMAIN.cpe.vpr;

import com.fasterxml.jackson.annotation.JsonCreator;
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;

import java.util.Map;

public class MedicationFill extends AbstractPOMObject {
	private Long id;

	/**
	 * The status of the fill. It is generally "completed". It would be
	 * "aborted" if the fill was never given to the patient (returned to stock).
	 * 
	 * @see "HITSP/C154 8.40 Fill Status
	 */
	private String fillStatus;

	private Medication med;

	/**
	 * The date/time the fill was given or mailed to the patient.
	 * 
	 * @see "HITSP/C154 8.37 Dispense Date
	 */
	private PointInTime dispenseDate;

	/**
	 * Name of the pharmacy that dispensed the medication. For VistA, this
	 * simply says "VA" for the time being. It could include the facility or
	 * CMOP information.
	 * 
	 * @see "HITSP/C154 8.35 Dispensing Pharmacy
	 */
	private String dispensingPharmacy;

	/**
	 * The amount dispensed.
	 * 
	 * @see "HITSP/C154 8.38 Quantity Dispense
	 */
	private String quantityDispensed;

	/**
	 * The days supply dispensed.
	 */
	private Integer daysSupplyDispensed;

	/**
	 * In VistA Window, Mail, Clinic
	 */
	private String routing;

	@JsonCreator
	public MedicationFill(Map<String, Object> vals) {
		super(vals);
	}

    public Long getId() {
		return id;
	}

	public String getFillStatus() {
		return fillStatus;
	}

	public Medication getMed() {
		return med;
	}

	void setMed(Medication med) {
		this.med = med;
	}

	public PointInTime getDispenseDate() {
		return dispenseDate;
	}

	public String getDispensingPharmacy() {
		return dispensingPharmacy;
	}

	public String getQuantityDispensed() {
		return quantityDispensed;
	}

	public Integer getDaysSupplyDispensed() {
		return daysSupplyDispensed;
	}

	public String getRouting() {
		return routing;
	}
}
