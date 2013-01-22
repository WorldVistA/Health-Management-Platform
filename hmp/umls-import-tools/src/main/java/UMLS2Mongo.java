import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;


public class UMLS2Mongo {
	private static Connection c;
	private static PreparedStatement ps, ps2, ps3;
	private static int queryCount=0;
	
	public static void main(String[] args) throws Exception {
		if (args.length < 6) {
			System.out.println("Usage: UMLS2Mongo <JDBC URL> <JDBC USER> <JDBC PASSWORD> <SCHEMA> <sab> <limit>");
			return;
		}
		
		// initalize mongo connection
		Mongo mongo = new Mongo("localhost");
		DB db = mongo.getDB("vpr");
		DBCollection coll = db.getCollection("umls");
		
		// initalize the db connection
		String tab = args[3] + ".umls_concept_index";
		String sab = args[4];
		c = DriverManager.getConnection(args[0], args[1], args[2]);
		ps = c.prepareStatement("SELECT * FROM " + tab + " WHERE aui=?");
		ps2 = c.prepareStatement("SELECT * FROM " + args[3] + ".mrconso WHERE sab=? AND code=?");
		ps3 = c.prepareStatement("SELECT * FROM " + args[3] + ".mrsat WHERE sab=? AND code=? ORDER BY atn");
		
		// initalize counters
		long startAtMS = System.currentTimeMillis();
		int count = 0;
		int total = runQueryInt("SELECT COUNT(*) as counter FROM " + tab + " WHERE sab='" + sab + "'", false);
		int limit = Integer.parseInt(args[5]);
		int step = Math.min(1000, (limit/100));
		
		// fetch each record from the relational db and insert it into mongodb
		ResultSet rs = runQuery("SELECT aui, parent_set FROM " + tab + " WHERE sab='" + sab + "' LIMIT " + limit, false);
		while (rs.next() && count <= limit) {
			DBObject doc = getRow(rs.getString("AUI"), true, false);
			DBObject ancestors = computeAncestors(doc);
			BasicDBList idx = new BasicDBList();
			idx.addAll(ancestors.keySet());
			doc.put("ancestorIdx", idx);
			if (sab.equals("ICD9CM")) {
				DBObject newAncestors = new BasicDBObject();
				for (String key : ancestors.keySet()) {
					newAncestors.put(key.replace('.', '_'), ancestors.get(key));
				}
				ancestors = newAncestors;
			}
			doc.put("ancestors", ancestors);
			coll.insert(doc);
			
			if (++count % step == 0) {
				if (cache.size() > 25000) cache.clear();
				showStatus(count, Math.min(total, limit), startAtMS);
			}
		}
		rs.close();
		
		System.out.println("\n\nLOADING COMPLETE!");
		showStatus(count, total, startAtMS);
	}
	
	private static DBObject computeAncestors(DBObject base) {
		BasicDBObject ret = new BasicDBObject();
		BasicDBList parents = (BasicDBList) base.get("parents");
		if (parents != null && parents.size() > 0) {
			for (int i=0; i < parents.size(); i++) {
				Object obj = parents.get(i);
				if (obj instanceof DBObject) {
					DBObject dbo = (DBObject) obj;
					String id = (String) dbo.get("_id");
					ret.put(id, dbo.get("pref_desc"));
					ret.putAll(computeAncestors(dbo));
				}
			}
		}
		
		return ret;
	}
	
