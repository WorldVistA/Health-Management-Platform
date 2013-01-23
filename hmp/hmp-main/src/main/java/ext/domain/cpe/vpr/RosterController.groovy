package org.osehra.cpe.vpr

import com.fasterxml.jackson.databind.JsonNode
import org.osehra.cpe.auth.UserContext
import org.osehra.cpe.param.ParamService
import org.osehra.cpe.vista.rpc.RpcTemplate
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.pom.POMUtils
import org.osehra.cpe.vpr.sync.ISyncService;
import org.osehra.cpe.vpr.web.PatientNotFoundException
import grails.converters.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

import static org.osehra.cpe.vpr.UserInterfaceRpcConstants.VPR_UI_CONTEXT
import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView

@RequestMapping(value = ["/roster/**", "/vpr/roster/**"])
@Controller
public class RosterController {

    @Autowired
    RpcTemplate rpcTemplate

    @Autowired
    IPatientDAO patientDao

    @Autowired
    ParamService paramService

    @Autowired
    UserContext userContext

    @Autowired
    RosterService rosterService
	
    @Autowired
    ISyncService syncService

    @RequestMapping(value = "update")
    @ResponseBody
    String update(@RequestParam("def") String[] definition, @RequestParam(required = false) String id, HttpSession session, HttpServletRequest request) {
        // clear cache
        session.removeAttribute("rosters");

        // update the user preferences (if any)
        if (id) {
            // this is the list of recognized parameters that the GUI can modify.
            def keys = ['favorite', 'viewdef', 'panel']
            def prefs = [:]

            Map params = request.parameterMap
            for (p in params.keySet()) {
                if (keys.contains(p)) {
                    prefs[p] = params.get(p)[0].toString();
                }
            }

            // only update the preference if some parameters were actually defined.
            if (prefs.size() > 0) {
                paramService.setUserParamVals("VPR ROSTER PREF", id, prefs)
            }
        }

        // update the roster definition
        return rosterService.updateRoster(definition, false);
    }

    @RequestMapping(value = "uall")
    @ResponseBody
    String update(@RequestParam("set") def mySet, HttpSession session, HttpServletRequest request) {
        // clear cache
        session.removeAttribute("rosters");

        Map params = request.parameterMap;

        /*
           * When we send just one record, it comes thru as a String instead of String[].
           * Surely there's a more graceful way; I might have one of the guys look over this with me.
           */
        if (mySet instanceof String) {
            updateJsonRoster(POMUtils.parseJSONtoNode(mySet));
        }
        else {
            for (val in mySet) {
                updateJsonRoster(POMUtils.parseJSONtoNode(val));
            }
        }
    }

    private void updateJsonRoster(JsonNode myDef) {
        // update the user preferences (if any)
        if (myDef.get("id") != null) {
            // this is the list of recognized parameters that the GUI can modify.
            //def keys = ['favorite','viewdef','panel']
            def keys = ['favorite', 'viewdef', 'panel'];
            def prefs = [:];

            for (p in myDef.fieldNames()) {
                if (keys.contains(p)) {
                    prefs[p] = myDef.get(p).asText();
                }
            }
            // only update the preference if some parameters were actually defined.
            if (prefs.size() > 0) {
                paramService.setUserParamVals("VPR ROSTER PREF", myDef.get("id").textValue(), prefs)
            }
            rosterService.updateRoster(myDef.get("def"), false);
        }
    }

