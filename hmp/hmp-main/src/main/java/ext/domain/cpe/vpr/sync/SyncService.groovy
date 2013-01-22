package EXT.DOMAIN.cpe.vpr.sync

import EXT.DOMAIN.cpe.auth.UserContext;
import EXT.DOMAIN.cpe.vpr.EventController
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService

import org.springframework.jms.core.MessagePostProcessor
import org.springframework.stereotype.Service

import javax.jms.Message

import org.springframework.jms.core.JmsOperations
import EXT.DOMAIN.cpe.HmpProperties
import com.fasterxml.jackson.databind.ObjectMapper

import javax.management.MBeanServerConnection

import org.apache.activemq.broker.jmx.QueueViewMBean

/**
 * Class responsible for dispatching sync messages via JMS for processing by JMS listeners.
 */
@Service
class SyncService implements ISyncService {

    private static final String JMSXGROUP_ID = "JMSXGroupID"
    private static final String JMSXGROUP_SEQ = "JMSXGroupSeq"

    static Logger log = LoggerFactory.getLogger(SyncService)

    @Autowired
    IPatientDAO patientDao

    @Autowired
    JmsOperations jmsTemplate

    @Autowired
    ConversionService conversionService

	@Autowired
	QueueViewMBean vprWorkQueueMBean

    @Autowired
    UserContext userContext
	
	@Autowired
	EventController eventController
	
    long getProcessingQueueSize() {
        try {
            return vprWorkQueueMBean.getQueueSize();
        }catch (Throwable t) {
            log.warn("Error connecting to work queue : " + t.getMessage())
            return -1;
        }
    }
	
    void sendLoadPatientsMsg(String vistaId, List<String> dfnList) {
        dfnList.each { dfn ->
            sendLoadPatientMsgWithDfn(vistaId, dfn.toString())
        }
    }

    void sendLoadPatientMsgWithIcn(String vistaId, String icn) {
        Map msg = [:]
        msg[SyncMessageConstants.ACTION] = SyncAction.PATIENT_LOAD
        msg[SyncMessageConstants.VISTA_ID] = vistaId
        msg[SyncMessageConstants.PATIENT_ICN] = icn
        msg[SyncMessageConstants.TIMESTAMP] = System.currentTimeMillis()

        getJmsTemplate().convertAndSend(SyncQueues.PROCESSING_QUEUE, msg)
    }

    void sendLoadPatientMsgWithDfn(String vistaId, String dfn) {
        Map msg = [:]
        msg[SyncMessageConstants.ACTION] = SyncAction.PATIENT_LOAD
        msg[SyncMessageConstants.VISTA_ID] = vistaId
        msg[SyncMessageConstants.PATIENT_DFN] = dfn
        msg[SyncMessageConstants.TIMESTAMP] = System.currentTimeMillis()

        getJmsTemplate().convertAndSend(SyncQueues.PROCESSING_QUEUE, msg)
    }

    void sendLoadPatientCompleteMsg(Patient pt, Map loadMsg) {
        Map loadCompleteMsg = loadMsg.clone()
        loadCompleteMsg[SyncMessageConstants.ACTION] = SyncAction.PATIENT_LOAD_COMPLETE

        getJmsTemplate().convertAndSend(SyncQueues.PROCESSING_QUEUE, loadCompleteMsg, { Message message ->
            message.setStringProperty(JMSXGROUP_ID, getMessageGroupId(pt.getPid()))
            message.setIntProperty(JMSXGROUP_SEQ, -1); // closes message group
            return message
        } as MessagePostProcessor)
    }

    private String getMessageGroupId(String pid) {
        return "vpr.pt.${pid}"
    }

    void sendImportPatientDataExtractItemMsg(VistaDataChunk chunk) {
        assert chunk.getPatientId()
        Map parseMsg
        try {
            parseMsg = conversionService.convert(chunk, Map.class)
            parseMsg[SyncMessageConstants.ACTION] = SyncAction.IMPORT_CHUNK
        } catch (Throwable t) {
            throw t
        }
        try {
			registerChunkProcessing(chunk);
            getJmsTemplate().convertAndSend(SyncQueues.PROCESSING_QUEUE, parseMsg, { Message message ->
                message.setStringProperty(JMSXGROUP_ID, getMessageGroupId(chunk.getPatientId()))
                return message
            } as MessagePostProcessor)
        } catch (Throwable t) {
            errorDuringMsg(parseMsg, t)
        }
    }

    void retryMsg(Map msg) {
        try {
            getJmsTemplate().convertAndSend(SyncQueues.PROCESSING_QUEUE, msg);
        } catch (Throwable t) {
            errorDuringMsg(msg, t)
        }
    }

    void sendReindexPatientMsg(Patient pt) {
        sendReindexPatientMsg(pt.pid)
    }

    void sendReindexPatientMsg(String pid) {
        Map msg = [:]
        msg[SyncMessageConstants.ACTION] = SyncAction.PATIENT_REINDEX
        msg[SyncMessageConstants.PATIENT_ID] = pid
        getJmsTemplate().convertAndSend(SyncQueues.PROCESSING_QUEUE, msg)
    }

