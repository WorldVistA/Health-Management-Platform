package org.osehra.cpe.auth;

public interface UserContext {
    boolean isLoggedIn();
    HmpUserDetails getCurrentUser();
}
