package org.osehra.cpe.vpr
import static org.osehra.cpe.vpr.UserInterfaceRpcConstants.VPR_UI_CONTEXT
import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vista.rpc.RpcOperations
import org.osehra.cpe.vpr.vistasvc.CacheMgr.CacheType
import org.osehra.cpe.vpr.vistasvc.CacheMgr
import org.osehra.cpe.vpr.vistasvc.ICacheMgr;

import java.text.SimpleDateFormat
import java.util.GregorianCalendar;

import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.fasterxml.jackson.databind.node.ArrayNode

@Service
class RosterService {

    private static final Set<String> SOURCES = new HashSet<String>(['Clinic','Ward','OE/RR','PCMM Team','Provider','PXRM','Specialty','Patient','VPR Roster']);

    @Autowired
    protected RpcOperations rpcTemplate;
	
	protected ICacheMgr cache = new CacheMgr("RosterCache", CacheType.MEMORY);

    private XmlSlurper xmlSlurper = new XmlSlurper();

	public List<Map> searchRosterSource(String src, String search = "") {
        if (!SOURCES.contains(src)) throw new IllegalArgumentException("Unexpected roster src '${src}', should be one of ${SOURCES}");
        // TODO: Put some pagination controls in here?
		
		// run the RPC and convert to XML document
		String str = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPR GET SOURCE", [src, search ?: '']);
		
		// an empty roster sometimes returns "1^EMPTY ROSTER"
		if (str.startsWith("1^")) {
			return null;
		}
		
		def xml = null;
		def row = null;
		def ret = [];
		try {
			xml = xmlSlurper.parseText(str);
		} catch (Exception ex) {
			throw new RuntimeException("Unable to parse VPR GET SOURCE XML: " + str, ex);
		}
		xml.source?.entries?.children().each {
			if (it.name() == 'entry') {
				if (row != null) ret.add(row);
				row = [id: it.@id.text(), name: it.@NAME.text()]
				
				// SEMI Hack: some users of this function expect patient search to return DFN, some
				// expect ID.  
				if (src == 'Patient') {
					row.dfn = row.id;
				}
			} else if (it.name() == 'identifiers') {
				it.children().each {
					String key = it.@name.text().toLowerCase();
					String val = it.@value.text();
					if (key == 'icn') {
						val = parseICN(val);
					}
					row.putAt(key, val);
				}
				ret.add(row);
				row = null;
			}
		}
		
		return ret;
	}

	/**
	 * TODO: Document the definition format: ['DR ROBERT ALLEN^^Dr. Allens Patients^^20012','Clinic^UNION^195','Ward^UNION^38']
	 * 
	 * @param definition
	 * @return
	 */
	public List<Map> updateRoster(String[] definition, boolean previewOnly) {
		if (previewOnly) {
			def str = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPR PREVIEW ROSTER", [definition]);
			return parseRosterXML(str);
		} else {
			cache.removeAll();
			String str = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPR UPDATE ROSTER", [definition]);
			return parseRosterXML(str);
		}
	}
	
	public List<Map> updateRoster(ArrayNode definition, boolean previewOnly) {
		ArrayList<String> convertToArray = new ArrayList<String>();
		definition.each {
			convertToArray.add(it.textValue());
		}
		String[] defs = new String[convertToArray.size()];
		int x = 0;
		for(String s: convertToArray)
		{
			defs[x++] = s;
		}
		return updateRoster(defs, previewOnly);
	}
	
	public String deleteRoster(String id) {
		cache.removeAll();
		return rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPR DELETE ROSTER", [id]);
	}

	public List<Map> getRosterPats(String rosterID) {
		List rosters = getRostersById(rosterID)
		
		// instead of returning all rosters, just return the patients on the one roster
		if (rosters && rosters.size()) {
			return rosters.get(0).patients;
		}
		return [];
	}
	
	public List getRosterPatDFNs(String rosterID) {
		def ret = [];
		List l = getRosterPats(rosterID);
		for (def i in l) {
			ret.add(i.dfn);
		}
		return ret;
	}
	
