package EXT.DOMAIN.cpe.vistalink.springframework.security.userdetails;

import EXT.DOMAIN.cpe.vista.springframework.security.userdetails.VistaUserDetails;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.util.Assert;

/**
 * TODOC: Provide summary documentation of class EhCacheBasedVistaUserCache
 */
public class EhCacheBasedVistaUserCache implements VistaUserCache {

    private static final Logger logger = LoggerFactory.getLogger(EhCacheBasedVistaUserCache.class);

    //~ Instance fields ================================================================================================

    private Ehcache cache;

    //~ Methods ========================================================================================================

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cache, "cache mandatory");
    }

    public Ehcache getCache() {
        return cache;
    }

    public VistaUserDetails getUserFromCache(String access, String verify) {
        Element element = null;

        try {
            element = cache.get(getKey(access, verify));
        } catch (CacheException cacheException) {
            throw new DataRetrievalFailureException("Cache failure: " + cacheException.getMessage());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Cache hit: " + (element != null) + "; av: " + getKey(access, verify));
        }

        if (element == null) {
            return null;
        } else {
            return (VistaUserDetails) element.getValue();
        }
    }

    private String getKey(String access, String verify) {
        return access + ";" + verify; // TODO: encrypt this
    }

    public void putUserInCache(VistaUserDetails user) {
        Element element = new Element(user.getUsername(), user);

        if (logger.isDebugEnabled()) {
            logger.debug("Cache put: " + element.getKey());
        }

        cache.put(element);
    }

    public void removeUserFromCache(VistaUserDetails user) {
        if (logger.isDebugEnabled()) {
            logger.debug("Cache remove: " + user.getUsername());
        }

        this.removeUserFromCache(user.getAccessCode(), user.getVerifyCode());
    }

    public void removeUserFromCache(String access, String verify) {
        cache.remove(getKey(access, verify));
    }

    public void setCache(Ehcache cache) {
        this.cache = cache;
    }
}
