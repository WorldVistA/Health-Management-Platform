package org.osehra.cpe.vpr.sync.msg

import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.PatientFacility

import org.osehra.cpe.vpr.sync.SyncMessageConstants

import org.osehra.cpe.vpr.sync.UnreachableFacilityException

import org.osehra.cpe.vpr.sync.vista.VistaDataChunk

import org.perf4j.StopWatch
import org.perf4j.slf4j.Slf4JStopWatch
import org.springframework.beans.factory.annotation.Autowired

import static org.osehra.cpe.vpr.sync.SyncMessageConstants.PATIENT_ID
import static org.osehra.cpe.vpr.sync.SyncMessageConstants.VISTA_ID

import static org.osehra.cpe.vpr.sync.SyncMessageConstants.PATIENT_DFN
import org.springframework.core.env.Environment
import org.springframework.context.EnvironmentAware
import org.osehra.cpe.HmpProperties

import org.springframework.dao.InvalidDataAccessResourceUsageException

import org.osehra.cpe.vpr.sync.vista.IVistaPatientDataService
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.convert.converter.Converter
import org.osehra.cpe.vpr.sync.ISyncService
import org.osehra.cpe.vpr.pom.IPatientDAO

// TODO: consider removing reference to jmsTemplate and move all msg generation into SyncService
class LoadPatientMessageHandler implements IMapMessageHandler, EnvironmentAware {

    @Autowired
    IPatientDAO patientDao

    @Autowired
    IVistaPatientDataService vistaPatientDataService

    @Autowired
    ISyncService syncService

    private Converter<VistaDataChunk, Object> centralImporter

    @Required
    void setCentralImporter(Converter<VistaDataChunk, Object> centralImporter) {
        this.centralImporter = centralImporter
    }

    Environment environment

    static Set<Map<String, Object>> loadConfig = [
//            [extract: "accession"],
            [extract: "allergy"],
            [extract: "appointment"],
            [extract: "document", includeBody: true, category: "ALL"],
            [extract: "factor", includeBody: true],
            [extract: "immunization"],
            [extract: "lab"],
            [extract: "pharmacy"],
            [extract: "observation"],
            [extract: "order"],
            [extract: "problem"],
            [extract: "consult", includeBody: true],
            [extract: "radiology", includeBody: true],
            [extract: "surgery", includeBody: true],
            [extract: "task"],
            [extract: "visit"],
            [extract: "vital"],
    ];

    void onMessage(Map msg) {
        assert msg[VISTA_ID]
        assert msg[PATIENT_DFN] || msg[SyncMessageConstants.PATIENT_ICN]
        StopWatch timer = new Slf4JStopWatch();

        VistaDataChunk patientChunk
        if (msg[PATIENT_DFN]) {
            patientChunk = vistaPatientDataService.fetchPatientDemographicsWithDfn(msg[VISTA_ID], msg[PATIENT_DFN])
        } else {
            patientChunk = vistaPatientDataService.fetchPatientDemographicsWithIcn(msg[VISTA_ID], msg[SyncMessageConstants.PATIENT_ICN])
        }

        Patient pt = centralImporter.convert(patientChunk)
        if (!pt) throw new InvalidDataAccessResourceUsageException("Unable to convert patientChunk into Patient object: {}", patientChunk)

        // assigns a new pid when demographics are saved
        pt = patientDao.save(pt);
        msg[PATIENT_ID] = pt.getPid()

        // determine list of VistA systems to fetch data from
        Set<String> vistaIds = getSystemIdsOfAllPatientFacilities(patientChunk.systemId, pt, msg)
        for (String vistaId: vistaIds) {
            try {
                // if this is the initial VistA system
                if (vistaId.equals(msg[VISTA_ID])) {
                    loadPatientFromVistaId(msg, vistaId, pt, patientChunk.json.path('localId').textValue());
                } else {
                    // if not, we need to grab the localPatientId in this system
                    VistaDataChunk f = vistaPatientDataService.fetchPatientDemographicsWithIcn(vistaId, pt.icn)
                    String localPatientId = f.json.path('localId').textValue();

                    loadPatientFromVistaId(msg, vistaId, pt, localPatientId);
                }
                msg[VISTA_ID] = vistaId;
                subscribePatient(msg);
            } catch (Throwable t) {
                syncService.errorDuringMsg(msg, t)
            }
        }

        syncService.sendLoadPatientCompleteMsg(pt, msg);

        timer.stop("loadComplete Queued pt: " + pt.pid);
    }

    private Set<String> getSystemIdsOfAllPatientFacilities(String systemId, Patient pt, Map loadMsg) {
        Map msg
        Set<String> vistaIds = new HashSet<String>();
        vistaIds.add(systemId); // seed list with current vistaId
        if (pt.icn) {
            loadMsg[SyncMessageConstants.PATIENT_ICN] = pt.icn
            loadMsg.remove(PATIENT_DFN)

            List unreachableFacilities = []
            pt.facilities.each { PatientFacility f ->
                if (f.systemId)
                    vistaIds << f.systemId
                else
                    unreachableFacilities << f
            }
            unreachableFacilities.each { PatientFacility f ->
                syncService.errorDuringMsg(loadMsg, new UnreachableFacilityException(pt, f.code, f.name, systemId))
            }
        }
        vistaIds
    }

    private void loadPatientFromVistaId(Map msg, String vistaId, Patient pt, String localPatientId) {
        // generate import messages for each item
        for (Map<String, Object> load : loadConfig) {
            try {
                List<VistaDataChunk> items = vistaPatientDataService.fetchDomainChunks(vistaId, pt, load.extract, load.includeBody ?: false, load.category)

                for (VistaDataChunk item : items) {
                    try {
                        syncService.sendImportPatientDataExtractItemMsg(item)
                    } catch (Throwable t) {
                        Map loadMessage = msg.clone();
                        loadMessage[SyncMessageConstants.VPR_DOMAIN] = load.extract
                        syncService.errorDuringMsg(loadMessage, t)
                    }
                }
            } catch (Throwable t) {
                syncService.errorDuringMsg(msg, t)
            }
        }
    }

    private void subscribePatient(Map msg) {
        String vistaId = msg[VISTA_ID]
        Patient pt = patientDao.findByAnyPid(msg[PATIENT_ID])
        String pid = pt.icn ? ';' + pt.icn : (msg[PATIENT_DFN] ?: getDfnForVistaId(pt, msg[VISTA_ID]))

        vistaPatientDataService.subscribePatient(vistaId, pid, environment.getProperty(HmpProperties.SERVER_ID))
    }

    private String getDfnForVistaId(Patient pt, String vistaId) {
        PatientFacility facility = pt.facilities.find { it.systemId == vistaId }
        return facility?.localPatientId
    }
}
