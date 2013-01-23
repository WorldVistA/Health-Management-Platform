package org.osehra.cpe.vpr.sync.msg;


import  org.junit.Test;
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

import org.osehra.cpe.vpr.*

import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;
import static org.mockito.Mockito.*;


import org.osehra.cpe.vpr.dao.ISolrDao;

class ReindexPatientMessageHandlerTest {
		@Test
		public void testOnMessage() throws Exception {
	
			def handler = new ReindexPatientMessageHandler()
	
			ISolrDao solrServiceMock = mock(ISolrDao.class)
			IGenericPatientObjectDAO genericPatientRelatedDaoMock = mock(IGenericPatientObjectDAO.class)
			handler.solrService = solrServiceMock
			handler.genericPatientRelatedDao = genericPatientRelatedDaoMock
	
			Allergy al = new Allergy(pid:'3',summary:'test')
			Page<Object> page = new PageImpl([al])
			when(genericPatientRelatedDaoMock.findAllByPID(Allergy, '3', null)).thenReturn(page)
	
			handler.onMessage([patientId:'3'])
			verify(genericPatientRelatedDaoMock, times(1)).findAllByPID(Allergy, '3', null)
			verify(solrServiceMock, times(1)).index(al, false)
			verify(solrServiceMock, times(1)).commit()
		}
		