	public List<Map> getRosters() {
		List<Map> ret = cache.fetch("ALL_ROSTERS");
		if (!ret) {
			def xml = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPR ROSTERS", ['']);
			ret = parseRosterXML(xml);
			cache.store("ALL_ROSTERS", ret);
		}
		return ret;
	}
	
	public List<Map> addPatientToRoster(String dfn,String rosterId){
		Map roster = getRostersById(rosterId)?.get(0)
		return updateRoster(buildRPCDefenition(roster,dfn), false);
	}
	
	protected List getRostersById(String rosterID) {
		// First, check the cache for rosters, if it doesn't exist, fetch from VistA and store in cache
		List rosters = cache.fetch(rosterID);
		if (!rosters) {
			def xml = rpcTemplate.executeForString("/${VPR_UI_CONTEXT}/VPR ROSTER PATIENTS", rosterID);
			rosters = parseRosterXML(xml);
			cache.store(rosterID, rosters, 600);
		}
		return rosters
	}

	protected String[] buildRPCDefenition(Map rosterMap, String dfn){
		def defenition = []
		defenition.add(sprintf('%1s^^%2s^^%3s',[rosterMap?.name,rosterMap?.display,rosterMap?.ownerid]))
		
		for (def pat in rosterMap?.patients){ 
			defenition.add(buildPatientRPCDefenition(pat.dfn))
		}
		//add new patient
		defenition.add(buildPatientRPCDefenition(dfn))
		return defenition
	}
	
	protected String buildPatientRPCDefenition(String dfn){		
		return "Patient^UNION^$dfn"
	}
	
	private List parseRosterXML(String xmlStr) {
		if (xmlStr == null || xmlStr.size() == 0 || xmlStr.startsWith('1^EMPTY ROSTER')) {
			return [];
		}
		
		// check for errors
		// TODO: how to pass errors back to GUI? Evaluate google return format.
		if (xmlStr.startsWith('Invalid Entry in Roster')) {
			return [];
		}
		
		// convert to an object (for rendering)
		def rosters = [];
		def xml = null;
		try {
			xml = new XmlSlurper().parseText(xmlStr);
		} catch (Exception ex) {
			System.out.println(xmlStr);
			ex.printStackTrace();
			throw new RuntimeException("Unable to parse roster XML: " + xmlStr, ex);
		}
		for (roster in xml.roster) {
			def r = [id: roster.@ien.text(), name: roster.rosterName.text(), display: roster.displayName.text(),
				ownerid: roster.@ownerid.text(), ownername: roster.@ownername.text()];
			
			// if there is source info, parse that
			if (roster.sources.size()) {
				r.sources = [];
				for (src in roster.sources.source) {
					r.sources.add([sequence: src.@sequence.text(), type: src.@type.text(), id: src.@id.text(), name: src.@name.text(), operation: src.@operation.text()]);
				}
			}

			if (roster.patients.size()) {
				r.patientCount = roster.patients.patient.size();
				r.patients = [];
				for (pat in roster.patients.patient) {
					
					def dob = new PointInTime(pat.@dob.text());
					def ret = [
						dfn: pat.@id.text(),
						name: pat.@name.text(),
						gender: pat.@gender.text(),
						icn: parseICN(pat.@icn.text()),
						ssn: pat.@ssn.text(),
						dob: dob,
						age: new Period(dob, PointInTime.today()).getYears()
					];
				
					// TODO: have vista return this
					ret.uid = "urn:va:patient:foo:${ret.dfn}".toString();
					ret.last4 = (ret.ssn.length() > 4) ? ret.ssn.substring(ret.ssn.length() - 4) : ret.ssn;
				
					// TODO: Default these in EXTJS not here: qtip: '[NON-VPR]', leaf: true, expandable: false
					r.patients.add(ret)
				}
				
			}
			rosters.add(r);
		}
		
		return rosters;
	}
	
	// ICN Parsing: strip the VXXXX off the end and convert -1^NO MPI NODE to null.
	private static String parseICN(String icn) {
		def idx = icn.indexOf('V');
		if (idx > 0) {
			return icn.substring(0,idx);
		} else if (icn.startsWith("-1^")) {
			return null;
		}
		return icn;
	}
}
