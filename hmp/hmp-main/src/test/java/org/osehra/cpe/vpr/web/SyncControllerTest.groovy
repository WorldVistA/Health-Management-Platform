package org.osehra.cpe.vpr.web;

import static org.junit.Assert.*
import static org.mockito.Matchers.*
import static org.mockito.Mockito.*
import org.osehra.cpe.auth.HmpUserDetails
import org.osehra.cpe.auth.UserContext;
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.sync.ISyncService

import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.servlet.ModelAndView


class SyncControllerTest {
	SyncController controller;
	IPatientDAO patientDaoMock;
	
	@Before
	public void setUp() throws Exception {
		controller = new SyncController();
		patientDaoMock = mock(IPatientDAO.class)
		controller.setPatientDao(patientDaoMock)
	}
	
	@Test
	public void testIsSelectedPatientToReload_LocalId() throws Exception {
		def pt = new Patient(pid:"2")
		
		when(patientDaoMock.findByLocalID("545","8")).thenReturn(pt);
		assertTrue(controller.isSelectedPatientToReload("2", "8", "545", null))
		assertFalse(controller.isSelectedPatientToReload("3", "", "", ""))
	}
	
	@Test
	public void testIsSelectedPatientToReload_Dfn() throws Exception {
		def pt = new Patient(pid:"2")
				
		when(patientDaoMock.findByIcn("1001")).thenReturn(pt);
		assertTrue(controller.isSelectedPatientToReload("2", null, null, "1001"))
		assertFalse(controller.isSelectedPatientToReload("3", null, null, "1001"))
	}
	
	@Test
	public void testClearAllPatient() throws Exception {
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.session.setAttribute("pid", "3");
		ISyncService syncServiceMock = mock(ISyncService.class)
		controller.setSyncService(syncServiceMock)
		
	    ModelAndView mv = controller.clearAllPatient(mockRequest)
		assertEquals("contentNegotiatingView", mv.getViewName())
		assertNull(mockRequest.session.getAttribute("pid"));
		
		verify(syncServiceMock, times(1)).sendClearAllPatientsMsg()
		
	}
	
	@Test
	public void testClearPatient_LocalId() throws Exception {
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.session.setAttribute("pid", "33");
		
		ISyncService syncServiceMock = mock(ISyncService.class)
		controller.setSyncService(syncServiceMock)
		
		def pt = new Patient(pid:"3")
		when(patientDaoMock.findByLocalID("545","8")).thenReturn(pt);
		
		ModelAndView mv = controller.clearPatient(null, "8", "545", null, mockRequest)
		
		assertEquals("contentNegotiatingView", mv.getViewName())
		assertEquals("33",mockRequest.session.getAttribute("pid"));
		
		verify(syncServiceMock, times(1)).sendClearPatientMsg(pt)
		
	}
	@Test
	public void testClearPatient_Dfn() throws Exception {
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.session.setAttribute("pid", "3");
		
		ISyncService syncServiceMock = mock(ISyncService.class)
		controller.setSyncService(syncServiceMock)
				
		def pt = new Patient(pid:"3")
		when(patientDaoMock.findByIcn("1001")).thenReturn(pt);
		
		ModelAndView mv = controller.clearPatient("1001", null, null, null, mockRequest)
				
		assertEquals("contentNegotiatingView", mv.getViewName())
		assertNull(mockRequest.session.getAttribute("pid"));
		verify(syncServiceMock, times(1)).sendClearPatientMsg(pt)
	}
	
	@Test
	public void load() throws Exception {
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.session.setAttribute("pid", "3");
		
		ISyncService syncServiceMock = mock(ISyncService.class)
		UserContext userContextMock = mock(UserContext.class)
		HmpUserDetails userDetails = mock(HmpUserDetails.class)
		
		controller.setSyncService(syncServiceMock)
		controller.setUserContext(userContextMock)
		
		def pt = new Patient(pid:"3")
		when(userContextMock.getCurrentUser()).thenReturn(userDetails)
		when(userDetails.getVistaId()).thenReturn("545")
		when(patientDaoMock.findByIcn("1001")).thenReturn(pt);
		
		ModelAndView mv = controller.load("1001", null, mockRequest)
				
		assertEquals("contentNegotiatingView", mv.getViewName())
		assertNull(mockRequest.session.getAttribute("pid"));
		verify(syncServiceMock, times(1)).sendLoadPatientMsgWithIcn("545", "1001")
	}

}
