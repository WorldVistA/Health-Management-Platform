package EXT.DOMAIN.cpe.vpr.dao.solr

import org.junit.Test
import org.junit.Before
import EXT.DOMAIN.cpe.vpr.Document
import org.apache.solr.common.SolrInputDocument
import static org.hamcrest.core.IsEqual.equalTo
import static org.junit.Assert.assertThat

import EXT.DOMAIN.cpe.vpr.Clinician
import EXT.DOMAIN.cpe.vpr.Encounter
import EXT.DOMAIN.cpe.vpr.EncounterProvider
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.PatientFacility
//import EXT.DOMAIN.cpe.codes.DocumentType
import EXT.DOMAIN.cpe.vpr.Order
import EXT.DOMAIN.cpe.datetime.PointInTime

// TODO: implement these mappings using Jackson views
class SolrDomainMappingsTests {

    private DomainObjectToSolrInputDocument converter;

    private Patient mockPatient = new Patient([pid: "1234"])

    @Before
    public void setUp() throws Exception {
        converter = new DomainObjectToSolrInputDocument(SolrDomainMappings.MAPPINGS);
    }

    @Test
    public void testDocumentMapping() {
        Document d = new Document([uid: "urn:va:foo:bar:baz", pid: "1234", facilityCode: "500", facilityName: "CAMP MASTER"])
//        d.documentType = new DocumentType(code: "PN", name: "Progress Note");
		d.documentTypeCode = "PN"
		d.documentTypeName ="ProgressNote"
        d.localTitle = "FEIFEIFEFE"
        d.content = '''
FOO BAR
BAZ
SPAZ
'''

        SolrInputDocument solrDoc = converter.convert(d)

        assertThat(solrDoc.getFieldValue("domain"), equalTo("document"))
        assertThat(solrDoc.getFieldValue("pid"), equalTo(d.pid))
        assertThat(solrDoc.getFieldValue("uid"), equalTo(d.uid))
        assertThat(solrDoc.getFieldValue("facility"), equalTo(d.facilityName))
        assertThat(solrDoc.getFieldValue("document_type"), equalTo(d.documentTypeName))
        assertThat(solrDoc.getFieldValue("local_title"), equalTo(d.localTitle))
        assertThat(solrDoc.getFieldValue("body"), equalTo(d.text))
    }

    @Test
    public void testOrderMapping() {
        Order o = new Order(uid: "urn:va:foo:bar:baz", pid: "1234", facilityCode: "500", facilityName: "CAMP MASTER")
        o.name = "FOOBAR"
        o.start = new PointInTime(2011, 6, 10)
        o.displayGroup = "FOO"
        o.statusName = "COMPLETE"

        SolrInputDocument solrDoc = converter.convert(o)

        assertThat(solrDoc.getFieldValue("domain"), equalTo("order"))
        assertThat(solrDoc.getFieldValue("pid"), equalTo(o.pid))
        assertThat(solrDoc.getFieldValue("uid"), equalTo(o.uid))
        assertThat(solrDoc.getFieldValue("facility"), equalTo(o.facilityName))
        assertThat(solrDoc.getFieldValue("order_name"), equalTo(o.name))
        assertThat(solrDoc.getFieldValue("order_start_date_time"), equalTo(o.start))
        assertThat(solrDoc.getFieldValue("order_group_va"), equalTo(o.displayGroup))
        assertThat(solrDoc.getFieldValue("order_status_va"), equalTo("COMPLETE"))
    }

	@Test
	public void testEncounterMapping() {
		Encounter e = new Encounter()
        e.uid = "urn:va:foo:bar:baz"
        e.pid = mockPatient.pid
        e.facilityCode = "500"
        e.facilityName = "CAMP MASTER"
		e.typeName = "FOO"
		e.patientClassName = "PA"
		e.categoryName = "CA"
		e.dispositionName = "DA"
		e.sourceName = "SN" 
		e.referrerName = "Clinician Name"
		e.dateTime = new PointInTime(2011, 6, 10)
		e.addToProviders(new EncounterProvider([providerName:"Bob", primary: true]))
		
		SolrInputDocument solrDoc = converter.convert(e)

		assertThat(solrDoc.getFieldValue("domain"), equalTo("encounter"))
		assertThat(solrDoc.getFieldValue("pid"), equalTo(e.pid))
		assertThat(solrDoc.getFieldValue("uid"), equalTo(e.uid))
		assertThat(solrDoc.getFieldValue("facility"), equalTo(e.facilityName))
		assertThat(solrDoc.getFieldValue("encounter_type"), equalTo(e.typeName))
		assertThat(solrDoc.getFieldValue("patientClass"), equalTo(e.patientClassName))
		assertThat(solrDoc.getFieldValue("encounter_category"), equalTo(e.categoryName))
		assertThat(solrDoc.getFieldValue("discharge_disposition"), equalTo(e.dispositionName))
		assertThat(solrDoc.getFieldValue("admission_source"), equalTo(e.sourceName))
		assertThat(solrDoc.getFieldValue("referrer"), equalTo("Clinician Name"))
		assertThat(solrDoc.getFieldValue("visit_date_time"), equalTo(e.dateTime))
		//TODO: Fix the mapping
		//assertThat(solrDoc.getFieldValue("primaryProvider"), equalTo("Bob"))
	}

}
