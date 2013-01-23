package org.osehra.cpe.vpr.sync.vista.json

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.vpr.Result
import org.osehra.cpe.vpr.sync.vista.MockVistaDataChunks
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk
import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.CoreMatchers.nullValue

class ResultImporterTest extends AbstractImporterTest {

    ResultImporter ri = new ResultImporter()

    @Test
    void testImportGlucose() {
        VistaDataChunk chunk = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("lab-glucose.json"), mockPatient, 'lab')

        Result glucose = ri.convert(chunk)

        assertThat(glucose.categoryCode, equalTo("urn:va:lab-category:CH"))
        assertThat(glucose.categoryName, equalTo("Laboratory"))
        assertThat(glucose.displayName, equalTo("GLUCOSE"))
        assertThat(glucose.facilityCode, equalTo("500D"))
        assertThat(glucose.facilityName, equalTo("SLC-FO HMP DEV"))
        assertThat(glucose.groupName, equalTo("CH 0721 6"))
        assertThat(glucose.groupUid, equalTo("urn:va:F484:229:accession:CH;6889297.92"))
        assertThat(glucose.high, equalTo("110"))
        assertThat(glucose.interpretationCode, equalTo("urn:hl7:observation-interpretation:H"))
        assertThat(glucose.interpretationName, equalTo("High"))
        assertThat(glucose.localId, equalTo("CH;6889297.92;2"))
        assertThat(glucose.low, equalTo("60"))
        assertThat(glucose.observed, equalTo(new PointInTime(2011, 7, 1, 8, 0)))
        assertThat(glucose.result, equalTo("120"))
        assertThat(glucose.resulted, equalTo(new PointInTime(2011, 7, 21, 6, 45)))
        assertThat(glucose.specimen, equalTo("SERUM"))
//        assertThat(glucose.statusCode, equalTo("urn:va:lab-status:completed"))
//        assertThat(glucose.statusName, equalTo("completed"))
        assertThat(glucose.summary, equalTo("GLUCOSE (SERUM) 120<em>H</em> mg/dL"))
        assertThat(glucose.typeCode, equalTo("urn:lnc:2345-7"))
        assertThat(glucose.typeName, equalTo("GLUCOSE"))
        assertThat(glucose.uid, equalTo("urn:va:F484:229:lab:CH;6889297.92;2"))
        assertThat(glucose.units, equalTo("mg/dL"))
//        assertThat(glucose.vuid, equalTo("urn:vuid:4665460"))
    }

    @Test
    void testImportMalariaSmear() {
        VistaDataChunk chunk = MockVistaDataChunks.createFromJson(getClass().getResourceAsStream("lab-malaria-smear.json"), mockPatient, 'lab')

        Result malariaSmear = ri.convert(chunk)

        assertThat(malariaSmear.categoryCode, equalTo("urn:va:lab-category:CH"))
        assertThat(malariaSmear.categoryName, equalTo("Laboratory"))
        assertThat(malariaSmear.comment, equalTo("TESTING THE EPI PATCH MALARIA SMEAR reported incorrectly as POSITIVE FOR MALARIA "))
        assertThat(malariaSmear.displayName, equalTo("MALARIA"))
        assertThat(malariaSmear.facilityCode, equalTo("500"))
        assertThat(malariaSmear.facilityName, equalTo("CAMP MASTER"))
        assertThat(malariaSmear.groupName, equalTo("HE 0520 1"))
        assertThat(malariaSmear.groupUid, equalTo("urn:va:F484:229:accession:CH;7029478.858493"))
        assertThat(malariaSmear.high, nullValue())
        assertThat(malariaSmear.interpretationCode, nullValue())
        assertThat(malariaSmear.interpretationName, nullValue())
        assertThat(malariaSmear.localId, equalTo("CH;7029478.858493;488"))
        assertThat(malariaSmear.low, nullValue())
        assertThat(malariaSmear.observed, equalTo(new PointInTime(1997, 5, 20, 14, 15)))
        assertThat(malariaSmear.result, equalTo("POSITIVE"))
        assertThat(malariaSmear.resulted, equalTo(new PointInTime(1997, 5, 21, 8, 16)))
//        assertThat(malariaSmear.sample, equalTo("BLOOD"))
        assertThat(malariaSmear.specimen, equalTo("BLOOD"))

//        assertThat(malariaSmear.statusCode, equalTo("urn:va:lab-status:completed"))
//        assertThat(malariaSmear.statusName, equalTo("completed"))
        assertThat(malariaSmear.summary, equalTo("MALARIA SMEAR (BLOOD) POSITIVE"))
        assertThat(malariaSmear.typeCode, equalTo("urn:va:ien:60:503:70"))
        assertThat(malariaSmear.typeName, equalTo("MALARIA SMEAR"))
        assertThat(malariaSmear.uid, equalTo("urn:va:F484:229:lab:CH;7029478.858493;488"))
        assertThat(malariaSmear.units, nullValue())
//        assertThat(malariaSmear.vuid, equalTo("urn:vuid:4665460"))
    }
}
