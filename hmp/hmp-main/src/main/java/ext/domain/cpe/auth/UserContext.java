package EXT.DOMAIN.cpe.auth;

public interface UserContext {
    boolean isLoggedIn();
    HmpUserDetails getCurrentUser();
}
