package org.osehra.cpe.vpr.sync.msg

import org.osehra.cpe.vpr.dao.ISyncErrorDao
import org.apache.solr.client.solrj.SolrServerException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.osehra.cpe.vpr.*
import static org.osehra.cpe.vpr.sync.SyncMessageConstants.PATIENT_ID
import org.springframework.transaction.annotation.Transactional
import org.osehra.cpe.vpr.dao.ISolrDao
import org.osehra.cpe.vpr.pom.IPatientDAO

import org.osehra.cpe.vpr.sync.vista.IVistaPatientDataService

import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.osehra.cpe.HmpProperties
import org.springframework.util.StringUtils

@Service
@Transactional
class ClearPatientMessageHandler implements IMapMessageHandler, EnvironmentAware {

    private static Logger log = LoggerFactory.getLogger(ClearPatientMessageHandler.class);

    Environment environment

    @Autowired
    IPatientDAO patientDao

    @Autowired
    ISyncErrorDao syncErrorDao

    @Autowired
    ISolrDao solrDao

    @Autowired
    IVistaPatientDataService vistaPatientDataService

    void onMessage(Map msg) {
        String pid = msg[PATIENT_ID]?.toString()
        assert pid

        log.debug("Getting patient for PID " + pid + " via class " + patientDao.getClass().getName())
        Patient pt = patientDao.findByVprPid(pid)
        if (pt == null) return

        log.debug("Clearing patient " + pt.familyName + " with UID " + pt.uid + " AND pid " + pt.pid);

        deletePatient(pt);
        unsubscribePatient(pt);
    }

    private void deletePatient(Patient pt) {
        patientDao.deleteByPID(pt.getPid())

        syncErrorDao.deleteByPatientId(pt.getPid());

        try {
            solrDao.deleteByQuery("pid:${pt.getPid()}")
            solrDao.commit()
        } catch (SolrServerException e) {
            log.error("unable to clear ${pt} from solr")
        }
    }

    private void unsubscribePatient(Patient pt) {
        String serverId = environment.getProperty(HmpProperties.SERVER_ID)

        for (PatientFacility f : pt.getFacilities()) {
            if (StringUtils.hasText(f.systemId)) {
                String localPatientId = f.getLocalPatientId();
                if (!StringUtils.hasText(localPatientId)) {
                    continue;
                }
                try {
                    vistaPatientDataService.unsubscribePatient(f.systemId, localPatientId, serverId);
                    log.debug("unsubscribed from VPR updates when localPatientId:${localPatientId} from systemId:${f.systemId}")
                } catch (Throwable t) {
                    log.warn("exception during unsubscribe localPatientId:${localPatientId} from systemId:${f.systemId}", t)
                }
            }
        }
    }
}
