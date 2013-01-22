package EXT.DOMAIN.cpe.vpr.web;

import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.vpr.NotFoundException
import EXT.DOMAIN.cpe.vpr.Problem
import EXT.DOMAIN.cpe.vpr.Result
import EXT.DOMAIN.cpe.vpr.VitalSign
import EXT.DOMAIN.cpe.vpr.mapping.ILinkService
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO;
import EXT.DOMAIN.cpe.vpr.queryeng.query.QueryDef
import EXT.DOMAIN.cpe.vpr.ws.link.LinkRelation

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.Matchers;


import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.joda.time.DateTime
import org.joda.time.chrono.ISOChronology;

class TrendControllerTest {

    @Test
    public void testCreateTrendData() throws Exception {
        TrendController tc = new TrendController()
        def list = []
        def result = new Result("pid": "123",
                "facilityName": "SLC-FO HMP DEV",
                "high": 100,
                "low": "Neg.",
                "observed": 201205291802,
                "result": 12.5,
                "specimen": "URINE",
                "typeName": "URINE GLUCOSE",
                "uid": "urn:va:F484:8:lab:CH;6879469.819787;690",
                "units": "mg/dL")
        list.add(result)
        List trendData = tc.createTrendData(list)
        assertEquals(1, trendData.size())
        DateTime t = new DateTime(2012, 5, 29, 18, 2, 30, 0)
        assertEquals([x:t.millis, y:12.5F], trendData.get(0))

    }

    @Test
    public void testCreateTrendData_ResultMissing() throws Exception {
        TrendController tc = new TrendController()
        def list = []
        def result = new Result("pid": "123",
                "facilityName": "SLC-FO HMP DEV",
                "high": 100,
                "low": "Neg.",
                "observed": 201205291802,
                "result": "Neg.",
                "specimen": "URINE",
                "typeName": "URINE GLUCOSE",
                "uid": "urn:va:F484:8:lab:CH;6879469.819787;690",
                "units": "mg/dL")
        list.add(result)
        List trendData = tc.createTrendData(list)
        assertEquals(0, trendData.size())
    }

    @Test
    public void testCreateTrendData_NoObserved() throws Exception {
        TrendController tc = new TrendController()
        def list = []
        def result = new Result("pid": "123",
                "facilityName": "SLC-FO HMP DEV",
                "high": 100,
                "low": "Neg.",
                "result": "12",
                "specimen": "URINE",
                "typeName": "URINE GLUCOSE",
                "uid": "urn:va:F484:8:lab:CH;6879469.819787;690",
                "units": "mg/dL")
        list.add(result)
        List trendData = tc.createTrendData(list)
        assertEquals(0, trendData.size())
    }

    @Test
    public void testCreateTrendData_ObservedIncomplete() throws Exception {
        TrendController tc = new TrendController()
        def list = []
        def result = new Result("pid": "123",
                "facilityName": "SLC-FO HMP DEV",
                "observed": 201209,
                "result": "12",
                "high": 100,
                "low": "Neg.",
                "specimen": "URINE",
                "typeName": "URINE GLUCOSE",
                "uid": "urn:va:F484:8:lab:CH;6879469.819787;690",
                "units": "mg/dL")
        list.add(result)
        List trendData = tc.createTrendData(list)
        assertEquals(0, trendData.size())
    }

    @Test
    public void testGetIndex() {
        TrendController tc = new TrendController()
        assertEquals(TrendController.LAB_INDEX, tc.getIndex(new Result()))
        assertEquals(TrendController.VITAL_INDEX, tc.getIndex(new VitalSign()))
        try {
            assertEquals(TrendController.VITAL_INDEX, tc.getIndex(new Problem()))
            assertFalse(true)//should never get here
        } catch (Exception e) {
            assertEquals('Trend  type is invalid. Valid types: result, vitalSign', e.getMessage())
        }
    }

