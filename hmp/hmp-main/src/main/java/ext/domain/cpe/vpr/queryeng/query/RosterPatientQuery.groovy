package EXT.DOMAIN.cpe.vpr.queryeng.query;

import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.RosterService;
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO;
import EXT.DOMAIN.cpe.vpr.queryeng.Query.QueryMode;

import EXT.DOMAIN.cpe.vpr.queryeng.Query
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDefRenderer;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;

/**
 * Combines roster search and free-text patient search into a single viewdef.
 * 
 * If the patient is on the roster (or in the search results) but not in the VPR
 * then the record is still included, but should not be clickable (because vprid is not present).  
 * If the patient is in the VPR, then additional fields (from PatientDao) are also included in the results. 
 * 
 * Uses the roster service to perform the search/query.
 * 
 * Requires the following params: 1) roster.ien OR search 2) ViewParam.SessionParams() be registered.
 * @author brian
 */
public class RosterPatientQuery extends Query {
	
	private RosterService rosterSvc;
	private IPatientDAO patientDao;

	public RosterPatientQuery(RosterService svc, IPatientDAO patient) {
		super("dfn", null, QueryMode.ONCE);
		rosterSvc = svc;
		patientDao = patient;
	}
	
	// for each row, map additional values in from the Patient object (if any)
	protected Map<String, Object> mapRow(RenderTask task, Map<String, Object> row) {
		String dfn = row.get("dfn");
		String vistaId = task.getParamStr("vista_id");
		// TODO: Should probably throw an error if vista_id is null, indicates the correct params were not available?
		Patient pat = patientDao.findByAnyPid("${vistaId};${dfn}".toString());
		if (pat != null) {
			row.putAll([pid: pat.pid, familyName: pat?.familyName, givenNames: pat?.givenNames,
				updated: pat?.getLastUpdated(), sensitive: pat?.isSensitive(), died: pat?.getDied()]);
		}
		return row;
	}

	@Override
	public void exec(RenderTask task) throws Exception {
		String ien = task.getParamStr("roster.ien");
		String searchStr = task.getParamStr("search");
		List<Map> results;
		if (ien != null && ien.trim().length() > 0) {
			results = rosterSvc.getRosterPats(ien);
		} else if (searchStr != null) {
			results = rosterSvc.searchRosterSource("Patient", searchStr);
		}
		if (results != null) {
			for (Map result : results) {
				task.add(mapRow(task, result));
			}
		}
	}
}
