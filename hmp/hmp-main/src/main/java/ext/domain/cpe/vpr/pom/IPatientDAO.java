package org.osehra.cpe.vpr.pom;

import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.PatientFacility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPatientDAO extends IPatientObjectDAO<Patient> {

    Patient findByIcn(String icn);

    /**
     * Attempts to find a patient from any patient identifier, or <code>pid</code> used in VistA/VPR.
     * <p/>
     * Accepts patient identifiers in 3 forms
     * <ol>
     *     <li><code>{facilityCode;localPatientId}</code> - In VistA this is <code>{stationNumber;dfn}</code></li>
     *     <li><code>icn</code> - if there is no semicolon, interpret pid as an ICN</li>
     *     <li><code>vprId</code> - if there is no patient with the given ICN, interpret pid as an internal VPR patient id</li>
     * </ol>
     *
     * @param pid One of three types of patient identifier
     * @return corresponding Patient object, if found, null otherwise.
     */
    Patient findByAnyPid(String pid);
    
    Patient findByVprPid(String pid);

    /* Simplifiying....
    Patient findBySystemIdAndLocalPatientId(String systemId, String localPatientId);
    PatientFacility findFacilityByCodeAndLocalPatientId(String facilityCode, String localPatientId);
    PatientFacility findFacilityBySystemIdAndLocalPatientId(String systemId, String localPatientId);
	*/
    Patient findByLocalID(String systemOrCode, String dfn);

    Page<Patient> findAll(Pageable pageable);

    List<String> listPatientIds();

    // TODO: uncomment and implement a pageable version of all patient ids
//    Page<String> listPatientIds(Pageable pageable);

    int count();

    Patient save(Patient pat);
}