    @Test
    public void testPitToJsDate() throws Exception {
        TrendController tc = new TrendController()
        DateTime t = new DateTime(2001, 10, 22, 12, 0, 0, 0);
        assertEquals(t.millis, tc.pitToJsDate(new PointInTime(2001, 10, 22)))
        assertEquals(null, tc.pitToJsDate(new PointInTime(2001, 10)))
    }

    @Test
    public void testCreateLink() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI('/vpr/trend/urn:va:F484:8:lab:CH;6879469.819787;690')
        IGenericPatientObjectDAO genericPatientObjectDaoMock = mock(IGenericPatientObjectDAO.class)
        ILinkService linkServiceMock = mock(ILinkService.class)
        def result = new Result()
        when(genericPatientObjectDaoMock.findByUID(Result, 'urn:va:F484:8:lab:CH;6879469.819787;690')).thenReturn(result)
        def link = new Link()
        link.rel = LinkRelation.TREND
        when(linkServiceMock.getLinks(result)).thenReturn([link])

        TrendController tc = new TrendController()
        tc.genericPatientObjectDao = genericPatientObjectDaoMock
        tc.linkService = linkServiceMock

        assertNotNull(tc.createLink(mockRequest))

    }

    @Test
    public void testCreateLink_NoMatch() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI('/vpr/trend/urn:va:F484:8:lab:CH;6879469.819787;690')
        IGenericPatientObjectDAO genericPatientObjectDaoMock = mock(IGenericPatientObjectDAO.class)
        ILinkService linkServiceMock = mock(ILinkService.class)
        def result = new Result()
        when(genericPatientObjectDaoMock.findByUID(Result, 'urn:va:F484:8:lab:CH;6879469.819787;690')).thenReturn(result)
        when(linkServiceMock.getLinks(result)).thenReturn([new Link()])

        TrendController tc = new TrendController()
        tc.genericPatientObjectDao = genericPatientObjectDaoMock
        tc.linkService = linkServiceMock

        try {
            tc.createLink(mockRequest)
            assertTrue(false)//should never get here
        } catch (NotFoundException nfe) {
            assertEquals('No trend found for item with uid=urn:va:F484:8:lab:CH;6879469.819787;690', nfe.getMessage())
        }
    }

    @Test
    public void testRenderJson() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI('/vpr/trend/urn:va:F484:8:lab:CH;6879469.819787;690')
        IGenericPatientObjectDAO genericPatientObjectDaoMock = mock(IGenericPatientObjectDAO.class)
        ILinkService linkServiceMock = mock(ILinkService.class)
        def result = new Result(pid: 22L, typeName: "GLUCOSE")
        when(genericPatientObjectDaoMock.findByUID(Result, 'urn:va:F484:8:lab:CH;6879469.819787;690')).thenReturn(result)
        def link = new Link()
        link.rel = LinkRelation.TREND
        when(linkServiceMock.getLinks(result)).thenReturn([link])
        QueryDef veriyfyQryDef = new QueryDef();
        veriyfyQryDef.namedIndexRange('lab-type/summary', "GLUCOSE", null);
        when(genericPatientObjectDaoMock.findAllByQuery(Matchers.any(Class), (QueryDef) Matchers.any(QueryDef), Matchers.anyMap())).thenReturn([new Result(pid: 22L, "observed": 20120920, "result": "12")])


        TrendController tc = new TrendController()
        tc.genericPatientObjectDao = genericPatientObjectDaoMock
        tc.linkService = linkServiceMock

        def mv = tc.renderJson(mockRequest)
        assertNotNull(mv)
        def resp = mv.model.response
        assertNotNull(resp)
        assertEquals("GLUCOSE", resp.additionalData.name)
        assertEquals("line", resp.additionalData.type)
        DateTime t = new DateTime(2012, 9, 20, 12, 0, 0, 0)
        assertEquals([x:t.millis, y:12.0F], resp.data.items.get(0))
    }

}
