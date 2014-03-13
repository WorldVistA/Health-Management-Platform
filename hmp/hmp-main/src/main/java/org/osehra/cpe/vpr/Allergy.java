package org.osehra.cpe.vpr;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.pom.AbstractPatientObject;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.*;

/**
 * This documents the allergies / adverse reactions for a patient.
 *
 * @see <a
 *      href="http://wiki.hitsp.org/docs/C83/C83-3.html#__Ref207780152">HITSP/C83
 *      Allergy/Drug Sensitivity</a>
 */
public class Allergy extends AbstractPatientObject implements IPatientObject {
    /**
     * The facility where the allergy was observed or recorded
     *
     * @see "HITSP/C154 16.17 Facility ID"
     */
    private String facilityCode;
    /**
     * The facility where the allergy was observed or recorded
     *
     * @see "HITSP/C154 16.18 Facility Name"
     */
    private String facilityName;
    /**
     * For VistA -- localId is the ien from 120.8 by itself
     */
    private String localId;
    /**
     * Text describing the type of allergy (adverse event or allergy)
     */
    private String adverseEventTypeName;
    /**
     * SNOMED CT code describing the type of allergy / adverse event In VistA,
     * this is derived from the Drug/Food/Other set of codes
     * <p/>
     * <p>
     * <i>For VistA</i>, the Allergy Type (120.8,3.1) and Mechanism are used to
     * calculate the value of adverseEventType.
     * </p>
     * <p/>
     * <p>
     * Mechanism + Allergy Type = SNOMED CT code
     * </p>
     * <p/>
     * <pre>
     *   Allergy + D only = 416098002
     *   Allergy + F only = 414285001
     *   Allergy + O only = 419199007
     *   Allergy + multiple or no mechanisms = 419199007
     *
     * Otherwise (Mechanism is Pharmaceutic, Unknown, or empty):
     *
     *   D only = 419511003
     *   F only = 418471000
     *   O only = 418038007
     *   else     420134006
     * </pre>
     *
     * @see "HITSP/C154 6.02 Adverse Event Type"
     */
    private String adverseEventTypeCode;
    /**
     * The time that the allergy was entered or recorded.
     *
     * @see "HITSP/C154 6.01 Adverse Event Date"
     */
    private PointInTime entered;
    /**
     * UID of the person who originally entered the allergy.
     */
    private String enteredByUid;
    /**
     * Name of the person who originally entered the allergy.
     */
    private String enteredByName;
    /**
     * The time that the allergy was verified.
     */
    private PointInTime verified;
    /**
     * UID of the person who verified the allergy.
     */
    private String verifiedByUid;
    /**
     * Name of the person who verified the allergy.
     */
    private String verifiedByName;
    /**
     * Free text describing the severity of the reaction.
     *
     * @see "HITSP/C154 6.07 Severity Free-Text"
     */
    private String severityName;
    /**
     * SNOMED CT code describing the severity of the reaction.
     *
     * @see "HITSP/C154 6.08 Severity Code"
     */
    private String severityCode;
    /**
     * True if the allergy / adverse reaction was historical, otherwise assume
     * observed
     */
    private Boolean historical;

    private String kind;

    private String summary;

    /**
     * For VistA: reference contains the variable pointer to the causitive agent
     */
    private String reference;
    /**
     * List of products that describe the substance causing the allergy /
     * adverse reaction
     *
     * @see "HITSP/C83 Allergy/Drug Sensitivity - Product Detail"
     */
    private List<AllergyProduct> products;
    /**
     * List of reactions to the substance
     *
     * @see "HITSP/C83 Allergy/Drug Sensitivity - Reaction"
     */
    private List<AllergyReaction> reactions;

    private List<AllergyComment> comments;

    public Allergy() {
        super(null);
    }

    @JsonCreator
    public Allergy(Map<String, Object> vals) {
        super(vals);
    }

    public void addToProducts(AllergyProduct product) {
        if (products == null) {
            products = new ArrayList<AllergyProduct>();
        }
        products.add(product);
    }

    public void addToReactions(AllergyReaction reaction) {
        if (reactions == null) {
            reactions = new ArrayList<AllergyReaction>();
        }
        reactions.add(reaction);
    }

    public void addToComments(AllergyComment comment) {
        if (comments == null) {
            comments = new ArrayList<AllergyComment>();
        }
        comments.add(comment);
    }

    public String getLocalId() {
        return localId;
    }

    public String getAdverseEventTypeName() {
        return adverseEventTypeName;
    }

    public String getAdverseEventTypeCode() {
        return adverseEventTypeCode;
    }

    public PointInTime getEntered() {
        return entered;
    }

    public String getEnteredByUid() {
        return enteredByUid;
    }

    public String getEnteredByName() {
        return enteredByName;
    }

    public PointInTime getVerified() {
        return verified;
    }

    public String getVerifiedByUid() {
        return verifiedByUid;
    }

    public String getVerifiedByName() {
        return verifiedByName;
    }

    public String getSeverityName() {
        return severityName;
    }

    public String getSeverityCode() {
        return severityCode;
    }

    public Boolean getHistorical() {
        return historical;
    }

    public String getReference() {
        return reference;
    }

    public List<AllergyProduct> getProducts() {
        return products;
    }

    public List<AllergyReaction> getReactions() {
        return reactions;
    }

    public List<AllergyComment> getComments() {
        return comments;
    }

    public String getKind() {
        return "Allergy / Adverse Reaction";
    }

    public String getSummary() {
        if (StringUtils.hasText(summary)) return summary;

        Set<String> productNames = new HashSet<String>();
        for (AllergyProduct p : products) {
            productNames.add(p.getName());
        }
        return StringUtils.collectionToDelimitedString(productNames, ",");
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }
}
