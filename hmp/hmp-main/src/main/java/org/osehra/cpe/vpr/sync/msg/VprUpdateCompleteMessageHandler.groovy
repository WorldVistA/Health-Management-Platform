package org.osehra.cpe.vpr.sync.msg

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.hub.dao.IVistaAccountDao
import org.osehra.cpe.vpr.Patient

import org.osehra.cpe.vpr.pom.IGenericPOMObjectDAO
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.pom.POMUtils
import org.osehra.cpe.vpr.sync.SyncMessageConstants
import org.osehra.cpe.vpr.sync.SyncService
import org.osehra.cpe.vpr.sync.vista.VprUpdate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import org.slf4j.LoggerFactory
import org.slf4j.Logger

import org.osehra.cpe.vpr.dao.ISolrDao
import org.osehra.cpe.vpr.sync.ISyncService
import org.osehra.cpe.vpr.dao.IVprUpdateDao

@Service
@Transactional
class VprUpdateCompleteMessageHandler implements IMapMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(VprUpdateCompleteMessageHandler.class)

    @Autowired
    ISolrDao solrService

    @Autowired
    ISyncService syncService

    @Autowired
    IPatientDAO patientDao

    @Autowired
    IVistaAccountDao vistaAccountDao

    @Autowired
    IVprUpdateDao lastUpdateDao

    void onMessage(Map msg) {
        if (StringUtils.hasText(msg[SyncMessageConstants.PATIENT_DOMAINS_BY_PID])) {
            Map<String, List<String>> domainsByPid = POMUtils.parseJSONtoMap(msg[SyncMessageConstants.PATIENT_DOMAINS_BY_PID])
            Set<String> pids = StringUtils.commaDelimitedListToSet(msg[SyncMessageConstants.PATIENT_IDS])
            for (String pid : pids) {
                try {
                    updatePatientLastUpdated(pid, domainsByPid.get(pid))
                } catch (Throwable t) {
                    syncService.errorDuringMsg(msg, t);
                }
            }
        }
        String vistaLastUpdateTimestamp = msg[SyncMessageConstants.VISTA_LAST_UPDATED]
        String vistaId = msg[SyncMessageConstants.VISTA_ID]

        LOG.debug("updatecomplete VistA {} at {}", vistaId, vistaLastUpdateTimestamp)
        lastUpdateDao.save(new VprUpdate(vistaId, vistaLastUpdateTimestamp));

        solrService.commit()

        // TODO: record timing info
    }

    private void updatePatientLastUpdated(String pid, List<String> domains) {
        String updatedDomains = ''

        Patient pt = patientDao.findByAnyPid(pid);
        pt.lastUpdated = PointInTime.now();

        for (String domain : domains) {
            String domainName
            int strLength = domain.length();
            if (domain == 'pharmacy') {
                domainName = "meds"
            } else if ('y' == domain.charAt(strLength - 1)) {
                domainName = domain.substring(0, domain.length() - 1) + 'ies'
            } else domainName = domain + 's'
            if (updatedDomains == '') {
                updatedDomains = domainName
            } else if (updatedDomains.count(domainName.toString()) < 1) {
                updatedDomains = updatedDomains + ', ' + domainName
            }
        }
        pt.domainUpdated = updatedDomains;
        patientDao.save(pt)
    }
}
