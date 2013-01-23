package org.osehra.cpe.vpr.sync.vista;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.PatientFacility;

/**
 * TestCase that sets up a mock Patient and facility for use in tests
 */
public class MockPatientUtils {

    public static Patient create() {
        return create("1");
    }

    public static Patient create(String pid) {
        return create(pid, MockVistaDataChunks.ICN, MockVistaDataChunks.VISTA_ID, MockVistaDataChunks.DFN);
    }

    public static Patient create(String pid, String icn, String systemId, String localPatientId) {
        Patient pt = new Patient();
        pt.setData("pid", pid);
        pt.setData("icn", icn);
        pt.setLastUpdated(PointInTime.now());

        PatientFacility facility = new PatientFacility();
        facility.setData("code", MockVistaDataChunks.DIVISION);
        facility.setData("name", "CAMP MASTER");
        facility.setData("homeSite", true);
        facility.setData("localPatientId", localPatientId);
        facility.setData("systemId", systemId);

        pt.addToFacilities(facility);
        return pt;
    }
}
