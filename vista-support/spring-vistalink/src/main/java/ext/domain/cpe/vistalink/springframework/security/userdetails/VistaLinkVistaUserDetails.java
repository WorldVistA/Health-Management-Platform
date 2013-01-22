package EXT.DOMAIN.cpe.vistalink.springframework.security.userdetails;

import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetails;

/**
 * TODO: Provide summary documentation of class VistaLinkVistaUserDetails
 */
public interface VistaLinkVistaUserDetails extends VistaUserDetails {
    String getGivenName();

    String getMiddleName();

    String getFamilyName();

    String getPrefix();

    String getSuffix();

    String getDegree();

    String getSignonLogInternalEntryNumber();

    String getPersonName();
}
