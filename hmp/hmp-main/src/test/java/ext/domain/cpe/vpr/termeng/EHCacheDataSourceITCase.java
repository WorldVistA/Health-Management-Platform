package EXT.DOMAIN.cpe.vpr.termeng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.UnknownHostException;
import java.sql.SQLException;

import net.sf.ehcache.Cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoException;

public class EHCacheDataSourceITCase {
	
	private EHCacheDataSource cache;
	private Cache ehcache;

	@Before
	public void setup() {
		cache = new EHCacheDataSource();
		ehcache = cache.getCache();
	}
	
	@After
	public void teardown() {
		ehcache.flush();
		ehcache.getCacheManager().shutdown();
	}
	
	@Test
	public void loadNDFCache() throws SQLException, UnknownHostException, MongoException {
		// check that they were loaded
		//assertTrue(ehcache.calculateOnDiskSize() > 1000);

		// check that the specific concept exists
		assertNotNull(cache.getConceptData("urn:vandf:4000624"));
		assertEquals("ATROPINE SO4 0.4MG TAB", cache.getDescription("urn:vandf:4000624"));
		assertEquals(1, cache.getParentSet("urn:vandf:4000624").size());
		assertEquals(5, cache.getEquivalentSet("urn:vandf:4000624").size());
		assertEquals(1, cache.getAncestorSet("urn:vandf:4000624").size());
	}

}
