package org.osehra.cpe.vpr.termeng;

import org.osehra.cpe.vpr.vistasvc.CacheMgr;
import org.osehra.cpe.vpr.vistasvc.CacheMgr.CacheType;
import org.osehra.cpe.vpr.vistasvc.ICacheMgr;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A data source that works off a intermediate JDBC (Postgress) database.
 * 
 * Its not intended to be used in production, its main purpose is to build Concept objects that can
 * be serialized into JSON objects and stored more efficiently.
 * 
 * It is well known that is is horribly inefficient in terms of DB query overhead.
 * 
 * Its main addition is an efficient way to enumerate and iterate over all the concepts in the database
 * so they can be loaded into some other type of database (via the iterator() method).
 * 
 * This is a formalization/replacement of the previous UMLS2Mongo java class.
 * 
 * TODO:  Since this class controls the JSON document representation of a concept, maybe there should be an option for the short form vs long form?
 */

public class JSONBuilderDataSource implements ITermDataSource {
	
	private PreparedStatement ps_main;
	private PreparedStatement ps_terms;
	private PreparedStatement ps_attrs;
	private PreparedStatement ps_aui_list;
	private PreparedStatement ps_sab_list;
	private PreparedStatement ps_sets;
	
	private Map<String,Integer> codeSystems;
	private ICacheMgr<Set<String>> parents = new CacheMgr<Set<String>>("UMLS_PARENT_CACHE", CacheType.MEMORY);
	private ICacheMgr<Map<String, Object>> rec = new CacheMgr<Map<String,Object>>("UMLS_REC_CACHE", CacheType.MEMORY);
	
	private class MyIterator implements Iterator<String> {
		
		private String sab;
		private List<String> chunk;
		private Iterator<String> itr;

		public MyIterator(String sab) {
			this.sab = sab;
			this.chunk = fetchConceptList(sab, "");
			this.itr = chunk.iterator();
		}
		
		@Override
		public boolean hasNext() {
			if (!itr.hasNext() && chunk.size() > 0) {
				// at the end of this chunk, try to get a new one...
				String lastCode = parseCode(chunk.get(chunk.size()-1));
				this.chunk = fetchConceptList(sab, lastCode);
				this.itr = chunk.iterator();
			}
			return itr.hasNext();
		}

