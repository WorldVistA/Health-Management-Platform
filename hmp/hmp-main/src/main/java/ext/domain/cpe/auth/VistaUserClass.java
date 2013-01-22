package EXT.DOMAIN.cpe.auth;

import com.fasterxml.jackson.annotation.JsonValue;
import EXT.DOMAIN.cpe.datetime.PointInTime;
import org.springframework.security.core.GrantedAuthority;

public class VistaUserClass implements GrantedAuthority {

    public static final String VISTA_USER_CLASS_PREFIX = "VISTA_USER_CLASS_";

    private String userClass;
    private PointInTime effectiveDate;
    private PointInTime expirationDate;

    public VistaUserClass(String userClass) {
        this(userClass, null, null);
    }

    public VistaUserClass(String userClass, PointInTime effectiveDate, PointInTime expirationDate) {
        this.userClass = userClass;
        this.effectiveDate = effectiveDate;
        this.expirationDate = expirationDate;
    }

    @JsonValue
    @Override
    public String getAuthority() {
        return VISTA_USER_CLASS_PREFIX + userClass.replace(' ', '_');
    }

    public String getUserClass() {
        return userClass;
    }

    public PointInTime getEffectiveDate() {
        return effectiveDate;
    }

    public PointInTime getExpirationDate() {
        return expirationDate;
    }
    
    public String toString() {
    	return getAuthority() + String.format(" [effective: %s; expires: %s]", effectiveDate, expirationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VistaUserClass that = (VistaUserClass) o;

        if (!userClass.equals(that.userClass)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return userClass.hashCode();
    }
}
