package org.osehra.cpe.vpr;

import java.util.Date;
import java.util.HashSet;

import org.osehra.cpe.datetime.PointInTime;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;
import org.junit.Before;

public class ResultTests  {

    private ResultOrganizer ro;
    private Result r;
    private Patient pt;
    private PatientFacility facility;
    
	@Before
    public void setUp() {
        pt = new Patient();
        pt.setData("pid","1");
        pt.setData("icn","12345");
        pt.setLastUpdated(PointInTime.now());
        
        facility = new PatientFacility(); 
        facility.setData("code","500");
        facility.setData("name","CAMP MASTER");
        facility.setData("homeSite",true);
        facility.setData("localPatientId","229");
        
        ro = new ResultOrganizer();
        ro.setData("pid", pt.getPid());
		ro.setData("facilityCode",facility.getCode());
        ro.setData("facilityName",facility.getName());
		ro.setData("localId","CH;6959389.875453");
		ro.setData("observed",new PointInTime(1975, 7, 23, 10, 58));
		ro.setData("specimen","BLOOD");
		ro.setData("organizerType","accession");
				
        Result result = new Result();
		result.setData("localId","CH;6959389.875453;2");
		result.setData("typeName","SODIUM");
		result.setData("result","140");
		result.setData("units","meq/L");
		ro.addToResults(result);
		
		r = ro.getResults().get(0);

        r.addToOrganizers(ro);
    }

	@Test
    public void testAccessionProperties() {
        assertEquals(pt.getPid(), r.getPid());
        assertEquals(facility.getCode(), r.getFacilityCode());
        assertEquals(facility.getName(), r.getFacilityName());
        assertEquals(new PointInTime(1975, 7, 23, 10, 58), r.getObserved());
        assertNull(r.getResulted());
        assertNull(r.getResultStatusName());
        assertEquals("BLOOD", r.getSpecimen());
        assertNull(r.getCategoryCode());
        assertNull(r.getCategoryName());
    }

    @Test
    public void testSummaryWithoutInterpretation() {
        assertEquals("SODIUM (BLOOD) 140 meq/L", r.getSummary());
    }

    @Test
    public void testSummaryWithInterpretation() {
        Result r2 = new Result();
        r2.setData("typeName","HEMOGLOBIN A1C");
        r2.setData("result","6.2");
        r2.setData("units","%");
        r2.setData("specimen","SERUM");
        r2.setData("interpretationCode","urn:hl7:observation-interpretation:H");
        r2.setData("interpretationName","High");
        ResultOrganizer ro = new ResultOrganizer();
        ro.setData("specimen","SERUM");
        ro.setData("organizerType","accession");
        r2.addToOrganizers(ro);
        
        assertEquals("HEMOGLOBIN A1C (SERUM) 6.2<em>H</em> %", r2.getSummary());
    }

    @Test
    public void testSummaryWithoutUnits() {
        Result r2 = new Result();//(typeName: "MALARIA SMEAR", result: "POSITIVE")
        r2.setData("typeName","MALARIA SMEAR");
        r2.setData("result","POSITIVE");
        assertEquals("MALARIA SMEAR POSITIVE", r2.getSummary());
    }

    @Test
    public void testQualifiedName() {
        assertEquals("SODIUM (BLOOD)", r.getQualifiedName());
    }

    @Test
    public void testLaboratoryKind() {
        r = new Result();
        //(typeName: "HEMOGLOBIN A1C", organizers: [new ResultOrganizer(category: new ResultCategory(code: "CH", name: "Laboratory"), 
        //specimen: "SERUM", organizerType: "accession")]);
        r.setData("typeName","HEMOGLOBIN A1C");
        r.setData("specimen","SERUM");

        ResultOrganizer ro = new ResultOrganizer();
        ro.setData("categoryCode","CH");
        ro.setData("categoryName","Laboratory");
        ro.setData("organizerType","accession");
        r.addToOrganizers(ro);
        
        assertEquals("Laboratory", r.getKind());
    }

    @Test
    public void testMicrobiologyKind() {
        r = new Result(); //typeName: "Bacteriology Remark(s)", result: "NO GROWTH AFTER 2 DAYS", organizers: 
        //[new ResultOrganizer(category: new ResultCategory(code: "MI", name: "Microbiology"), organizerType: "accession")]);
        r.setData("typeName","Bacteriology Remark(s)");
        r.setData("result","NO GROWTH AFTER 2 DAYS");
        ResultOrganizer ro = new ResultOrganizer();
        ro.setData("categoryCode","MI");
        ro.setData("categoryName","Microbiology");
        ro.setData("organizerType","accession");
        r.addToOrganizers(ro);
        assertEquals("Microbiology", r.getKind());
    }
}
