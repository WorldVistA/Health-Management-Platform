package org.osehra.cpe.vistalink.springframework.security.userdetails;

import org.osehra.cpe.vista.springframework.security.userdetails.VistaUserDetails;

/**
 * TODOC: Provide summary documentation of class VistaUserCache
 */
public interface VistaUserCache {
    VistaUserDetails getUserFromCache(String accessCode, String verifyCode);

    void putUserInCache(VistaUserDetails user);

    void removeUserFromCache(String accessCode, String verifyCode);
}
