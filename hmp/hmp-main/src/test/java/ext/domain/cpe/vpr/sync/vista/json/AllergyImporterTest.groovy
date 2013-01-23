package org.osehra.cpe.vpr.sync.vista.json

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vpr.Allergy
import org.osehra.cpe.vpr.AllergyProduct
import org.osehra.cpe.vpr.AllergyReaction
import org.osehra.cpe.vpr.UidUtils
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks
import org.junit.Before
import org.junit.Test

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk

import org.junit.Ignore

class AllergyImporterTest extends AbstractImporterTest {

    AllergyImporter importer

    @Before
    void setUp() {
        super.setUp();
        importer = new AllergyImporter()
    }

    @Test
    void testImport() {
        VistaDataChunk chunk = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("allergy.json"), mockPatient, "allergy")

        Allergy a = importer.convert(chunk)

        assertThat(a.getPid(), is(equalTo(MOCK_PID)));

        assertThat(a.facilityCode, is(equalTo("500")))
        assertThat(a.facilityName, is(equalTo("CAMP MASTER")))

        assertThat(a.uid, is(equalTo(UidUtils.getAllergyUid(MockVistaDataChunks.VISTA_ID, "100846", "982"))))
        assertThat(a.localId, is(equalTo("982")))
        assertThat(a.adverseEventTypeName, is(equalTo("DRUG OTHER")))
//        assertThat(a.adverseEventTypeCode, is(equalTo("urn:sct:419511003")))
        assertThat(a.entered, is(new PointInTime(2011, 11, 22, 13, 43)))
        assertThat(a.verified, is(new PointInTime(2011, 11, 22, 13, 43, 43)))
        assertThat(a.severityName, is(nullValue()))
        assertThat(a.severityCode, is(nullValue()))
        assertThat(a.historical, is(true))
        assertThat(a.reference, is(equalTo("219;PSNDF(50.6,")))

        assertThat(a.products.size(), equalTo(1));
        AllergyProduct p = a.products.toList()[0] as AllergyProduct
        assertThat(p.name, is(equalTo("DIPHENHYDRAMINE")))
        assertThat(p.vuid, is(equalTo("urn:va:vuid:4019724")))
        assertThat(p.code, is(nullValue()))

        assertEquals 1, a.reactions.size()
        AllergyReaction r = a.reactions.toList()[0] as AllergyReaction
        assertEquals "ANXIETY", r.name
        assertThat(r.vuid, is("urn:va:vuid:4637050"))
        assertThat(r.code, is(nullValue()))
    }

    @Ignore
    @Test
    void testImportComments() {
        VistaDataChunk chunk = MockVistaDataChunks.createFromJson('''
{

}
''', "allergy")

        Allergy a = importer.convert(chunk)
        assertThat(a.comments.size(), is(equalTo(2)))
    }
}
