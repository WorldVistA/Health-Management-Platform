package org.osehra.cpe.vpr.web.view;

import static org.junit.Assert.*;
import org.junit.Test

class VPRMappingJacksonJsonViewTest {
	@Test
	public void testIsFilterNeeded() throws Exception {
		def view = new VPRMappingJacksonJsonView();
		view.setModelKey("response");
		assertTrue(view.isFilterNeeded([response:[data:['foo','bar']]]))
		assertTrue(view.isFilterNeeded([response:[data:['foo','bar']], boo:['baa']]))
		assertFalse(view.isFilterNeeded([request:[data:['foo','bar']], boo:['baa']]))		
		assertFalse(view.isFilterNeeded([data:[response:['foo','bar']]]))
	}

	@Test
	public void testFilterData() throws Exception {
		
		def view = new VPRMappingJacksonJsonView();
		view.setModelKey("response");
		view.setExtractValueFromSingleKeyModel(true)
		
		def map = [response:[data:['foo','bar']]]
		assertEquals(view.filterModel(map),[data:['foo','bar']])
		
		map = [data:[response:['foo','bar']]]
		def expected = view.filterModel(map)
		assertEquals(expected,[data:[response:['foo','bar']]])
		
	}
}
