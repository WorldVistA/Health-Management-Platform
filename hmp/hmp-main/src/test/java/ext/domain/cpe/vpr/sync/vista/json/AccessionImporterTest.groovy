package org.osehra.cpe.vpr.sync.vista.json

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vpr.Result
import org.osehra.cpe.vpr.ResultOrganizer
import org.osehra.cpe.vpr.UidUtils
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.assertThat

class AccessionImporterTest extends AbstractImporterTest {

    @Test
    void testImportChem7() {
        VistaDataChunk chunk = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("accession.json"), mockPatient, "accession")

        AccessionImporter importer = new AccessionImporter();

        ResultOrganizer o = importer.convert(chunk)

        assertThat(o.getPid(), is(equalTo(MOCK_PID)));
        assertThat(o.getFacilityCode(), is(equalTo("500")));
        assertThat(o.getFacilityName(), is(equalTo("CAMP MASTER")));
        assertThat(o.getUid(), is(equalTo(UidUtils.getResultOrganizerUid(MockVistaDataChunks.VISTA_ID, "229", "CH;6889293.857945"))));
        assertThat(o.getLocalId(), is("CH;6889293.857945"));
        assertThat(o.getStatusCode(), is("urn:va:lab-status:completed"));
        assertThat(o.getStatusName(), is("Completed"));
        assertThat(o.getCategoryCode(), is("urn:va:lab-category:CH"));
        assertThat(o.getCategoryName(), is(equalTo("Laboratory")));

        assertThat(o.getResulted(), is(new PointInTime(2011, 7, 5, 14, 24, 48)));
        assertThat(o.getObserved(), is(new PointInTime(2011, 7, 5, 14, 20, 55)));
        assertThat(o.getSpecimen(), is("SERUM"));
        assertThat(o.getOrganizerType(), is("accession"));
        assertThat(o.getResults().size(), is(7));

        Result r = o.getResults().get(0);
        assertThat(r.getPid(), is(equalTo(MOCK_PID)))

        assertThat(r.getFacilityCode(), is(equalTo("500")));
        assertThat(r.getFacilityName(), is(equalTo("CAMP MASTER")));
        assertThat(r.getResultStatusCode(), is("urn:va:lab-status:completed"));
        assertThat(r.getResultStatusName(), is("Completed"));
        assertThat(r.getCategoryCode(), is("urn:va:lab-category:CH"));
        assertThat(r.getCategoryName(), is(equalTo("Laboratory")));

        assertThat(r.getResulted(), is(new PointInTime(2011, 7, 5, 14, 24, 48)));
        assertThat(r.getObserved(), is(new PointInTime(2011, 7, 5, 14, 20, 55)));
        assertThat(r.getSpecimen(), is("SERUM"));

        assertThat(r.getUid(), is(UidUtils.getResultUid(MockVistaDataChunks.VISTA_ID, "229", "CH;6889293.857945;2")));
        assertThat(r.getLocalId(), is("CH;6889293.857945;2"));
        assertThat(r.getResult(), is("75"));
        assertThat(r.getTypeName(), is("GLUCOSE"));
        assertThat(r.getDisplayName(), is("GLUCOSE"));
        assertThat(r.getTypeCode(), is("urn:lnc:2345-7"));
        assertThat(r.getHigh(), is("110"));
        assertThat(r.getLow(), is("60"));
        assertThat(r.getUnits(), is("mg/dL"));
        assertThat(r.getInterpretationCode(), nullValue());
    }
}
