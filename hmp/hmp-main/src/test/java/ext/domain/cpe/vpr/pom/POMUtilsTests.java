package org.osehra.cpe.vpr.pom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static junit.framework.Assert.*;
import static org.osehra.cpe.vpr.pom.POMUtils.*;

public class POMUtilsTests {
	
	private static HashMap newMap(Object key, Object val) {
		HashMap m = new HashMap();
		m.put(key, val);
		return m;
	}

	
	@Test
	public void testMapPath() {
		// create a map with simple values
		HashMap map = new HashMap();
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);
		
		// add a simple sub-map
		map.put("x", new HashMap(map));
		
		// add complex values: list of maps with same key
		ArrayList<Map> al = new ArrayList<Map>();
		al.add(new HashMap(map));
		al.add(new HashMap(map));
		al.add(new HashMap(map));
		map.put("y", al);
		
		// add complex values: list of maps with different keys 
		al = new ArrayList<Map>();
		al.add(newMap("a", "x"));
		al.add(newMap("b", "y"));
		al.add(newMap("c", "z"));
		map.put("z", al);
		
		// test expected null results..
		assertNull(getMapPath(map, "blablabla"));
		assertNull(getMapPath(map, null));
		assertNull(getMapPath(null, "a"));
		
		// test simple values, should behave just like normal get()
		assertEquals(1, getMapPath(map, "a"));
		assertEquals(2, getMapPath(map, "b"));
		assertEquals(3, getMapPath(map, "c"));
		assertTrue(getMapPath(map, "x") instanceof Map);
		assertTrue(getMapPath(map, "y") instanceof List);
		assertTrue(getMapPath(map, "z") instanceof List);
		
		// test dot notation
		assertEquals(1, getMapPath(map, "x.a"));
		assertEquals(2, getMapPath(map, "x.b"));
		assertEquals(3, getMapPath(map, "x.c"));
		assertNull(getMapPath(map, "x.x"));
		assertNull(getMapPath(map, "zzz.abc")); // TODO: Is this the correct behavior or not?

		// test gather '[]' syntax: y has identical sub-maps, z does not
		Object o = getMapPath(map, "y[].a");
		assertTrue(o instanceof List);
		List l = (List) o;
		assertEquals(3, l.size());
		assertEquals(1, l.get(0));
		assertEquals(1, l.get(1));
		assertEquals(1, l.get(2));
		
		o = getMapPath(map, "z[].a");
		assertTrue(o instanceof List);
		l = (List) o;
		assertEquals(1, l.size());
		assertEquals("x", l.get(0));
		
		// test error conditions
		try {
			// TODO: error condition: z[] -> requires subkey
			getMapPath(map, "z[]");
			fail("Expected Exception");
		} catch (Exception ex) {
			// expected
		}
		
		try {
			// TODO: error condition x.y where y is a list?
			getMapPath(map, "y.a");
			fail("Expected Exception");
		} catch (Exception ex) {
			// expected
		}

		try {
			// TODO: error conditions x[].nonarray
			getMapPath(map, "x[].a");
			fail("Expected Exception");
		} catch (Exception ex) {
			// expected
		}

	}
}
