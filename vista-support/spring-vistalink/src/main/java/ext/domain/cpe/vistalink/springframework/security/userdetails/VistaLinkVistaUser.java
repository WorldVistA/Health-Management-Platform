package org.osehra.cpe.vistalink.springframework.security.userdetails;

import org.osehra.cpe.vista.springframework.security.userdetails.VistaUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class VistaLinkVistaUser extends VistaUser implements VistaLinkVistaUserDetails {

    private String signonLogInternalEntryNumber;
    private String personName;
    private String givenName;
    private String middleName;
    private String familyName;
    private String prefix;
    private String suffix;
    private String degree;

//    private SortedMap<String, VistaDivision> permittedDivisions;

    public VistaLinkVistaUser(String signonLogInternalEntryNumber, String stationNumber, String duz, String access, String verify, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, List<GrantedAuthority> authorities) {
        this(signonLogInternalEntryNumber, stationNumber, duz, access, verify, null, null, null, null, null, null, null, null, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }


    public VistaLinkVistaUser(String signonLogInternalEntryNumber, String loginStationNumber, String duz, String accessCode, String verifyCode, String personName, String displayName, String givenName, String middleName, String familyName, String prefix, String suffix, String degree, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities) {
        super(null, null, loginStationNumber, duz, accessCode, verifyCode, displayName, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        if (duz == null) throw new IllegalArgumentException();
        if (loginStationNumber == null) throw new IllegalArgumentException();
        if (signonLogInternalEntryNumber == null) throw new IllegalArgumentException();
        if (authorities == null) throw new IllegalArgumentException();
        this.personName = personName;
        this.givenName = givenName;
        this.middleName = middleName;
        this.familyName = familyName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.degree = degree;
        this.signonLogInternalEntryNumber = signonLogInternalEntryNumber;
    }


    public String getGivenName() {
        return givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getDegree() {
        return degree;
    }

    public String getSignonLogInternalEntryNumber() {
        return signonLogInternalEntryNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
}
