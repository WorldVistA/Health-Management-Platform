package org.osehra.cpe.vpr.vistasvc;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.WeakHashMap;

import javax.management.MBeanServer;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.management.ManagementService;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Simple cache utility for storing data in session, request, application scopes or on disk.
 * 
 * For session, request, its stored in the current session (throws an error if there is none)
 * 
 * For application and disk, uses EHCache for for storage.
 * 
 * 2 usage methods:
 * 
 * 1) Use the static methods CacheMgr.store() and CacheMgr.fetch() to fetch/store any cached value
 * - Always returns Objects
 * - Forces you to specify the cache namespace/type for each call
 * 2) create a new instance of CacheMgr (CacheMgr mycache = new CacheMgr<Patient>("PatientCache", CacheType.SESSION))
 * - Generified, so you don't have to do any casting
 * - targeted at one cache namespace/type, much more simple store()/fetch() methods.
 * 
 * 
 */
public class CacheMgr<T> implements ICacheMgr<T> {
	public enum CacheType {
		/**
		 * Stores items as session variables, they are user specific and are lost after the session expires
		 */
		SESSION,

		/**
		 * Stores items as request variables, they are user specific and are lost at the end of the request
		 */
		REQUEST,  

		/**
		 * Similar to application scope variables, stored in memory, shared by all users and are lost when
		 * the server/app is restarted.
		 */
		MEMORY,

		/**
		 * Disk-persisted scope, shared by all users and is durable to disk so it can survive restarts.
		 */
		DISK,
		
		/**
		 * Custom EHCache policy defined in ehcache.xml, throws an error if cacheName is not defined in ehcache.xml
		 */
		CUSTOM,
		
		/**
		 * Same as session, but backed by EHCache instead of session scoped variables.  
		 * Will likely replace SESSION soon.
		 */
		SESSION_MEMORY {@Override
		public String getCacheName(String name) {
			ServletRequestAttributes attr = getRequestAttrs();
			return attr.getSessionId() + ":" + name;
		}},
		
		/**
		 * Items are not cached at all, fetch() always returns null
		 */
		NONE;
		
		public String getCacheName(String name) {
			return name;
		}
	}
	
	private static CacheManager manager = CacheManager.create();
	 
	static {
		// register MBean
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		ManagementService.registerMBeans(manager, mBeanServer, true, true, true, true);
	}
	
	private String cacheName;
	private CacheType type;
	
	/**
	 * Creates a new cache manager for working with the specified cache namespace.
	 * 
	 * If CacheType is APPLICATION or DISK, then it will create a new EHCache cache
	 * named cacheName if it does not already exist.  You can fine-tune the cache
	 * properties by creating an entry with the same name in ehcache.xml
	 */
	public CacheMgr(String cacheName, CacheType type) {
		this.cacheName = cacheName;
		this.type = type;
	}
	
	/**
	 * Creates a new cache manager for working with the specified cache namespace.
	 * 
	 * CacheName must exist in ehcache.xml
	 * 
	 * @param cacheName
	 */
	public CacheMgr(String cacheName) {
		this.cacheName = cacheName;
		this.type = CacheType.CUSTOM;
	}
	