	// cache the rows
	private static HashMap<String, DBObject> cache = new HashMap<String, DBObject>();
	private static DBObject getRow(String aui, boolean recurse, boolean compact) throws SQLException {
		// if the object exists in the cache, return it
		if (cache.containsKey(aui)) {
			return cache.get(aui);
		}
		
		DBObject doc = new BasicDBObject();
		ps.setString(1, aui);
		ResultSet rs = ps.executeQuery();
		queryCount++;
		Array parents = null; // don't want to have too many open result sets
		Array sameas = null;
		if (rs.next()) {
			String id = getID(rs.getString("SAB"), rs.getString("CODE"));
			parents = rs.getArray("PARENT_SET");
			sameas = rs.getArray("SAMEAS_SET");
			doc.put("_id", id);
			doc.put("pref_desc", rs.getString("PREF_DESC"));
			if (compact == false) {
				doc.put("cui", rs.getString("CUI"));
				doc.put("sab", rs.getString("SAB"));
				doc.put("code", rs.getString("CODE"));
				doc.put("aui", rs.getString("AUI"));
			}
		} else {
			// doesn't exists
			cache.put(aui, null);
			return null;
		}
		rs.close();
		
		// get all the terms for this concept
		ps2.setString(1, (String) doc.get("sab"));
		ps2.setString(2, (String) doc.get("code"));
		rs = ps2.executeQuery();
		BasicDBList terms = new BasicDBList();
		while (rs.next()) {
			BasicDBObject term = new BasicDBObject();
			term.put("aui", rs.getString("aui"));
			term.put("lat", rs.getString("lat"));
			term.put("tty", rs.getString("tty"));
			term.put("str", rs.getString("str"));
			terms.add(term);
		}
		rs.close();
		if (terms.size() > 0) {
			doc.put("terms", terms);
		}
		
		// get all the attributes
		ps3.setString(1, (String) doc.get("sab"));
		ps3.setString(2, (String) doc.get("code"));
		rs = ps3.executeQuery();
		BasicDBObject atts = new BasicDBObject();
		while (rs.next()) {
			String atn = rs.getString("atn");
			String atv = rs.getString("atv");
			if (!atts.containsField(atn)) {
				atts.put(atn, atv);
			} else if (atts.get(atn) instanceof BasicDBList){
				// append to existing list
				((BasicDBList)atts.get(atn)).add(atv);
			} else {
				// must convert values to a list of 2
				BasicDBList l = new BasicDBList();
				l.add(atts.get(atn));
				l.add(atv);
				atts.put(atn, l);
			}
		}
		rs.close();
		if (atts.size() > 0) {
			doc.put("attributes", atts);
		}
		
		// sameas/synonyms
		if (sameas != null && compact == false) {
			BasicDBList l = new BasicDBList();
			for (String p : (String[]) sameas.getArray()) {
				DBObject row = getRow(p, false, true);
				if (row != null) l.add(row);
			}
			if (l.size() > 0) doc.put("sameas", l);
		}
		
		// recursively load parents
		if (parents != null && recurse) {
			// explicit parents
			BasicDBList l = new BasicDBList();
			BasicDBList same = (BasicDBList) doc.get("sameas");
			if (same != null) l.putAll(same);
			for (String p : (String[]) parents.getArray()) {
				DBObject row = getRow(p, true, true);
				if (row != null) l.add(row);
			}
			if (l.size() > 0) doc.put("parents", l);
		}
		
		// cache the results
		cache.put(aui, doc);
		return doc;
	}
	
	private static void showStatus(int rowsCount, int rowsTotal, long startAtMS) {
		// display progress
		int pct = Math.round((float) rowsCount / (float) rowsTotal * 100f);
		long elapsed = ((System.currentTimeMillis() - startAtMS) / 1000 );
		String msg = String.format("Loading... (%s/%s=%s%%; %s queries; %s cache) %ss elapsed", 
				rowsCount, rowsTotal, pct, queryCount, cache.size(), elapsed);
		System.out.print(msg.trim());
		for (int i = 1; i <= msg.length(); i++) {
			System.out.print("\b");
		}
	}
	
	private static String getID(String sab, String code) {
		if (sab.equals("SNOMEDCT")) {
			return "urn:sct:" + code;
		} else if (sab.equals("LNC")) {
			return "urn:lnc:" + code;
		}
		return "urn:" + sab.toLowerCase() + ":" + code;
	}
	
	private static int runQueryInt(String sql, boolean suppressErrors) throws SQLException {
		int ret = -1;
		ResultSet rs = runQuery(sql, suppressErrors);
		if (rs.next()) {
			ret = rs.getInt(1);
		}
		rs.close();
		return ret;
	}

	private static ResultSet runQuery(String sql, boolean suppressErrors)
			throws SQLException {
		// there is a known cursor leak here.
		Statement stmt = null;
		try {
			stmt = c.createStatement();
			queryCount++;
			return stmt.executeQuery(sql);
		} catch (SQLException ex) {
			if (suppressErrors) {
				// the transaction is corrupt, start a new one
				c.rollback();
				return null;
			}
			throw ex;
		} finally {
			// if (stmt != null)
			// stmt.close();
		}
	}
}
