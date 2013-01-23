package org.osehra.cpe.vpr.web

import org.osehra.cpe.auth.UserContext
import org.osehra.cpe.hub.dao.IVistaAccountDao
import org.osehra.cpe.jsonc.JsonCCollection
import org.osehra.cpe.jsonc.JsonCResponse
import org.osehra.cpe.param.ParamService
import org.osehra.cpe.vpr.IAppService
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.vpr.RosterService
import org.osehra.cpe.vpr.SyncError

import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.sync.ISyncService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView
import static org.springframework.web.bind.annotation.RequestMethod.GET
import static org.springframework.web.bind.annotation.RequestMethod.POST
import org.osehra.cpe.vpr.dao.ISyncErrorDao
import org.osehra.cpe.vpr.sync.vista.VprUpdateJob

@Controller
class SyncController implements EnvironmentAware {

    @Autowired
    ISyncService syncService

    @Autowired
    RosterService rosterService

    @Autowired
    IVistaAccountDao vistaAccountDao

    @Autowired
    ISyncErrorDao syncErrorDao

    @Autowired
    IPatientDAO patientDao

    @Autowired
    UserContext userContext

    @Autowired
    ParamService paramService

    @Autowired
    IAppService appService

    @Autowired
    VprUpdateJob vprUpdateJob

    Environment environment

    @RequestMapping(value = "/sync/stats", method = GET)
    ModelAndView stats() {
        long queueSize = syncService.getProcessingQueueSize();

        List stats = []
        stats << [name: 'Patients', value: patientDao.count()]
        stats << [name: 'Patients with Errors', value: syncErrorDao.countAllPatientIds()]
        stats << [name: 'Sync Errors', value: syncErrorDao.count()]
        stats << [name: 'Work Queue', value: queueSize]
        stats << [name: 'Automatic Updates', value: (vprUpdateJob.disabled ? 'Disabled' : 'Enabled')]

        return contentNegotiatingModelAndView([data: [items: stats]])
    }

    @RequestMapping(value = "/sync/loadRosterPatients", method = POST)
    ModelAndView loadRosterPatients(HttpServletRequest request,
                                    @RequestParam(required = false) String rosterId) {
        String vistaId = userContext.currentUser.vistaId;

        List<String> dfnList = rosterService.getRosterPatDFNs(rosterId)

        String message
        if (dfnList == null || dfnList.isEmpty()) {
            message = "Roster ${rosterId} is empty"
        } else {
            syncService.sendLoadPatientsMsg(vistaId, dfnList)
            message = "Loading patients from roster ${rosterId}"
        }

        return contentNegotiatingModelAndView(JsonCResponse.create(request, [message: message]));
    }

      @RequestMapping(value = "/sync/load", method = POST)
    ModelAndView load(@RequestParam(required = false) String icn, @RequestParam(required = false) String dfn, HttpServletRequest request) {
        if (!icn && !dfn) throw new BadRequestException("either 'dfn' or 'icn' request parameter is required")

        String vistaId = userContext.currentUser.vistaId;
		
		//Is patient to load current patient
		if(isSelectedPatientToReload(request.session.getAttribute("pid"), dfn,vistaId,icn)){	
			request.session.removeAttribute("pid");
		}

        String message
        if (icn) {
            syncService.sendLoadPatientMsgWithIcn(vistaId, icn)
            message = "Loading patient ${icn}..."
        } else if (dfn) {
            syncService.sendLoadPatientMsgWithDfn(vistaId, dfn)
            message = "Loading patient ${vistaId};${dfn}..."
        }

        return contentNegotiatingModelAndView(JsonCResponse.create(request, [message: message]));
    }
		