		@Override
		public String next() {
			return itr.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public JSONBuilderDataSource(Connection conn, String schema) throws SQLException {
		// setup the various prepared statments to be used later
		String sql ="SELECT c.* FROM " + schema + ".mrconso c, " + schema + ".mrrank r ";
		sql += "WHERE c.sab=r.sab and c.tty=r.tty and c.sab=? AND c.code=? ";
		sql += "and c.tty != 'MTH_LN' ORDER BY r.rank DESC LIMIT 1"; // -- LOINC issue where PAR/CHD relationships are not under the highest ranking TTY 
		ps_main = conn.prepareStatement(sql);
		sql = "SELECT c.sab, c.code, c.aui, r.rel, r.rela FROM " + schema + ".mrrel r, " + schema + ".mrconso c WHERE r.aui1=c.aui AND r.aui2=? ";
		ps_sets = conn.prepareStatement(sql);
		ps_terms = conn.prepareStatement("SELECT aui, lat, tty, str FROM " + schema + ".mrconso WHERE sab=? AND code=?");
		ps_attrs = conn.prepareStatement("SELECT * FROM " + schema + ".mrsat WHERE sab=? AND code=? ORDER BY atn");
		ps_aui_list = conn.prepareStatement("SELECT sab, code FROM " + schema + ".mrconso WHERE sab=? AND code > ? GROUP BY sab, code ORDER BY code ASC");
		ps_aui_list.setMaxRows(1000);
		
		// also find which vocabs are in the DB/Schema
		ps_sab_list = conn.prepareStatement("SELECT sab, count(*) FROM " + schema +".mrconso GROUP BY sab");
		codeSystems = new HashMap<String,Integer>();
		ResultSet rs = ps_sab_list.executeQuery();
		while (rs.next()) {
			codeSystems.put(rs.getString(1), rs.getInt(2));
		}
		rs.close();
	}
	
	public Iterator<String> iterator(String sab) {
		return new MyIterator(sab);
	}

	@Override
	public boolean contains(String urn) {
		return true;
	}

	@Override
	public Set<String> getCodeSystemList() {
		return codeSystems.keySet();
	}
	
	@Override
	public Map<String, Object> getCodeSystemMap() {
		return new HashMap<String, Object>(codeSystems);
	}
	
	@Override
	public List<String> search(String text) {
		return null; // not implemented
	}
	
	public int getConceptCount(String sab) {
		if (codeSystems.containsKey(sab)) {
			return codeSystems.get(sab); 
		}
		return -1;
	}
	

	@Override
	public String getDescription(String urn) {
		return fetchDBRowField(urn, "str", String.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getConceptData(String urn) {
		Map<String, Object> data = fetchDBRowField(urn, null, Map.class);
		HashMap<String, Object> ret = new HashMap<String, Object>();
		if (data == null) return null;
		
		// properties
		ret.put("urn", urn);
		ret.put("code", data.get("code"));
		ret.put("codeSystem", data.get("sab"));
		ret.put("description", data.get("str"));
		ret.put("aui", data.get("aui"));
		ret.put("cui", data.get("cui"));

		// attributes/terms
		ret.put("attributes", fetchAttributes(urn));
		ret.put("terms", fetchTerms(urn));
		ret.put("rels", fetchRelationMap(urn, "RO"));
		
		// sets
		ret.put("sameas", getEquivalentSet(urn));
		ret.put("parents", getParentSet(urn));
		ret.put("ancestors", getAncestorSet(urn));
		
		return ret;
	}

	// Set functions ----------------------------------------------------------
	
	@Override
	public Set<String> getAncestorSet(String urn) {
		return getAncestorSet(urn, new HashSet<String>());
	}
		
	private Set<String> getAncestorSet(String urn, Set<String> traversed) {
		Set<String> ret = getParentSet(urn);
		
		// recursivley get the parents
		traversed.add(urn);
		HashSet<String> addl = new HashSet<String>();
		for (String s : ret) {
			if (!traversed.contains(s)) {
				addl.addAll(getAncestorSet(s, traversed));
			}
		}
		ret.addAll(addl);
		return ret;
	}
	
	@Override
	public Set<String> getEquivalentSet(String urn) {
		return getEquivalentSet(urn, new HashSet<String>());
	}
		
	private Set<String> getEquivalentSet(String urn, Set<String> traversedSet) {
		Set<String> ret = fetchRelationSet(urn, "SY");
		
		// recursivley add equivalent set from equivalents
		traversedSet.add(urn);
		HashSet<String> addl = new HashSet<String>();
		for (String s : ret) {
			if (!traversedSet.contains(s)) {;
				addl.addAll(getEquivalentSet(s, traversedSet));
			}
		}
		ret.addAll(addl);
		return ret;
	}
	
	@Override
	public Set<String> getParentSet(String urn) {
		if (parents.contains(urn)) {
			return parents.fetch(urn);
		}
		return parents.store(urn, fetchRelationSet(urn, "CHD", "RN"));
	}
	
	@Override
	public Map<String, String> getRelMap(String urn) {
		return fetchRelationMap(urn);
	}
	
	// Private helper functions -----------------------------------------------
	
	private Set<String> fetchRelationSet(String urn, String... rel) {
		return new HashSet<String>(fetchRelationMap(urn, rel).keySet());
	}
	
	private Map<String,String> fetchRelationMap(String urn, String... rel) {
		// TODO: MRSMAP - supplemental, 3rd party mappings?
		// TODO: MRCOC - statistical co-occurances?
		List<String> rels = Arrays.asList(rel);
		String aui = fetchDBRowField(urn, "aui", String.class);
		Map<String,String> ret = new HashMap<String,String>();
		ResultSet rs = null;
		try {
			ps_sets.setString(1, aui);
			rs = ps_sets.executeQuery();
			while (rs.next()) {
				if (rels.size() == 0 || rels.contains(rs.getString("rel"))) {
					String key = rs.getString("rela");
					if (key == null) key = rs.getString("rel");
					ret.put(buildURN(rs.getString("code"), rs.getString("sab")), key);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {rs.close();} catch (SQLException ex) {};
		}
		return ret;
	}
	
	private List<String> fetchConceptList(String sab, String startCode) {
		List<String> ret = new ArrayList<String>();
		ResultSet rs = null;
		try {
			ps_aui_list.setString(1, sab.toUpperCase());
			ps_aui_list.setString(2, startCode);
			rs = ps_aui_list.executeQuery();
			while (rs.next()) {
				ret.add(buildURN(rs.getString("code"), rs.getString("sab")));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {rs.close();} catch (SQLException ex) {};
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> fetchAttributes(String urn) {
		ResultSet rs = null;
		try {
			ps_attrs.setString(1, parseCodeSystem(urn));
			ps_attrs.setString(2, parseCode(urn));
			rs = ps_attrs.executeQuery();
			Map<String, Object> atts = new HashMap<String, Object>();
			while (rs.next()) {
				String atn = rs.getString("atn");
				String atv = rs.getString("atv");
				if (!atts.containsKey(atn)) {
					atts.put(atn, atv);
				} else if (atts.get(atn) instanceof List){
					// append to existing list
					((List)atts.get(atn)).add(atv);
				} else {
					// must convert values to a list of 2
					List<String> l = new ArrayList<String>();
					l.add(atts.get(atn).toString());
					l.add(atv);
					atts.put(atn, l);
				}
			}
			return atts;
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try { rs.close(); } catch (SQLException ex) {};
		}
		return null;
	}
	
	private List<Map<String, String>> fetchTerms(String urn) {
		ResultSet rs = null;
		try {
			ps_terms.setString(1, parseCodeSystem(urn));
			ps_terms.setString(2, parseCode(urn));
			rs = ps_terms.executeQuery();
			List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("aui", rs.getString("aui"));
				map.put("lat", rs.getString("lat"));
				map.put("tty", rs.getString("tty"));
				map.put("str", rs.getString("str"));
				ret.add(map);
			}
			return ret;
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try { rs.close(); } catch (SQLException ex) {};
		}
		return null;
	}

	@SuppressWarnings("unchecked")	
	private <T> T fetchDBRowField(String urn, String field, Class<T> type) {
		if (!contains(urn)) return null;
		Map<String, Object> data = rec.fetch(urn);
		if (!rec.contains(urn)) {
			ResultSet rs = null;
			try {
				ps_main.setString(1, parseCodeSystem(urn));
				ps_main.setString(2, parseCode(urn));
				rs = ps_main.executeQuery();
				data = rec.store(urn, (rs.next()) ? recToMap(rs) : null);
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				try { rs.close(); } catch (SQLException ex) {};
			}
		}
		
		if (type.equals(Map.class)) {
			return (T) data;
		} else if (type.equals(String.class) && data != null && data.containsKey(field)) {
			return (T) data.get(field).toString();
		}
		return null;
	}
	
	private static Map<String, Object> recToMap(ResultSet rs) throws SQLException {
		Map<String, Object> m = new HashMap<String, Object>();
		ResultSetMetaData meta = rs.getMetaData();
		for (int i=1; i <= meta.getColumnCount(); i++ ) {
			String key = meta.getColumnLabel(i);
			if (meta.getColumnType(i) == Types.ARRAY) {
				Array ary = rs.getArray(i);
				if (ary != null) {
					m.put(key, ary.getArray());
				}
			} else {
				m.put(key, rs.getObject(i));
			}
		}
		return m;
	}
	
	public static final String buildURN(String code, String codeSystem) {
		codeSystem = codeSystem.toLowerCase();
		if (codeSystem.equals("snomedct")) codeSystem = "sct";
		return "urn:" + codeSystem + ":" + code;
	}
	
	public static final String parseCode(String urn) {
		String[] parts = urn.split("\\:");
		return parts[2];
	}

	public static final String parseCodeSystem(String urn) {
		String[] parts = urn.split("\\:");
		String ret = parts[1].toUpperCase();
		if (ret.equals("SCT")) ret = "SNOMEDCT";
		return ret;
	}
}
