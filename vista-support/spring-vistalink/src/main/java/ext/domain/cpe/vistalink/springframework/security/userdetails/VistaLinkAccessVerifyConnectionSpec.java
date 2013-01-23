package org.osehra.cpe.vistalink.springframework.security.userdetails;

import org.osehra.crypto.VistaKernelHash;
import org.osehra.crypto.VistaKernelHashCountLimitExceededException;
import org.osehra.vistalink.adapter.cci.VistaLinkConnectionSpecImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

class VistaLinkAccessVerifyConnectionSpec extends VistaLinkConnectionSpecImpl {

    private static final String TYPE_AV = "av";
    private static final String ELEMENT_AV = "AccessVerify";
    private static final String ATTRIBUTE_AVCODE = "avCode";
    private String accessCode;
    private String verifyCode;
    private String clientIp;
    private String avCode;

    private static Logger logger =
            LoggerFactory.getLogger(VistaLinkAccessVerifyConnectionSpec.class);

    /**
     * Main constructor for this connection spec.
     *
     * @param division   station # (external format) of the division to log the user in against
     * @param accessCode user access code
     * @param verifyCode user verify code
     */
    public VistaLinkAccessVerifyConnectionSpec(
            String division,
            String accessCode,
            String verifyCode,
            String clientIp) {

        super(division);
        this.accessCode = accessCode;
        this.verifyCode = verifyCode;
        this.clientIp = clientIp;
        this.avCode = "";
        try {
            this.avCode =
                    VistaKernelHash.encrypt(
                            accessCode + ";" + verifyCode + ";" + clientIp,
                            true);
        } catch (VistaKernelHashCountLimitExceededException e) {
            logger.error("Could not encrypt access/verify code", e);
        }
    }

    public ArrayList getProprietarySecurityInfo() {
        ArrayList values = new ArrayList();
        values.add(this.avCode);
        return values;
    }

    public void setAuthenticationNodes(
            Document requestDoc,
            Node securityNode) {

        if (logger.isDebugEnabled()) {
            logger.debug("setAuthenticationNodes -> Re Auth type is 'av'");
        }

        //AC/OAK OIFO - Next line commented out and replaced by following line as required for upgrading to VL 1.5 dev17:
        //		setSecurityDivision(securityNode, this.getDivision());
        setSecurityDivisionAttr(securityNode);
        //AC/OAK OIFO - Next line commented out and replaced by following line as required for upgrading to VL 1.5 dev17:
        //		setSecurityType(securityNode, TYPE_AV);
        setSecurityTypeAttr(securityNode);

        Element elemAV = requestDoc.createElement(ELEMENT_AV);

        /* add CDATA section for encoded AV code */
        CDATASection cdata = requestDoc.createCDATASection(this.avCode);
        Node currentAvCdataNode = elemAV.getFirstChild();
        if (currentAvCdataNode != null) {
            elemAV.removeChild(currentAvCdataNode);
        }
        elemAV.appendChild(cdata);

        securityNode.appendChild(elemAV);

    }

    /**
     * checks equality with any object
     */
    public boolean isConnSpecEqual(Object obj) {
        return equals(obj);
    }

    /**
     * @return whether the object is equal
     */
    public boolean equals(Object obj) {
        if (obj instanceof VistaLinkAccessVerifyConnectionSpec) {
            VistaLinkAccessVerifyConnectionSpec connSpec =
                    (VistaLinkAccessVerifyConnectionSpec) obj;
            if ((connSpec.getDivision().equals(this.getDivision()))
                    && (connSpec.getAccessCode().equals(this.getAccessCode()))
                    && (connSpec.getVerifyCode().equals(this.getVerifyCode()))
                    && (connSpec.getClientIp().equals(this.getClientIp()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the hashCode
     */
    public int hashCode() {
        // algorithm taken from "Effective Java" item #8.
        int HASHCODE_SEED = 17;
        int returnVal = HASHCODE_SEED;

        // division contribution to hashcode
        int divisionHashCode = this.getDivision().hashCode();
        returnVal = 37 * returnVal + divisionHashCode;
        // Access code contribution to hashcode
        int accessHashCode = this.getAccessCode().hashCode();
        returnVal = 37 * returnVal + accessHashCode;
        // Verify code contribution to hashcode
        int verifyHashCode = this.getVerifyCode().hashCode();
        returnVal = 37 * returnVal + verifyHashCode;
        // Client IP contribution to hashcode
        int clientIPHashCode = this.getClientIp().hashCode();
        returnVal = 37 * returnVal + clientIPHashCode;
        return returnVal;
    }

    /**
     * @return the client ip address
     */
    public String getClientIp() {
        return this.clientIp;
    }

    /**
     * @return the internal access code
     */
    public String getAccessCode() {
        return accessCode;
    }

    /**
     * @return the internal verify code
     */
    public String getVerifyCode() {
        return verifyCode;
    }

    /**
     * returns the security type.
     */
    public String getSecurityType() {
        return TYPE_AV;
    }
}
