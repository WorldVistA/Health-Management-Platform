package org.osehra.cpe.vpr.sync.vista.json

import org.junit.Test
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks

import org.osehra.cpe.vpr.Immunization

import org.osehra.cpe.vpr.UidUtils

import org.osehra.cpe.datetime.PointInTime

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*

class ImmunizationImporterTest extends AbstractImporterTest {

    @Test
    void testConvert() {
        ImmunizationImporter importer = new ImmunizationImporter()

        Immunization a = importer.convert(MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("immunization.json"), mockPatient, "immunization"))

        assertThat(a.getPid(), is(equalTo(MOCK_PID)))
        assertThat(a.uid, is(equalTo(UidUtils.getImmunizationUid(MockVistaDataChunks.VISTA_ID, "229", "44"))))
        assertThat(a.localId, is(equalTo("44")))
		assertThat(a.administeredDateTime, is(new PointInTime(2000,04,04,10,55,06)))
		assertThat(a.comments, is(equalTo("")))
		assertThat(a.contraindicated, is(false))
		assertThat(a.cptName, is(equalTo("CHOLERA VACCINE, ORAL")))
		assertThat(a.cptCode, is(equalTo("???")))
		assertThat(a.encounterUid, is(equalTo(UidUtils.getVisitUid(MockVistaDataChunks.VISTA_ID, "229", "1975"))))
		assertThat(a.facilityName, is(equalTo("FT. LOGAN")))
		assertThat(a.facilityCode, is(equalTo("???")))
		assertThat(a.location, is(equalTo("AUDIOLOGY")))
		/*
		 * TODO: Find data for:
		 * - Reaction
		 * - Series
		 */
        assertThat(a.name, is(equalTo("PNEUMOCOCCAL")))
        assertThat(a.performerUid, is(equalTo(UidUtils.getUserUid(MockVistaDataChunks.VISTA_ID, "11278"))))
		assertThat(a.summary, is(equalTo("CHOLERA VACCINE, ORAL")))

    }
}