    @RequestMapping(value = "/sync/clearPatient", method = POST)
    ModelAndView clearPatient(
            @RequestParam(required = false) String icn,
            @RequestParam(required = false) String dfn,
            @RequestParam(required = false) String vistaId,
            @RequestParam(required = false) String pid,
            HttpServletRequest request) {
        if (!icn && !(dfn && vistaId) && !pid) throw new BadRequestException("either 'dfn' and 'vistaId' or 'icn' or 'pid' request parameter is required")


        Patient pt = null;
        if (dfn && vistaId) {
            pt = patientDao.findByLocalID(vistaId, dfn);
        }
        if (icn) {
            pt = patientDao.findByIcn(icn);
        }
        if (pid) {
            pt = patientDao.findByAnyPid(pid);
        }

        String message
        if (pt) {
			
			def lastSelectedPid = request.session.getAttribute("pid");
			if(lastSelectedPid && pt?.pid == lastSelectedPid){
				request.session.removeAttribute("pid");
			}
	
            syncService.sendClearPatientMsg(pt);
            message = "Clearing patient ${pt.patientIds.join(',')}..."
        } else {
            message = "Patient not found."
        }

        return contentNegotiatingModelAndView(JsonCResponse.create(request, [message: message]));
    }

    @RequestMapping(value = "/sync/toggleAutoUpdates", method = POST)
    ModelAndView autoUpdates(HttpServletRequest request) {
        vprUpdateJob.setDisabled(!vprUpdateJob.isDisabled());

        boolean enabled = !vprUpdateJob.disabled
        String message = enabled ? "Automatic Updates Enabled" : "Automatic Updates Disabled";
        return contentNegotiatingModelAndView(JsonCResponse.create(request, [message: message, autoUpdatesEnabled: enabled]));
    }

    @RequestMapping(value = "/sync/clearAllPatients", method = POST)
    ModelAndView clearAllPatient(HttpServletRequest request) {
		//remove reference to last selected patient from session.
		request.session.removeAttribute("pid");
        syncService.sendClearAllPatientsMsg();
        String message = "Clearing all patients..."
        return contentNegotiatingModelAndView(JsonCResponse.create(request, [message: message]));
    }

    @RequestMapping(value = "/sync/reindexPatient", method = POST)
    ModelAndView reindexPatient(@RequestParam(required = true) String pid, HttpServletRequest request) {
        if (!pid) throw new BadRequestException("'pid' request parameter is required")

        Patient pt = patientDao.findByAnyPid(pid);
        if (pt) {
            syncService.sendReindexPatientMsg(pt)

            String message = "Reindexing patient ${pid}..."

            return contentNegotiatingModelAndView(JsonCResponse.create(request, [message: message]));
        } else {
            throw new PatientNotFoundException(pid);
        }
    }

    @RequestMapping(value = "/sync/reindexAllPatients", method = POST)
    ModelAndView reindexAllPatients(HttpServletRequest request) {
        syncService.sendReindexAllPatientsMsg()
        String message = "Reindexing all patients..."
        return contentNegotiatingModelAndView(JsonCResponse.create(request, [message: message]));
    }

    @RequestMapping(value = "/sync/syncErrors", method = RequestMethod.GET)
    ModelAndView syncErrors(Pageable pageable, @RequestParam(required = false) String format) {
        Page<SyncError> page = syncErrorDao.findAll(new PageRequest(pageable.pageNumber, pageable.pageSize, Direction.DESC, "dateCreated"));

        JsonCCollection paginatedCollection = JsonCCollection.create(page)
        List<Map> patientSyncErrors = paginatedCollection.items.collect { SyncError error ->
            Patient pt = error.pid ? patientDao.findByVprPid(error.pid) : null
            [patient: pt?.getFullName(), pids: pt?.getPatientIds(), id: error.id, item: error.item, dateCreated: error.dateCreated, json: error.json, message: error.message, stackTrace: error.stackTrace]
        }
        paginatedCollection.data.items = patientSyncErrors

        return contentNegotiatingModelAndView(paginatedCollection)
    }

    @RequestMapping(value = "/sync/syncErrors/clear", method = RequestMethod.POST)
    ModelAndView clearAllSyncErrors(HttpServletRequest request) {
        syncErrorDao.deleteAll();
        return contentNegotiatingModelAndView(JsonCResponse.create(request, [message: "Cleared All Sync Errors"]))
    }
	
	boolean isSelectedPatientToReload(String pid, String dfn, String vistaId, String icn){
		boolean check = false;
		if(pid){
			Patient pt = null
			if (dfn && vistaId) {
				pt = patientDao.findByLocalID(vistaId, dfn);
			}
			else if (icn) {
				pt = patientDao.findByIcn(icn);
			}
			if(pt?.pid == pid){
				check = true
			}
		}
		return check;
	}

}

