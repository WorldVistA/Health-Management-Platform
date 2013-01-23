package org.osehra.cpe.vpr.dao.solr

import org.junit.Test
import org.junit.Before
import org.apache.solr.client.solrj.SolrServer
import org.springframework.core.convert.ConversionService

import static org.mockito.Mockito.mock
import org.osehra.cpe.vpr.search.SolrMockito

import static org.mockito.Mockito.verify
import org.apache.solr.common.SolrInputDocument

import static org.mockito.Mockito.when
import static org.mockito.Mockito.never
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.PatientFacility
import org.osehra.cpe.vpr.ResultOrganizer
import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vpr.Result
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk

import static org.mockito.Matchers.anyMapOf
import org.osehra.cpe.test.mockito.ReturnsArgument
import org.mockito.ArgumentCaptor

import static org.junit.Assert.assertThat
import org.osehra.cpe.vpr.VprConstants

import static org.hamcrest.CoreMatchers.sameInstance
import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.sameInstance
import org.osehra.cpe.vpr.VitalSignOrganizer
import org.osehra.cpe.vpr.VitalSign

import static org.mockito.Matchers.anyMapOf
import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.sameInstance
import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.sameInstance

class DefaultSolrDaoTests {

    DefaultSolrDao solrDao = new DefaultSolrDao()
    SolrServer mockSolrServer;
    ConversionService mockConversionService;

    @Before
    void setUp() {
        mockSolrServer = SolrMockito.mockSolrServer();
        mockConversionService = mock(ConversionService.class);

        solrDao = new DefaultSolrDao();
        solrDao.solrServer = mockSolrServer
        solrDao.conversionService = mockConversionService
    }

    @Test
    void testIndex() {
        Solr1 item = new Solr1(id: 23)

        SolrInputDocument doc = new SolrInputDocument()
        doc.addField("id", 23);

        when(mockConversionService.convert(item, SolrInputDocument.class)).thenReturn(doc);

        solrDao.index(item);

        verify(mockConversionService).convert(item, SolrInputDocument.class);
        verify(mockSolrServer).add(doc);
        verify(mockSolrServer).commit();
    }

    @Test
    void testIndexNoCommit() {
        Solr1 item = new Solr1(id: 23)

        SolrInputDocument doc = new SolrInputDocument()
        doc.addField("id", 23);

        when(mockConversionService.convert(item, SolrInputDocument.class)).thenReturn(doc);

        solrDao.index(item, false);

        verify(mockConversionService).convert(item, SolrInputDocument.class);
        verify(mockSolrServer).add(doc);
        verify(mockSolrServer, never()).commit();
    }

    @Test
    void testIndexResultOrganizer() {

        ResultOrganizer ro = new ResultOrganizer([pid: "1", facilityCode: "500", facilityName:"CAMP MASTER", localId: "CH;6959389.875453", observed: new PointInTime(1975, 7, 23, 10, 58), specimen: "BLOOD", organizerType: "accession"]);

        Result sodium = new Result([localId: "CH;6959389.875453;2", typeName: "SODIUM", result: "140", units: "meq/L"]);
        Result potassium = new Result([localId: "CH;6949681.986571;6", typeName: "POTASSIUM", result: "5.2", units: "meq/L"]);
        ro.addToResults(sodium);
        ro.addToResults(potassium);

        solrDao.index(ro, false)

        verify(mockConversionService).convert(sodium, SolrInputDocument.class);
        verify(mockConversionService).convert(potassium, SolrInputDocument.class);
        verify(mockSolrServer, never()).commit();
    }

    @Test
    void testSaveVitalSignOrganizer() {
        Patient pt = new Patient(id: 1L, icn: "12345", lastUpdated: PointInTime.now());
        PatientFacility facility = new PatientFacility(code: "500", name: "CAMP MASTER", homeSite: true, localPatientId: "229");

        VitalSignOrganizer vitals = new VitalSignOrganizer([pid: "1",
                facility: facility,
                uid: "urn:va:vs:ABCDEF:229:70:20090115171037.000",
                observed: new PointInTime(2009, 1, 15, 17, 18, 0, 0),
                resulted: new PointInTime(2009, 1, 15, 17, 10, 37, 0),
                location: 'ER']);

        VitalSign bloodPressure = new VitalSign([uid: 'urn:va:vs:ABCDEF:20679', typeName: 'BLOOD PRESSURE', result: '170/120'])
        VitalSign temp = new VitalSign([uid: 'urn:va:vs:ABCDEF:20679', typeName: 'TEMPERATURE', result: '101'])
        vitals.addToVitalSigns(bloodPressure);
        vitals.addToVitalSigns(temp);

        solrDao.index(vitals, false)

        verify(mockConversionService).convert(bloodPressure, SolrInputDocument.class);
        verify(mockConversionService).convert(temp, SolrInputDocument.class);
        verify(mockSolrServer, never()).commit();
    }
}
