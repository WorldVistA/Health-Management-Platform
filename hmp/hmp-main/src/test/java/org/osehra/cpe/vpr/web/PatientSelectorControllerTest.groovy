package org.osehra.cpe.vpr.web;
import org.osehra.cpe.vpr.sync.ISyncService
import org.osehra.cpe.vpr.RosterService

import java.util.List;
import java.util.Map;



import static org.junit.Assert.*;
import org.osehra.cpe.auth.HmpUserDetails
import org.osehra.cpe.auth.UserContext

import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpSession

import static org.mockito.Matchers.*
import static org.mockito.Mockito.*

class PatientSelectorControllerTest {
	
	@Test
	public void testAddSelectedPatient() throws Exception {
		PatientSelectorController controller = new PatientSelectorController()
		UserContext userContext = mock(UserContext.class)
		HmpUserDetails userDetails = mock(HmpUserDetails.class)
		RosterService rosterService = new RosterService(){
			@Override
			public List<Map> addPatientToRoster(String dfn, String rosterId) {
				assertEquals(dfn,'123')
				assertEquals(rosterId,'1729')
				return []
			}
		}
		ISyncService syncService = mock(ISyncService.class)
		
		controller.userContext = userContext
		controller.rosterService = rosterService
		controller.syncService = syncService
		
		 
	    def request = new MockHttpServletRequest()
		def session = new MockHttpSession()
		session.setAttribute('rosterID', '1729')
		request.setSession(session) 
		when(userContext.getCurrentUser()).thenReturn(userDetails)
		when(userDetails.getVistaId()).thenReturn('5098')
		
		controller.addSelectedPatient('123', request)
		
		verify(syncService, times(1)).sendLoadPatientMsgWithDfn('5098','123')
		
	}

}
