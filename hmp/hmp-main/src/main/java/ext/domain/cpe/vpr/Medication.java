package EXT.DOMAIN.cpe.vpr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.JDSIndex;
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.MultiValueJDSIndex;
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.RangeJDSIndex;
import EXT.DOMAIN.cpe.vpr.termeng.Concept;
import EXT.DOMAIN.cpe.vpr.termeng.TermEng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Medication extends AbstractPatientObject implements IPatientObject {

    // Source -------------------------

    /**
     * The ID of the facility where the medication was entered
     *
     * @see "HITSP/C154 16.17 Facility ID"
     */
    private String facilityCode;
    /**
     * The name of facility where the medication was entered
     *
     * @see "HITSP/C154 16.18 Facility Name"
     */
    private String facilityName;

    // Identifiers --------------------
    /**
     * For VistA -- localId is the identifier from the appropriate pharmacy
     * files R;O = Prescription file #52 P;O = Pending OP Orders file #52.41 N;O
     * = Non-VA meds subfile #55.04 P;I = Non-Verified Orders file #53.1 U;I =
     * Unit Dose orders subfile #55.06 V;I = IV orders subfile #55.01
     */
    private String localId;

    /**
     * Use to track the revisions history of the medication
     */
    private String predecessor;

    /**
     * Use to track the future revisions of the medication
     */
    private String successor;

    // Product and Instructions --------------
    /**
     * The NCI code for the physical form of the medication as presented to the
     * patient.
     *
     * @see "HITSP/C154 8.11 Product Form"
     *      <p/>
     *      For VistA the mapping to NCI doesn't exist, so this field is empty.
     *      Preferred would be the NCI code.
     */
    private String productFormCode;

    /**
     * Textual name for the product form
     *
     * @see "HITSP/C154 8.11 Product Form
     *      <p/>
     *      For VistA, this is the name field from file 50.606
     */
    private String productFormName;

    /**
     * The instructions for the medication order.
     *
     * @see "HITSP/C154 8.01 Free Text Sig"
     */
    private String sig;
    /**
     * Free Text instruction for the patient
     *
     * @see "HITSP/C154 8.22 Patient Instruction
     */
    private String patientInstruction;

    // Timing -------------------------
    /**
     * The initial order or start date/time of the medication
     */
    @RangeJDSIndex(endField="overallStop", name="med-time")
    private PointInTime overallStart;

    /**
     * The final stop date of the medication (whether dc'd or expired). This may
     * be a future date when the medications and all refills expire or when the
     * med should be stopped.
     *
     * @see "HITSP/C154 8.29 Order Expiration Date/Time"
     */
    private PointInTime overallStop;

    /**
     * The date the medication was stopped. Normally from a discontinue or from
     * a specific duration. Does not include expiration time.
     *
     * @see "HITSP/C154 8.02 Indicate Medication Stopped"
     */
    private PointInTime stopped;

    /**
     * The status of the medication.
     *
     * @see "HITSP/C154 8.20 Status of Medication"
     *      <p/>
     *      If the VistA status is active, suspended, non-verified, or refill:
     *      status is set to "Active" If the VistA status is hold or provider
     *      hold: status is set to "On Hold" All other values set the status to
     *      "No Longer Active"
     */
    private String medStatus;

    /**
     * The status name of the medication.
     *
     * @see "HITSP/C154 8.20 Status of Medication"
     *      <p/>
     *      If the VistA status is active, suspended, non-verified, or refill:
     *      status is set to "Active" If the VistA status is hold or provider
     *      hold: status is set to "On Hold" All other values set the status to
     *      "No Longer Active"
     */
    private String medStatusName;
    /**
     * The type of medication: OTC, Prescription, Clinic Dose, Clinic Infusion,
     * Unit Dose, Infusion.
     *
     * @see "HITSP/C154 8.19 Type of Medication"
     */
    private String medType;

    // VA Specific --------------------
    /**
     * The type of medication from VistA I:INPATIENT; O:OUTPATIENT; N:NON-VA;
     * V:IV FLUID
     */
    private String vaType;
    /**
     * The status of the medication from VistA
     */
    private String vaStatus;
    /**
     * VistA value to denote if an outpatient recieved an inpatient medication
     * while out an outpatient location
     */
    private Boolean IMO; // TODO: consider using Boolean here

    // Multi-valued -------------------
    /**
     * Medication product information -- the generic medication without specific
     * dose or strength information. For IV's, there may be multiple products.
     * For inpatient and outpatient, there is just one product.
     * <p/>
     * Also includes the specific product supplied -- this includes the
     * strength. For VistA, this references the dispensed drug.
     */
    @MultiValueJDSIndex(name="med-product", subfield="drugClassName")
    private List<MedicationProduct> products;

    /**
     * The dose(s) for a medication Using the List type preserves the sequence
     * of the doses (for complex orders).
     */
    private List<MedicationDose> dosages;

    /**
     * Orders that are fulfilled by this medication. In VistA normally a one to
     * one mapping. Set to a one to many mapping to handle orders coming from
     * multiple locations
     */
    private List<MedicationOrder> orders;

    /**
     * The indication for prescribing the medication Currently not used by VistA
     */
    private List<MedicationIndication> indications;

    /**
     * The date and other information for each fill of the medication
     */
    private List<MedicationFill> fills;

    /**
     * The qualified name contains just the active ingredient (generic name)
     * without strength information This allows searching for medications and
     * grouping them together without regard to dosage.
     */
    private String qualifiedName;
    
    public Set<String> rxnCodes;

    public Medication(){
		super(null);
	}

	@JsonCreator
	public Medication(Map<String, Object> vals) {
		super(vals);
	}

    public void addToProducts(MedicationProduct product) {
        if (products == null) {
            products = new ArrayList<MedicationProduct>();
        }
        products.add(product);
        product.setMed(this);
    }

    public void removeFromProducts(MedicationProduct product) {
        if (this.products == null) return;

        this.products.remove(product);
    }
    
    public void addToDosages(MedicationDose dosage) {
        if (dosages == null) {
            dosages = new ArrayList<MedicationDose>();
        }
        dosages.add(dosage);
        dosage.setMed(this);
    }

    public void removeFromDosages(MedicationDose dosage) {
        if (this.dosages == null) return;

        this.dosages.remove(dosage);
    }
    
    public void addToOrders(MedicationOrder order) {
        if (orders == null) {
            orders = new ArrayList<MedicationOrder>();
        }
        orders.add(order);
        order.setMed(this);
    }

    public void removeFromOrders(MedicationOrder order) {
        if (this.orders == null) return;

        this.orders.remove(order);
    }
    
    public void addToFills(MedicationFill fill) {
        if (fills == null) {
            fills = new ArrayList<MedicationFill>();
        }
        fills.add(fill);
        fill.setMed(this);
    }

    public void removeFromFills(MedicationFill fill) {
        if (this.fills == null) return;

        this.fills.remove(fill);
    }
    
    public void addToIndications(MedicationIndication indication) {
        if (indications == null) {
            indications = new ArrayList<MedicationIndication>();
        }
        indications.add(indication);
        indication.setMed(this);
    }

    public void removeFromIndications(MedicationIndication indication) {
        if (this.indications == null) return;

        this.indications.remove(indication);
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

    public String getPredecessor() {
        return predecessor;
    }

    public String getSuccessor() {
        return successor;
    }

    public String getProductFormCode() {
        return productFormCode;
    }

    public String getProductFormName() {
        return productFormName;
    }

    public String getSig() {
        return sig;
    }

    public String getPatientInstruction() {
        return patientInstruction;
    }

    public PointInTime getOverallStart() {
        return overallStart;
    }

    public PointInTime getOverallStop() {
        return overallStop;
    }

    public PointInTime getStopped() {
        return stopped;
    }

    public String getMedStatus() {
        return medStatus;
    }

    public String getMedStatusName() {
        return medStatusName;
    }

    public String getMedType() {
        return medType;
    }

    public String getVaType() {
        return vaType;
    }
    
    public String getVaStatus() {
        return vaStatus;
    }

    @JsonProperty("imo")
    public Boolean getIMO() {
        return IMO;
    }

    @JsonManagedReference("medication-product")
    public List<MedicationProduct> getProducts() {
    	if (products == null) products = new ArrayList<MedicationProduct>();
        return products;
    }

    @JsonManagedReference("medication-dosage")
    public List<MedicationDose> getDosages() {
        return dosages;
    }

    @JsonManagedReference("medication-order")
    public List<MedicationOrder> getOrders() {
        return orders;
    }

    @JsonManagedReference("medication-indication")
    public List<MedicationIndication> getIndications() {
        return indications;
    }

    public List<MedicationFill> getFills() {
        return fills;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public static Map<String, String> getVatypetokind() {
        return vaTypeToKind;
    }
    
    @JsonIgnore
    public Set<String> getDrugClassCodes() {
    	Set<String> ret = new HashSet<String>();
    	for (MedicationProduct mp : getProducts()) {
    		String code = mp.getDrugClassCode();
    		if (code != null) {
    			ret.add(code);
    		}
    	}
    	return ret;
    }
    
    /**
     * TODO: We should normalize the URN's so I don't have to convert urn:vuid:xxx to urn:vandf:xxx
     */
	public Set<String> getRXNCodes() {
    	// if they were already computed/stored, return it
    	if (this.rxnCodes != null) {
    		return this.rxnCodes;
    	}
    	
    	// otherwise compute the values
    	TermEng eng = TermEng.getInstance();
    	Set<String> ret = new LinkedHashSet<String>();
    	for (MedicationProduct mp : getProducts()) {
    		String vuid = mp.getIngredientCode();
    		if (vuid == null) {
    			continue;
    		}
    		String[] parts = vuid.split(":");
    		vuid = (parts.length >= 3) ? "urn:vandf:" + parts[2] : null;
    		if (vuid == null) {
    			continue;
    		}
    		Concept c = eng.getConcept(vuid);
    		c = c.getMappingTo("ndfrt");
    		if (c != null) {
	    		for (String same : c.getEquivalentSet()) {
	    			if (same.startsWith("urn:ndfrt:")) {
	    				ret.add(same);
	    				ret.addAll(eng.getAncestorSet(same));
	    			}
	    		}
    		}
    	}
    	this.rxnCodes = ret;
    	return ret;
    }

    @Override
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(displayProducts());
        if (vaStatus != null) {
            summary.append(" (");
            summary.append(vaStatus);
            summary.append(")\n");
        } else {
            // summary.append(VprConstants.SCT_MED_STATUS_TO_TEXT.[medStatus]);
            if (medStatus != null) {
                summary.append("(");
                summary.append(CodeConstants.SCT_MED_STATUS_TO_TEXT.get(medStatus));
                summary.append(")\n");
            }
        }
        if (sig != null) {
        	if(summary.length()>0) {
                summary.append(" ");
        	}
            summary.append(sig);
        }
        if (CodeConstants.VA_MED_TYPE_INFUSION.equals(vaType) && dosages != null && !dosages.isEmpty()) {
            MedicationDose onlyDose = dosages.get(0);
            if (onlyDose.getIvRate() != null) {
                summary.append(onlyDose.getIvRate() + " ");
            }
            if (onlyDose.getDuration() != null) {
                summary.append(onlyDose.getDuration() + " ");
            }
            if (onlyDose.getScheduleName() != null) {
                summary.append(onlyDose.getScheduleName() + " ");
            }
            if (onlyDose.getRestriction() != null) {
                summary.append("for a total of " + onlyDose.getRestriction());
            }
        }
        return summary.toString();
    }

    // Convenience --------------------
    
    @JsonIgnore
    public boolean isActive() {
    	String s = getVaStatus();
    	return (s != null && s.equals("ACTIVE"));
    }
    
    @JsonIgnore
    public boolean isPending() {
    	String s = getVaStatus();
    	return (s != null && s.equals("PENDING"));
    }


    public MedicationProduct onlyProduct() {
        // return products?.first()
        if (products != null && products.size() > 0) {
            return products.get(0);
        }
        return null;
    }

    //TODO - delete when checked.
    // we could potentially move this kind of logic to a "KindService(s)" if
    // that is less smelly
    // private static final Map vaTypeToKind = [
    // "I": "Medication, Inpatient",
    // "O": "Medication, Outpatient",
    // "N": "Medication, Non-VA",
    // "V": "Infusion",
    // ];

    private static final Map<String, String> vaTypeToKind;

    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("I", "Medication, Inpatient");
        aMap.put("O", "Medication, Outpatient");
        aMap.put("N", "Medication, Non-VA");
        aMap.put("V", "Infusion");
        vaTypeToKind = Collections.unmodifiableMap(aMap);
    }

    public String getKind() {
        // we could potentially move this kind of logic to a "KindService(s)" if
        // that is less smelly
        String kind = vaTypeToKind.get(vaType);
        if (kind == null) {
            return "Unknown";
        }
        return kind;
    }

    public List getTaggers() {
        // if (uid)
        // return manualFlush { Tagger.findAllByUrl(uid) }
        // else
        // return []
        return null;
        // TODO - fix this.
    }

    /**
     * Build a string of the supplied products or ingredients for display
     *
     * @return text of product(s)
     */
    public String displayProducts() {
        // ToDo: find a less VA-specific way to do this (need a less
        // idiosyncratic way to represent tapers, etc.)
        // TODO - fix this.
        StringBuilder x = new StringBuilder();
        if (medType != null && !medType.equals(CodeConstants.SCT_MED_TYPE_GENERAL)) {
            // products.collectAll { x += (x.length() ? ', ' : '') + it.suppliedName
            if (products != null) {
                for (MedicationProduct product : products) {
                    if (x.length() > 0) {
                        x.append(", ");
                    }
                    x.append(product.getSuppliedName());
                }
            }
        } else {
            // inpatient medications
            if (vaType != CodeConstants.VA_MED_TYPE_INFUSION) {
                // inpatient meds excluding infusions
                Set<String> names = new HashSet<String>();

                if (products != null) {
                    for (MedicationProduct product : products) {
                        names.add(product.getIngredientName());
                    }

                    for (String name : names) {
                        if (x.length() > 0) {
                            x.append(",");
                        }
                        x.append(name);
                    }
                }
                if (x.length() > 0) {
                    x.append(" ");
                }
                x.append(productFormName);
//	                products.each { names.add(it.ingredientName) }
//	                names.unique().each { x += (x.length() ? ', ' : '') + it}
//	                x += ' ' + productFormName
            } else {
                // infusions
                String a = "";
                String b = "";
                if (products != null) {
                    for (MedicationProduct product : products) {
                        if (product.getIngredientRole().equals(CodeConstants.SCT_MED_ROLE_BASE)) {
                            b += (b.length() > 0 ? ", " : "") + product.getSuppliedName();
                        } else {
                            a += (a.length() > 0 ? ", " : "") + product.getSuppliedName();
                        }
                    }
                    x.append(a);
                    x.append(" in ");
                    x.append(b);
                }
            }
        }
        //TODO - delete when checked
        // outpatient medications
        //products.collectAll { x += (x.length() ? ', ' : '') + it.suppliedName
        // }
        // } else {
        // // inpatient medications
        // if (vaType != VprConstants.VA_MED_TYPE_INFUSION) {
        // // inpatient meds excluding infusions
        // List names = []
        // products.each { names.add(it.ingredientName) }
        // names.unique().each { x += (x.length() ? ', ' : '') + it}
        // x += ' ' + productFormName
        // } else {
        // // infusions
        // String a = ''
        // String b = ''
        // products.each {
        // if (it.ingredientRole == VprConstants.SCT_MED_ROLE_BASE) {
        // b += (b.length() ? ', ' : '') + it.suppliedName
        // } else {
        // a += (a.length() ? ', ' : '') + it.suppliedName
        // }
        // }
        // x += a + ' in ' + b
        // }
        // }
        // return x
        if (x.length() == 0) x.append(getQualifiedName());
        return x.toString();
    }
}
