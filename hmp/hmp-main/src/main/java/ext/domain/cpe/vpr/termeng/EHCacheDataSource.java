package org.osehra.cpe.vpr.termeng;

import org.osehra.cpe.vpr.vistasvc.CacheMgr.CacheType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

public class EHCacheDataSource extends AbstractTermDataSource implements ITermDataSource {
	private static CacheManager manager = CacheManager.getInstance();
	private Cache cache;
	
	
	public EHCacheDataSource() {
		this.cache = manager.getCache("TermEngDB");
	}
	
	public EHCacheDataSource(Cache cache) {
		this.cache = cache;
	}
	
	// Custom functionality to populate cache ---------------------------------
    public void save(Map<String, Object> data) {
    	String urn = (String) data.get("urn");
    	
    	Element e = new Element(urn, data);
    	cache.put(e);
    }
    
    protected Cache getCache() {
    	return this.cache;
    }
    
    /**
     * Utility function to build a full EHCache database of concepts
     * @throws SQLException 
     */
    public static void main(String[] args) throws SQLException {
    	// setup JDBC connection
    	String jdbcurl = "jdbc:postgresql://localhost:5432/postgres";
		String uname = "umls2012aa";
		String pword = "umls2012aa";
		Connection conn = DriverManager.getConnection(jdbcurl, uname, pword);
		JSONBuilderDataSource src = new JSONBuilderDataSource(conn, "umls2012aa");
		
		// loop through each source system and load it into cache
    	for (String sys : src.getCodeSystemList()) {
    		loadSAB(src, sys);
    	}
    	
    }
    
    private static void loadSAB(JSONBuilderDataSource src, String sab) {
    	
    	// get the cache file we want
		Cache ehcache = getCache(sab);
		System.out.format("Current cache size: %d (%s)\n", ehcache.getSize(), ehcache.getName());

		EHCacheDataSource cachesrc = new EHCacheDataSource(ehcache);
		int count = src.getConceptCount(sab);
		System.out.format("Loading: %s (count: %d) \n", sab, count);
		
		int i=0;
		int termcount=0;
		String statusStr = "%2.2f%% complete. (%d/%d terms, %d concepts)\n";
		Iterator<String> itr = src.iterator(sab);
		while (itr.hasNext()) {
			String urn = itr.next();
			Map<String, Object> data = src.getConceptData(urn);
			cachesrc.save(data);
			if (data.containsKey("terms") && data.get("terms") instanceof List) {
				termcount += ((List) data.get("terms")).size();
			}
			if (++i % 1000 == 0) {
				float pct = ((float) termcount/ (float) count) * 100.0f;
				System.out.format(statusStr, pct, termcount, count, i);
			}
		}
		System.out.format(statusStr, 100.0, termcount, count, i);
		
		// shutdown/cleanup
		ehcache.flush();
    }
    
    private static Cache getCache(String sab) {
    	String cacheName = "TermEngDB_" + sab;
		Cache ret = manager.getCache(cacheName);
		if (ret == null) {
			// create new cache by cloning
			CacheConfiguration config = manager.getCache("TermEngDB").getCacheConfiguration().clone();
			config.setName(cacheName);
			ret = new Cache(config);
			manager.addCache(ret);
		}
		return ret;
    }
    
    // ------------------------------------------------------------------------
	
	@Override
	public Set<String> getCodeSystemList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<String, Object> getCodeSystemMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getConceptData(String urn) {
		Element e = cache.get(urn);
		if (e != null) {
			return (Map<String, Object>) e.getObjectValue();
		}
		return null;
	}

	
	
}
