package org.osehra.cpe.vpr.termeng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Works with pre-compiled/computed JSON document representations of concepts in a Mongo database
 * 
 * Has some additional functionality useful for populating the mongo store as well (save()/toJSON()/toMap())
 */
public class MongoTermDataSource implements ITermDataSource {
	private DBCollection collection;
	private Map<String, Object> codeSystems;

	public MongoTermDataSource(DBCollection col) {
		this.collection = col;
	}
	
	// Custom functionality for Mongo -----------------------------------------
    public void save(Map<String, Object> data) {
    	DBObject obj = new BasicDBObject(data);
		obj.put("_id", data.get("urn"));
		
		// re-arange the generic relationships structure
		Map<String,String> rels = (Map<String, String>) data.get("rels");
		Map<String, List<String>> ret = new HashMap<String,List<String>>();
		for (String key : rels.keySet()) {
			String type = rels.get(key);
			List<String> vals = ret.get(type);
			if (vals == null) {
				vals = new ArrayList<String>();
				ret.put(type, vals);
			}
			vals.add(key);
		}
		obj.put("rels", ret);
		
		collection.insert(obj);
    }
    
    //--------------------------------

	@Override
	public Set<String> getCodeSystemList() {
		if (codeSystems == null) {
			codeSystems = new HashMap<String, Object>();
			for (Object sab : collection.distinct("codeSystem")) {
				codeSystems.put(sab.toString(), null);
			}
		}
		return codeSystems.keySet();
	}
	
	@Override
	public Map<String, Object> getCodeSystemMap() {
		if (codeSystems == null) getCodeSystemList();
		return codeSystems;
	}
	
	@Override
	public List<String> search(String text) {
		return null; // not implemented
	}

	@Override
	public boolean contains(String urn) {
		for (String sys : getCodeSystemList()) {
			if (urn.startsWith("urn:" + sys.toLowerCase())) {
				return true;
			}
		}
		return false;
	}


	@Override
	public String getDescription(String urn) {
		DBObject result = collection.findOne(urn, new BasicDBObject("description", 1));
		if (result != null) {
			return (String) result.get("description");
		}
		return null;
	}


	@Override
	public Map<String, Object> getConceptData(String urn) {
		DBObject result = collection.findOne(urn);
		if (result != null) {
			return result.toMap();
		}
		return null;
	}
	

	// Set methods ------------------------------------------------------------
	
	@Override
	public Set<String> getEquivalentSet(String urn) {
		DBObject result = collection.findOne(urn, new BasicDBObject("sameas", 1));
		if (result != null && result.containsField("sameas")) {
			return (Set<String>) result.get("sameas");
		}
		return new HashSet<String>();
	}

	@Override
	public Set<String> getParentSet(String urn) {
		DBObject result = collection.findOne(urn, new BasicDBObject("parents", 1));
		if (result != null && result.containsField("parents")) {
			return (Set<String>) result.get("parents");
		}
		return new HashSet<String>();
	}

	@Override
	public Set<String> getAncestorSet(String urn) {
		DBObject result = collection.findOne(urn, new BasicDBObject("ancestors", 1));
		if (result != null && result.containsField("ancestorIdx")) {
			return (Set<String>) result.get("ancestorIdx");
		}
		return new HashSet<String>();
	}

	@Override
	public Map<String, String> getRelMap(String urn) {
		DBObject result = collection.findOne(urn, new BasicDBObject("rels", 1));
		if (result != null && result.containsField("rels")) {
			return (Map<String,String>) result.get("rels");
		}
		return new HashMap<String,String>();
	}
}
