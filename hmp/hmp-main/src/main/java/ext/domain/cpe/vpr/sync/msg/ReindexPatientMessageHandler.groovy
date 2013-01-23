package org.osehra.cpe.vpr.sync.msg

import org.osehra.cpe.vpr.dao.ISolrDao
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO
import org.osehra.cpe.vpr.sync.SyncMessageConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.osehra.cpe.vpr.*

@Service
@Transactional
class ReindexPatientMessageHandler implements IMapMessageHandler {

    private static Logger LOG = LoggerFactory.getLogger(ReindexPatientMessageHandler)

    @Autowired
    ISolrDao solrService

    @Autowired
    IGenericPatientObjectDAO genericPatientRelatedDao

    void onMessage(Map msg) {
        String pid = msg[SyncMessageConstants.PATIENT_ID]
        assert pid

        LOG.debug("Reindexing ${pid}")

        [Allergy, Document, Encounter, HealthFactor, Immunization, Medication, Order, Problem, Procedure, Result, VitalSign].each { Class domainClass ->
            def items = genericPatientRelatedDao.findAllByPID(domainClass, pid, null )// Pageable is ignored for now, JDS need to implement pagination 
            items.each { item ->
                try {
                    solrService.index(item, false)
                } catch (Throwable t) {
                    LOG.error("unable to reindex item ${item}", t)
                }
            }
        }
        solrService.commit()
    }
}