    @RequestMapping(value = "preview")
    ModelAndView preview(@RequestParam("def") String[] definition, HttpSession session) {
        //params.def = ['DR ROBERT ALLEN^^Dr. Allens Patients^^20012','Clinic^UNION^195','Ward^UNION^38']
        // TODO: Limit the number of returned records
        // TODO: Use a viewdef to render?  Display as a grid?
        def data = rosterService.updateRoster(definition, true).get(0);
        return contentNegotiatingModelAndView([data: data]);
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    String delete(@RequestParam String id, HttpSession session) {
        if (id) {
            session.removeAttribute("rosters");
            return rosterService.deleteRoster(id);
        }
    }

    @RequestMapping(value = "source")
    ModelAndView source(@RequestParam String id, @RequestParam(required = false) String filter) {
        def ret = [data: [], type: id, query: filter];
        if (!'Patient'.equals(id)) {
            ret.data = rosterService.searchRosterSource(id, filter)
        } else if (filter && filter.size() >= 4) { // ensure that at least 4 search characters are present for patient lists
            ret.data = rosterService.searchRosterSource(id, filter)
        }

        return contentNegotiatingModelAndView(ret)
    }

    @RequestMapping(value = "test")
    @ResponseBody
    String test() {
        def str = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/GET ROSTERS JSON", ['']);
        return str;
    }

    // adds a single patient to an existing roster, basically modifies the roster to append
    // a new patient source
    @RequestMapping(value = "addPatient")
    ModelAndView addPatient(@RequestParam String id, @RequestParam String dfn, @RequestParam(required = false) String del) {
        List pats = rosterService.getRosterPatDFNs(id);
		
		String vistaId = userContext.currentUser.vistaId;
		Patient pat = patientDao.findByLocalID(vistaId, del);
		
		if(!pats.contains(dfn)) {
			rosterService.addPatientToRoster(dfn, id);
		}
        // TODO: Add a Patient^UNION source?
		if(pat==null || pat.pid==null) {
	        syncService.sendLoadPatientMsgWithDfn(vistaId, dfn);
		}
        return contentNegotiatingModelAndView(['success':'true']);//"Not implemented";
    }

    /*
      * Primary roster list.
      *
      * Suitable both for lists and trees
      *
      * TODO: for trees, should it sort results into "favorite" and "other" nodes so they can all be fetched at once?
      */

    @RequestMapping(value = ["tree", "list"])
    ModelAndView tree(@RequestParam String id, HttpSession session) {
        def rosters = session.getAttribute('rosters');
        if (!rosters) {
            rosters = rosterService.getRosters();
            session.setAttribute('rosters', rosters);
            // populate all the user preferences each roster before returning results
            rosters.each {

                // get the user preferences to merge in (if any)
                // will over-ride any static map data (if specified)
                Map vals = paramService.getUserParamMap("VPR ROSTER PREF", it.id);
                if (vals != null) {
                    it.putAll(vals);
                }
            }
        }

        Map ret = [data: [], query: id, current: session.getAttribute("rosterID")];
        if (id == 'current' && ret.current) {
            id = ret.current;
        }

        if (id == '' || id == 'all') {
            ret.data = rosters;
        } else if (id == 'fav' || id == 'other') {
			String defaultRosterId;
			String userPref = paramService.getUserParam("VPR USER PREF", null);
			if(userPref) {
				Map prefMap = POMUtils.parseJSONtoMap(userPref); 
				defaultRosterId = prefMap.get('cpe.patientpicker.defaultRosterID');
			}
            for (r in rosters) {
                boolean isFav = ((r.get('id')?:'').equals(defaultRosterId)) || (r.favorite && r.favorite.toString().equalsIgnoreCase("true")) || (r.vals && r.vals.favorite && r.vals.favorite.toString().equalsIgnoreCase('true'))
                if (id == 'fav' && isFav) {
                    ret.data.add(r);
                } else if (id == 'other' && (!isFav)) {
                    ret.data.add(r);
                }
            }
        } else if (id == 'recent') {
            // TODO: re-implement this?
        } else {
            ret.data = rosters.findAll {it.id == id}
            if (ret.data.size() == 0) {
                throw new NotFoundException("Unable to find roster ${id}");
            }
        }

        // TODO: Must append this to each node
        //[leaf: true, viewdef: 'org.osehra.cpe.vpr.queryeng.RosterViewDef', panel: '/js/config/defaultchartpanel.js', allowDrop: true]

        return contentNegotiatingModelAndView(ret);
    }

    @RequestMapping(value = "select")
    ModelAndView select(@RequestParam(required = false) String pid, @RequestParam(required = false) String rosterID, HttpSession session) {
        // set the current/last roster in the session.
        if (rosterID) {
            session.setAttribute('rosterID', rosterID);
        }

        def pat = null;
        if (pid) {
            pat = patientDao.findByAnyPid(pid);
        }
        if (!pat) {
            return contentNegotiatingModelAndView();
        }

        // set the current/last patient context (and update the recent patients list)
        // first check if this is a valid patient (and obtain the DFN for the RPC)
        // this should facilitate eventually retrieving a patient by ICN, DFN, VPR ID, etc.
        String dfn = pat.getLocalPatientIdForSystem(userContext.currentUser?.vistaId)

        Map params = [:]
        params["command"] = "isPatientSensitive"
        params["patientId"] = dfn
        // call the patient checks RPC in VISTA (passing in the ICN for now since its easier than DFN)
        def chkdata = JSON.parse(rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPRCRPC RPC", [params]));

        // store the currently selected patient in the session (and recent patients list)
        session.setAttribute('pid', pid);

        // return the patient checks data
        // TODO: should this just return the full patient record?
        Map ret = [checks: chkdata];
        ret.patient = [fullName: pat.getFullName(), icn: pat.icn, age: pat.getAge(), gender: pat.getGenderCode()];

        return contentNegotiatingModelAndView(ret);
    }

    /**
     * See if a patient (or a roster of patients) has been updated since the last ping
     */
    @RequestMapping(value = "ping")
    ModelAndView ping(@RequestParam(required = false) String pid, @RequestParam(required = false) String rosterID) {
        if (pid) {
            // TODO: this probably isn't the most efficient
            def pat = patientDao.findByAnyPid(pid);
            if (pat) {
                def foo = [:]
                foo.items = []
                foo.items.add([
                        'lastUpdated': pat.lastUpdated,
                        'domainsUpdated': pat.domainUpdated
                ])
                return contentNegotiatingModelAndView(foo);
            }
            throw new PatientNotFoundException(pid);
        } else if (rosterID) {
            return contentNegotiatingModelAndView()
        }
    }

}