	// This version has changes that will work with pagination, if we ever get there
	// The change need to happen in the JDS store and then in the GenericPatientRelatedDao, untill then...
	//void onMessage(Map msg) {
	//	String pid = msg[SyncMessageConstants.PATIENT_ID]
	//	assert pid
	//
	//	LOG.debug("Reindexing ${pid}")
	//
	//   for(domainClass in getDomains()){
	//		int total = genericPatientRelatedDao.countByPID(domainClass, pid)
	//			if(!total){ continue }
	//
	//			int pageCount = total.intdiv(getPageSize())
	//			pageCount = (pageCount>0) ? pageCount-1 : 0 // zero based pagination
	//
	//			for(pageIx in 0..pageCount){
	//				def pageItems = genericPatientRelatedDao.findAllByPID(domainClass, pid, new PageRequest(pageIx, getPageSize()))
	//			for(item in pageItems?.getContent()){
	//				try {
	//					solrService.index(item, false)
	//				} catch (Throwable t) {
	//					LOG.error("unable to reindex item ${item}", t)
	//				}
	//			}
	//		}
	//        }
	//
	//	solrService.commit()
	//}
	//
	//protected List<Class>getDomains(){
	//	return [Allergy, Document, Encounter, HealthFactor, Immunization, Medication, Order, Problem, Procedure, Result, VitalSign]
	//}
	//
	//protected int getPageSize(){
	//	return PAGE_SIZE
	//}
//	@Test
//	public void testOnMessage_OnePage() throws Exception {
//		
//		def handler = new ReindexPatientMessageHandler(){
//			@Override
//			protected List<Class> getDomains() {
//				return [Allergy]
//			}
//			@Override
//			protected int getPageSize() {
//				return 3;
//			}
//		}
//		
//		SolrDao solrServiceMock = mock(SolrDao.class)			
//		IGenericPatientObjectDAO genericPatientRelatedDaoMock = mock(IGenericPatientObjectDAO.class)
//		handler.solrService = solrServiceMock
//		handler.genericPatientRelatedDao = genericPatientRelatedDaoMock
//		
//		when(genericPatientRelatedDaoMock.countByPID(Allergy, '3')).thenReturn(2)
//		Allergy al = new Allergy(pid:'3',summary:'test')
//		Page<Object> page = new PageImpl([al])
//		when(genericPatientRelatedDaoMock.findAllByPID(Allergy, '3', new PageRequest(0,3))).thenReturn(page)
//		
//		handler.onMessage([patientId:'3'])
//		verify(genericPatientRelatedDaoMock, times(1)).countByPID(anyObject(), anyString())
//		verify(genericPatientRelatedDaoMock, times(1)).findAllByPID(anyObject(), anyString(), anyObject())
//		verify(solrServiceMock, times(1)).index(al, false)
//		verify(solrServiceMock, times(1)).commit()
//	}
//	
//	@Test
//	public void testOnMessage_NoItems() throws Exception {
//		
//		def handler = new ReindexPatientMessageHandler(){
//			@Override
//			protected List<Class> getDomains() {
//				return [Allergy]
//			}
//			@Override
//			protected int getPageSize() {
//				return 3;
//			}
//		}
//		
//		SolrDao solrServiceMock = mock(SolrDao.class)
//		IGenericPatientObjectDAO genericPatientRelatedDaoMock = mock(IGenericPatientObjectDAO.class)
//		handler.solrService = solrServiceMock
//		handler.genericPatientRelatedDao = genericPatientRelatedDaoMock
//		
//		when(genericPatientRelatedDaoMock.countByPID(Allergy, '3')).thenReturn(0)
//		
//		handler.onMessage([patientId:'3'])
//		verify(genericPatientRelatedDaoMock, times(1)).countByPID(anyObject(), anyString())
//	    verify(genericPatientRelatedDaoMock, times(0)).findAllByPID(anyObject(), anyString(), anyObject())
//		verify(solrServiceMock, times(0)).index(anyObject(), anyBoolean())
//		verify(solrServiceMock, times(1)).commit()
//	}
//	
//	@Test
//	public void testOnMessage_MultiplePages() throws Exception {
//		
//		def handler = new ReindexPatientMessageHandler(){
//			@Override
//			protected List<Class> getDomains() {
//				return [Allergy]
//			}
//			@Override
//			protected int getPageSize() {
//				return 1;
//			}
//		}
//		
//		SolrDao solrServiceMock = mock(SolrDao.class)
//		IGenericPatientObjectDAO genericPatientRelatedDaoMock = mock(IGenericPatientObjectDAO.class)
//		handler.solrService = solrServiceMock
//		handler.genericPatientRelatedDao = genericPatientRelatedDaoMock
//		
//		when(genericPatientRelatedDaoMock.countByPID(Allergy, '3')).thenReturn(2)
//		Allergy al1 = new Allergy(pid:'3',summary:'test')
//		Allergy al2 = new Allergy(pid:'3',summary:'test1')
//		when(genericPatientRelatedDaoMock.findAllByPID(Allergy, '3', new PageRequest(0,1))).thenReturn(new PageImpl([al1]))
//		when(genericPatientRelatedDaoMock.findAllByPID(Allergy, '3', new PageRequest(1,1))).thenReturn(new PageImpl([al2]))
//		
//		handler.onMessage([patientId:'3'])
//		verify(genericPatientRelatedDaoMock, times(1)).countByPID(anyObject(), anyString())
//		verify(genericPatientRelatedDaoMock, times(2)).findAllByPID(anyObject(), anyString(), anyObject())
//
//		verify(solrServiceMock, times(1)).index(al1, false)
//		verify(solrServiceMock, times(1)).index(al2, false)
//		verify(solrServiceMock, times(1)).commit()
//	}
//
//	@Test
//	public void testOnMessage_OnePageExact() throws Exception {
//		
//		def handler = new ReindexPatientMessageHandler(){
//			@Override
//			protected List<Class> getDomains() {
//				return [Allergy]
//			}
//			@Override
//			protected int getPageSize() {
//				return 2;
//			}
//		}
//		
//		SolrDao solrServiceMock = mock(SolrDao.class)
//		IGenericPatientObjectDAO genericPatientRelatedDaoMock = mock(IGenericPatientObjectDAO.class)
//		handler.solrService = solrServiceMock
//		handler.genericPatientRelatedDao = genericPatientRelatedDaoMock
//		
//		when(genericPatientRelatedDaoMock.countByPID(Allergy, '3')).thenReturn(2)
//		Allergy al1 = new Allergy(pid:'3',summary:'test')
//		Allergy al2 = new Allergy(pid:'3',summary:'test1')
//		Page<Object> page = new PageImpl([al1,al2])
//		when(genericPatientRelatedDaoMock.findAllByPID(Allergy, '3', new PageRequest(0,2))).thenReturn(page)
//		
//		handler.onMessage([patientId:'3'])
//		
//		verify(genericPatientRelatedDaoMock, times(1)).countByPID(anyObject(), anyString())
//		verify(genericPatientRelatedDaoMock, times(1)).findAllByPID(anyObject(), anyString(), anyObject())
//
//		verify(solrServiceMock, times(1)).index(al1, false)
//		verify(solrServiceMock, times(1)).index(al2, false)
//		verify(solrServiceMock, times(1)).commit()
//	}
}
