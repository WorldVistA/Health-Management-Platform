package org.osehra.cpe.vista.rpc.conn;

import org.springframework.util.StringUtils;

/**
 * TODOC: Provide summary documentation of class AccessVerifyConnectionSpec
 *
 * @see ConnectionFactory
 */
public class AccessVerifyConnectionSpec implements ConnectionSpec {

    public static final String DIVISION_CREDENTIALS_DELIMITER = ":";
    public static final String ACCESS_VERIFY_CODE_DELIMITER = ";";

    private final String division;
    private final String accessCode;
    private final String verifyCode;

    public AccessVerifyConnectionSpec(String division, final String accessCode, final String verifyCode) {
        this.division = division;
        this.accessCode = accessCode;
        this.verifyCode = verifyCode;
    }

    public String getDivision() {
        return division;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public String getCredentials() {
        return getAccessCode() + ACCESS_VERIFY_CODE_DELIMITER + getVerifyCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessVerifyConnectionSpec that = (AccessVerifyConnectionSpec) o;

        if (accessCode != null ? !accessCode.equals(that.accessCode) : that.accessCode != null) return false;
        if (division != null ? !division.equals(that.division) : that.division != null) return false;
        if (verifyCode != null ? !verifyCode.equals(that.verifyCode) : that.verifyCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = division != null ? division.hashCode() : 0;
        result = 31 * result + (accessCode != null ? accessCode.hashCode() : 0);
        result = 31 * result + (verifyCode != null ? verifyCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (StringUtils.hasText(getDivision()))
            return getDivision() + DIVISION_CREDENTIALS_DELIMITER + getAccessCode() + ACCESS_VERIFY_CODE_DELIMITER + getVerifyCode();
        else
            return getAccessCode() + ACCESS_VERIFY_CODE_DELIMITER + getVerifyCode();
    }

    /**
     * Creates an AccessVerifyConnectionSpec from a string with the format
     * <p/>
     * {division}:{accessCode};{verifyCode}
     *
     * @param credentials
     * @return
     * @throws IllegalArgumentException
     */
    public static AccessVerifyConnectionSpec create(String credentials) {
        String username = null;
        String password = null;
        int delim = credentials.indexOf(DIVISION_CREDENTIALS_DELIMITER);
        if (delim != -1) {
            username = credentials.substring(0, delim);
            password = credentials.substring(delim + 1);
        } else {
            password = credentials;
        }

        String accessCode = "";
        String verifyCode = "";
        String newVerifyCode = null;
        String confirmNewVerifyCode = null;
        String[] pieces = password.split(";");
        if (pieces.length != 2 && pieces.length != 4)
            throw new IllegalArgumentException("expected 1 or 3 '" + ACCESS_VERIFY_CODE_DELIMITER + "' characters in credentials in order to create access/verify connection spec");
        accessCode = pieces[0];
        verifyCode = pieces[1];
        if (pieces.length == 4) {
            newVerifyCode = pieces[2];
            confirmNewVerifyCode = pieces[3];

            return new ChangeVerifyCodeConnectionSpec(username, accessCode, verifyCode, newVerifyCode, confirmNewVerifyCode);
        }

        return new AccessVerifyConnectionSpec(username, accessCode, verifyCode);
    }

}