	/* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.vistasvc.ICacheMgr#storeUnlessNull(java.lang.String, T)
	 */
	@Override
	public T storeUnlessNull(String key, T val) {
		if (key != null && val != null) {
			return store(key, val);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.vistasvc.ICacheMgr#store(java.lang.String, T)
	 */
	@Override
	public T store(String key, T val) {
		return (T) store(this.type.getCacheName(this.cacheName), key, val, this.type, -1);
	}

	/* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.vistasvc.ICacheMgr#store(java.lang.String, T, int)
	 */
	@Override
	public T store(String key, T val, int ttlSec) {
		return (T) store(this.type.getCacheName(this.cacheName), key, val, this.type, ttlSec);
	}
	
	/* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.vistasvc.ICacheMgr#fetch(java.lang.String)
	 */
	@Override
	public T fetch(String key) {
		return (T) fetch(this.type.getCacheName(this.cacheName), key, this.type);
	}
	
	/* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.vistasvc.ICacheMgr#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		return contains(this.type.getCacheName(this.cacheName), key, this.type);
	}
	
	/* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.vistasvc.ICacheMgr#getSize()
	 */
	@Override
	public int getSize() {
		return getSize(this.type.getCacheName(this.cacheName), this.type);
	}
	
	/* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.vistasvc.ICacheMgr#remove(java.lang.String)
	 */
	@Override
	public void remove(String... keys) {
		for (String key : keys) {
			if (key != null) {
				remove(this.type.getCacheName(this.cacheName), key, this.type);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.osehra.cpe.vpr.vistasvc.ICacheMgr#removeAll()
	 */
	@Override
	public void removeAll() {
		clearCache(this.type.getCacheName(this.cacheName), this.type);
	}
	
	// static helper methods -------------------------------------------
	
	protected static ServletRequestAttributes getRequestAttrs() {
		return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	}
	
	private synchronized static Cache getEHCache(String cacheName, CacheType type) {
		cacheName = type.getCacheName(cacheName);
		Cache ret = manager.getCache(cacheName);
		if (ret == null) {
			// create new cache by cloning from the appropriate cache
			CacheConfiguration config = null;
			if (type == CacheType.CUSTOM) {
				throw new IllegalStateException("CacheName: " + cacheName + ", does not exist in ehcache.xml");
			} else if (type == CacheType.SESSION_MEMORY) {
				config = manager.getCache("CacheMgrSess").getCacheConfiguration().clone();
			} else if (type == CacheType.DISK) {
				config = manager.getCache("CacheMgrDisk").getCacheConfiguration().clone();
			} else {
				config = manager.getCache("CacheMgrMem").getCacheConfiguration().clone();
			}
			config.setName(cacheName);
			ret = new Cache(config);
			manager.addCache(ret);
		}
		return ret;
	}
	
	// static methods -------------------------------
	
	public static Object fetch(String cacheName, String key, CacheType type) {
		if (key == null || cacheName == null) {
			throw new NullPointerException("CacheName + Key cannot be null");
		} else if (type == CacheType.NONE) {
			return null;
		} else if (type == CacheType.REQUEST || type == CacheType.SESSION) {
			ServletRequestAttributes attr = getRequestAttrs();
			int scope = (type == CacheType.REQUEST) ? ServletRequestAttributes.SCOPE_REQUEST : ServletRequestAttributes.SCOPE_SESSION;
			Map<String, Object> cachedVals = (Map<String, Object>) attr.getAttribute(cacheName, scope);
			if (cachedVals != null) {
				Long expiresAt = (Long) cachedVals.get(key + "_EXPIRESAT");
				if (expiresAt == null || System.currentTimeMillis() < expiresAt) {
					return cachedVals.get(key);
				}
			}
		} else {
			Cache cache = getEHCache(cacheName, type);
			Element e = cache.get(key);
			return ((e != null) ? e.getObjectValue() : null);
		}
		return null; // not found
	}

	public static Object store(String cacheName, String key, Object val, CacheType type) {
		return store(cacheName, key, val, type, -1);
	}
	
	public static Object store(String cacheName, String key, Object val, CacheType type, int ttlSec) {
		if (key == null || cacheName == null) {
			throw new NullPointerException("CacheName + Key cannot be null");
		} else if (type == CacheType.NONE) {
			return val;
		} else if (type == CacheType.REQUEST || type == CacheType.SESSION) {
			ServletRequestAttributes attr = getRequestAttrs();
			int scope = (type == CacheType.REQUEST) ? ServletRequestAttributes.SCOPE_REQUEST : ServletRequestAttributes.SCOPE_SESSION;
			Map<String, Object> cachedVals = (Map<String, Object>) attr.getAttribute(cacheName, scope);
			if (cachedVals == null) {
				cachedVals = new WeakHashMap<String, Object>();
			}
			
			cachedVals.put(key, val);
			if (ttlSec > 0) {
				cachedVals.put(key + "_EXPIRESAT", System.currentTimeMillis() + (ttlSec * 1000));
			}
			attr.setAttribute(cacheName, cachedVals, scope);
		} else {
			Cache cache = getEHCache(cacheName, type);
			Element e = new Element(key, val);
			if (ttlSec > 0) {
				e.setTimeToLive(ttlSec);
			}
			cache.put(e);
		}
		return val;
	}
	
	public static boolean contains(String cacheName, String key, CacheType type) {
		if (key == null || cacheName == null) {
			throw new NullPointerException("CacheName + Key cannot be null");
		} else if (type == CacheType.NONE) {
			return false;
		} else if (type == CacheType.REQUEST || type == CacheType.SESSION) {
			ServletRequestAttributes attr = getRequestAttrs();
			int scope = (type == CacheType.REQUEST) ? ServletRequestAttributes.SCOPE_REQUEST : ServletRequestAttributes.SCOPE_SESSION;
			Map<String, Object> cachedVals = (Map<String, Object>) attr.getAttribute(cacheName, scope);
			return (cachedVals != null && cachedVals.containsKey(key));
		} else {
			Cache cache = getEHCache(cacheName, type);
			return cache.isKeyInCache(key);
		}
	}
	
	public static int getSize(String cacheName, CacheType type) {
		if (cacheName == null) {
			throw new NullPointerException("CacheName + Key cannot be null");
		} else if (type == CacheType.NONE) {
			return 0;
		} else if (type == CacheType.REQUEST || type == CacheType.SESSION) {
			ServletRequestAttributes attr = getRequestAttrs();
			int scope = (type == CacheType.REQUEST) ? ServletRequestAttributes.SCOPE_REQUEST : ServletRequestAttributes.SCOPE_SESSION;
			Map<String, Object> cachedVals = (Map<String, Object>) attr.getAttribute(cacheName, scope);
			return (cachedVals == null) ? 0 : cachedVals.size();
		} else {
			Cache cache = getEHCache(cacheName, type);
			return cache.getKeysNoDuplicateCheck().size();
		}
	}
	
	public static void removeAny(String cacheName, String key) {
		remove(cacheName, key, CacheType.REQUEST);
		remove(cacheName, key, CacheType.SESSION);
		remove(cacheName, key, CacheType.MEMORY);
		remove(cacheName, key, CacheType.DISK);
	}
	
	public static void remove(String cacheName, String key, CacheType type) {
		if (key == null || cacheName == null) {
			throw new NullPointerException("CacheName + Key cannot be null");
		} else if (type == CacheType.NONE) {
			return;
		} else if (type == CacheType.REQUEST || type == CacheType.SESSION) {
			ServletRequestAttributes attr = getRequestAttrs();
			int scope = (type == CacheType.REQUEST) ? ServletRequestAttributes.SCOPE_REQUEST : ServletRequestAttributes.SCOPE_SESSION;
			Map<String, Object> cachedVals = (Map<String, Object>) attr.getAttribute(cacheName, scope);
			if (cachedVals == null) {
				return;
			}
			cachedVals.remove(key);
			attr.setAttribute(cacheName, cachedVals, scope);
		} else {
			Cache cache = getEHCache(cacheName, type);
			cache.remove(key);
		}
	}
	
	public synchronized static void clearCache(String cacheName, CacheType type) {
		if (cacheName == null) {
			throw new NullPointerException("CacheName + Key cannot be null");
		} else if (type == CacheType.NONE) {
			return;
		} else if (type == CacheType.REQUEST) {
			getRequestAttrs().removeAttribute(cacheName, ServletRequestAttributes.SCOPE_REQUEST);
		} else if (type == CacheType.SESSION) {
			getRequestAttrs().removeAttribute(cacheName, ServletRequestAttributes.SCOPE_SESSION);
		} else {
			Cache cache = getEHCache(cacheName, type);
			cache.removeAll();
		}
	}
	
	public synchronized static void clearCaches(String cacheName) {
		clearCache(cacheName, CacheType.REQUEST);
		clearCache(cacheName, CacheType.SESSION);
		clearCache(cacheName, CacheType.MEMORY);
		clearCache(cacheName, CacheType.DISK);
	}
}
