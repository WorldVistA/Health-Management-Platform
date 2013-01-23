package org.osehra.cpe.vpr.vistasvc;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.osehra.cpe.vpr.vistasvc.CacheMgr.CacheType;
import org.osehra.cpe.vpr.vistasvc.VistAServiceTests.HashMapAnswers;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

public class CacheMgrTests extends EhCacheTestUtils {
	public static String NAMESPACE = "TESTING";

    private ServletRequestAttributes attsMock;

    @BeforeClass
    public static void init() throws IOException {
        EhCacheTestUtils.setUp();
    }

    @AfterClass
    public static void shutdown() throws IOException {
        EhCacheTestUtils.tearDown();
    }

    @Before
	public void setup() {
        // setup a mock of the request/session context
		Answer myAnswer = new HashMapAnswers();
		attsMock = mock(ServletRequestAttributes.class);
		when(attsMock.getAttribute(anyString(), anyInt())).then(myAnswer);
		doAnswer(myAnswer).when(attsMock).setAttribute(anyString(), anyObject(), anyInt());
		doAnswer(myAnswer).when(attsMock).removeAttribute(anyString(), anyInt());
		RequestContextHolder.setRequestAttributes(attsMock);
	}

	@Test
	public void testInstanceObjectMEMORY() {
		testInstanceObject(new CacheMgr<String>(NAMESPACE, CacheType.MEMORY));
	}
	
	@Test
	public void testInstanceObjectDISK() {
		testInstanceObject(new CacheMgr<String>(NAMESPACE, CacheType.DISK));
	}

	@Test
	public void testInstanceObjectREQUEST() {
		testInstanceObject(new CacheMgr<String>(NAMESPACE, CacheType.REQUEST));
	}
	
	@Test
	public void testInstanceObjectSESSION() {
		testInstanceObject(new CacheMgr<String>(NAMESPACE, CacheType.SESSION));
	}
	
	@Test
	public void testInstanceObjectSESSION_MEMORY() {
		testInstanceObject(new CacheMgr<String>(NAMESPACE, CacheType.SESSION_MEMORY));
	}
	
	@Test
	public void testInstanceObjectNONE() {
		ICacheMgr cache = new CacheMgr<String>(NAMESPACE, CacheType.NONE);
		
		// this one is special in that it basically never stores anything and fetch
		// always returns null
		assertEquals(0, cache.getSize());
		assertEquals("bar", cache.store("foo", "bar"));
		assertEquals(0, cache.getSize());
		assertNull(cache.fetch("foo"));
		assertFalse(cache.contains("foo"));
	}
	
	// generic test, run for each CacheType
	public void testInstanceObject(ICacheMgr<String> mgr) {
		// store a simple value
		String val = mgr.store("foo", "bar");
		assertEquals("bar", val);
		
		// fetch it back out
		val = mgr.fetch("foo");
		assertNotNull(val);
		assertEquals("bar", val);
		assertEquals(1, mgr.getSize());
		
		// remove it, should be gone from cache
		assertTrue(mgr.contains("foo"));
		mgr.remove("foo");
		assertNull(mgr.fetch("foo"));
		assertFalse(mgr.contains("foo"));
		assertEquals(0, mgr.getSize());
		
		// remove an non-existant key is ok, but contains will be false
		assertFalse(mgr.contains("asdfasdfasdf"));
		mgr.remove("asdfasdfasdf");
		assertNull(mgr.fetch("asdfasdfasdf"));
		assertFalse(mgr.contains("asdfasdfasdf"));
		
		// insert 2 keys
		mgr.store("foo", "a");
		mgr.store("bar", "b");
		assertEquals(2, mgr.getSize());
		assertNotNull(mgr.fetch("foo"));
		assertNotNull(mgr.fetch("bar"));
		mgr.removeAll();
		assertEquals(0, mgr.getSize());
		assertNull(mgr.fetch("foo"));
		assertNull(mgr.fetch("bar"));
		
		// store null value is ok, but it still exists
		mgr.store("foo", null);
		assertNull(mgr.fetch("foo"));
		assertTrue(mgr.contains("foo"));
		mgr.remove("foo");
		
		// null key is not ok for store/fetch/remove/contains
		try {
			mgr.store(null, "asdf");
			fail("Expected NPE");
		} catch (NullPointerException ex) {
			// expected
		}
		try {
			mgr.fetch(null);
			fail("Expected NPE");
		} catch (NullPointerException ex) {
			// expected
		}
		try {
			mgr.remove(null);
			fail("Expected NPE");
		} catch (NullPointerException ex) {
			// expected
		}
		
		// null safe add function avoids NPEs, and skips null values
		assertEquals(0, mgr.getSize());
		mgr.storeUnlessNull(null, null);
		mgr.storeUnlessNull("foo", null);
		mgr.storeUnlessNull(null, "bar");
		assertEquals(0, mgr.getSize());
		
		// test TTL
		try {
			// store something for 1 second, should be available immediately,
			// but after waiting for >1s, should return null
			mgr.store("foo", "bar", 1);
			assertNotNull(mgr.fetch("foo"));
			Thread.sleep(1100);
			assertNull(mgr.fetch("foo"));
		} catch (Exception ex) {
			// ignore
		}
		
	}
}
