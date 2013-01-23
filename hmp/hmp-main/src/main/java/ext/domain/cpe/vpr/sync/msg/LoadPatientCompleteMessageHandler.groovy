package org.osehra.cpe.vpr.sync.msg

import org.osehra.cpe.vpr.EventController
import org.osehra.cpe.vpr.Patient;


import org.osehra.cpe.vpr.sync.ISyncService
import org.osehra.cpe.vpr.sync.SyncMessageConstants;

import org.osehra.cpe.vpr.dao.solr.DefaultSolrDao;
import org.perf4j.StopWatch;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.datetime.PointInTime;


@Service
class LoadPatientCompleteMessageHandler implements IMapMessageHandler {

    @Autowired
    DefaultSolrDao solrService

    @Autowired
    IPatientDAO patientDao
	
	@Autowired
	EventController eventController
	
	@Autowired
	ISyncService syncService

    void onMessage(Map msg) {
        assert msg[SyncMessageConstants.PATIENT_ID]

		solrService.commit()

        // set lastUpdated to now
        Patient pt = patientDao.findByVprPid(msg.get(SyncMessageConstants.PATIENT_ID))
        pt.lastUpdated = PointInTime.now()
        patientDao.save(pt)
		
		syncService.clearChunkProcessingForPatientId(pt.getPid());
		eventController.broadcastMessage(['syncComplete':['pid':pt.getPid(),'icn':pt.getIcn()]]);

        // measure how long it took to load one patient
        long start = msg[SyncMessageConstants.TIMESTAMP]
        long elapsed = System.currentTimeMillis() - start

        stopWatch(start, elapsed)
    }

    // mimics perf4j StopWatch even though there is not one StopWatch instance alive for the duration of a patient load
    private void stopWatch(long start, long elapsed) {
        StopWatch stopWatch = new StopWatch(start, elapsed, "vpr.load.patient", null)
        LoggerFactory.getLogger(StopWatch.DEFAULT_LOGGER_NAME).info(stopWatch.toString())
    }
}