    void sendReindexAllPatientsMsg() {
        List<String> pids = patientDao.listPatientIds()
        pids.each { String pid ->
            sendReindexPatientMsg(pid)
        }
    }

    void sendClearPatientMsg(Patient pt) {
        sendClearPatientMsg(pt.pid)
    }

    void sendClearPatientMsg(String pid) {
        Map msg = [:]
        msg[SyncMessageConstants.ACTION] = SyncAction.PATIENT_CLEAR
        msg[SyncMessageConstants.PATIENT_ID] = pid
        getJmsTemplate().convertAndSend(SyncQueues.PROCESSING_QUEUE, msg)
    }

    void sendClearItemMsg(String uid) {
        Map msg = [:]
        msg[SyncMessageConstants.ACTION] = SyncAction.ITEM_CLEAR
        msg[SyncMessageConstants.UID] = uid
        getJmsTemplate().convertAndSend(SyncQueues.PROCESSING_QUEUE, msg)
    }

    void sendClearAllPatientsMsg() {
        List<String> pids = patientDao.listPatientIds()
        pids.each { String pid ->
            sendClearPatientMsg(pid)
        }
    }

    void sendUpdateVprCompleteMsg(String serverId, String vistaId, String lastUpdate, Map<String, Set<String>> domainsByPatientId) {
        Map msg = new HashMap();
        msg[SyncMessageConstants.ACTION] = SyncAction.VPR_UPDATE_COMPLETE;
        msg[SyncMessageConstants.VISTA_ID] = vistaId;
        msg[HmpProperties.SERVER_ID] = serverId;
        msg[SyncMessageConstants.TIMESTAMP] = System.currentTimeMillis();
        msg[SyncMessageConstants.PATIENT_IDS] = domainsByPatientId ? domainsByPatientId.keySet().join(',') : ''
        msg[SyncMessageConstants.PATIENT_DOMAINS_BY_PID] = domainsByPatientId ? new ObjectMapper().writeValueAsString(domainsByPatientId) : '';
        msg[SyncMessageConstants.VISTA_LAST_UPDATED] = lastUpdate;

        getJmsTemplate().convertAndSend(SyncQueues.PROCESSING_QUEUE, msg)
    }

    void errorDuringMsg(Map msg, Throwable t) {
        getJmsTemplate().convertAndSend(SyncQueues.ERROR_QUEUE, SyncMessageUtils.createErrorMessage(msg, t), { Message message ->
            message.setStringProperty(SyncMessageConstants.PATIENT_ID, msg.get(SyncMessageConstants.PATIENT_ID)); // this enables us to select messages from the queue
            return message
        } as MessagePostProcessor);
    }
	
	Map<String, Integer[]> chunkProcessingCounts = java.util.Collections.synchronizedMap(new HashMap<String, Integer[]>());
	Map<String, String> pidToDfnMap = new HashMap<String, String>();
	
	Thread chunkWatcher;

	@Override
	public void registerChunkProcessing(VistaDataChunk chunk) {
		String pid = chunk.getPatientId();
		if(pidToDfnMap.get(pid)==null) {
			Patient pat = patientDao.findByVprPid(pid);
			if(pat) {
				pidToDfnMap.put(pid, pat.getLocalPatientIdForSystem(chunk.systemId));
			}
		}
		String dfn = chunk.getLocalPatientId();
		pidToDfnMap.put(pid, dfn); // Yes, it smells hacky. I admit.
		if(pid) {
			Integer[] counts = chunkProcessingCounts.get(pid);
			if(!counts) {
				counts = new Integer[2];
				counts[0] = 0;
				counts[1] = 1;
				chunkProcessingCounts.put(pid, counts);
				if(chunkWatcher==null) {
					chunkWatcher = new Thread() {
						public void run() {
							while(true) {
								Thread.sleep(2000);
								ArrayList<Map> rslt = new ArrayList<Map>();
								
								boolean found = false;
								for(String key: chunkProcessingCounts.keySet()){
									Integer[] vals = chunkProcessingCounts.get(key);
									if(vals && vals.length>1) {
										found = true;
										rslt.add(['pid':key,'dfn':pidToDfnMap.get(key),'qty':vals[0],'total':vals[1]]);
									}
								}
								
								if(found) {
									eventController.broadcastMessage(['syncStatus':rslt]);
								}
							}
						}
					}
					chunkWatcher.start();
				}
			} else {
				counts[1] = counts[1] + 1;
				chunkProcessingCounts.put(pid, counts);
			}
		}
	}

	@Override
	public void deregisterChunkProcessing(VistaDataChunk chunk) {
		Integer[] counts = chunkProcessingCounts.get(chunk.getPatientId());
		if(counts) {
			counts[0] = counts[0] + 1;
			chunkProcessingCounts.put(chunk.getPatientId(), counts);
		}
	}
	
	public void clearChunkProcessingForPatientId(String pid) {
		log.debug('Clearing synchronization status for PID: '+pid);
		chunkProcessingCounts.put(pid, null);
	}
}
