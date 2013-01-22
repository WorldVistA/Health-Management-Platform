package EXT.DOMAIN.cpe.vpr.sync.msg

import EXT.DOMAIN.cpe.vista.rpc.RpcIoException
import EXT.DOMAIN.cpe.vpr.dao.RoutingDataStore
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRunner
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject
import EXT.DOMAIN.cpe.vpr.pom.PatientEvent
import EXT.DOMAIN.cpe.vpr.sync.ISyncService
import EXT.DOMAIN.cpe.vpr.sync.vista.CentralImporter
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk

import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

import org.perf4j.StopWatch
import org.perf4j.slf4j.Slf4JStopWatch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ImportChunkMessageHandler implements IMapMessageHandler {
    private static Logger log = LoggerFactory.getLogger(ImportChunkMessageHandler)

	@Autowired
	FrameRunner runner;
	
    @Autowired
    ISyncService syncService

    @Autowired
    CentralImporter centralImporter

    @Autowired
    ConversionService conversionService

    @Autowired
    RoutingDataStore routingDao
	
    void onMessage(Map msg) {
        StopWatch timer
		VistaDataChunk chunk
        try {
            chunk = conversionService.convert(msg, VistaDataChunk.class)
			
            if (log.isDebugEnabled()) log.debug("importing item: ${chunk}")

            timer = new Slf4JStopWatch("vpr.import." + chunk.domain)
            Object domainObject = centralImporter.convert(chunk)
            timer.stop()

            timer = new Slf4JStopWatch("vpr.persist." + chunk.domain + ".total")
			if(!domainObject instanceof AbstractPOMObject) {
				log.error("JSON could not be interpreted as a domain object: "+msg)
			} else {
				routingDao.save(domainObject)
			}
            timer.stop()
			
            timer = new Slf4JStopWatch("vpr.pushevents." + chunk.domain + ".total")
			runner.pushEvents(domainObject);
			timer.stop();
        } catch (RpcIoException e) {
            syncService.retryMsg(msg)
            timer?.stop(e)
        } catch (Throwable t) {
            log.error("error handling msg: ${msg}", t)
            syncService.errorDuringMsg(msg, t)
            //timer.stop("sync.fragment.parse.failure")
            timer?.stop(t)
        } finally {
			if(chunk) { // Not trying to deal with errors at this point, just an overall status of chunk processing.
				syncService.deregisterChunkProcessing(chunk);
			}
        }
    }
}
