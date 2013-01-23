package org.osehra.cpe.vpr.queryeng.query;

import org.osehra.cpe.vpr.queryeng.query.QueryDefWalker.JDSFilterBuilder;
import org.osehra.cpe.vpr.queryeng.query.QueryDefWalker.MatchQueryWalker;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.util.HtmlUtils;

import com.mongodb.BasicDBObject;

/**
 * Defines a query object (but not the implementation or execution) that can be used by various 
 * JDS-backed data sources: {@see JDSQuery}, {@see org.osehra.cpe.vpr.pom.IDataStoreDAO}.  Its sort of a 
 * cross between a query/criteria/URL builder, pseudo-DSL, templating language and a filter/transfomer.
 * 
 * See the test suite for an extensive list of usage examples.
 * 
 * Defines: 
 * - fields to include/exclude and field aliases + transformations  
 * - middle-tier and server-tier filtering criteria 
 * - row start/limit (for paging) and sorting
 * 
 * Intentions:
 * - be a URL builder for JDS data stores.  
 * - implementation agnostic (originally worked with MongoDB, but Mongo implementation is currently way behind)
 * - situations with ugly conditional-syntax URLS like this:
 * -- /vpr/1/index/med-time?range=2000..2012&filter=#{getParamStr('filter.fieldx')!=null?'eq(fieldx, #{getParamStr('filter.fieldx')}):''}
 * 
 * TODO: Still want to be able to hand construct URL with a new QueryDef(String) constructor
 * TODO: Work the SPEL template engine into this?
 * TODO: Instead of the qry.addCriteria(server(...)) vs query.addCriteria(client(...)) just go back to where(...) and let the operator decide if it can be implmented server or client side?
 * 
 * Originally derived from spring-data-mongo Query+Criteria classes and then adds/removes the following:
 * 
 * - intended to be mostly agnostic of the actual storage layer: mumps, cache, mongo, relational, etc.
 * - collection and named index value/range are built in
 * - only use the operators we need and/or can deal with
 * - parameter value references (delayed evaluation, important for viewdefs)
 * - spring expression language parsing (not quite yet)
 * - uses maps for critera instead of mongo-specific DBObject's
 * - Not intended to be used for complex queries (joins, etc.), instead multiple simple queries can be joined other ways (in ViewDefs, etc).
 *
 *  New criteria operators:
 *  TODO: What kind of terminolgy enhancements could we make in here? (isa('urn:sct:xyz') criteria?)
 *  TODO: Another operator type (client only): .match(new CustomMatcher() { matches(Map row) {...}})?
 *  TODO: New operator exists("field")
 */
public class QueryDef {
	private QueryDefCriteria indexCriteria;
	
	private LinkedHashMap<String, QueryDefCriteria> criteria = new LinkedHashMap<String, QueryDefCriteria>();
	private QueryFields fields = new QueryFields();
	private QuerySort sort;
	
	private int limit = 100;
	private int skip = 0;
	
	public QueryDef() {
	}

	public QueryDef addCriteria(QueryDefCriteria crit) {
		String key = crit.getKey();
		this.criteria.put(key, crit);
		return this;
	}
	
	public QueryDef namedIndexRange(String indexName, String startRange, String endRange) {
		String key = (this.indexCriteria != null) ? this.indexCriteria.getKey() : null; 
		if (key != null && key.equals(indexName)) {
			String msg = "You can only specify one index range/value and its currently: ";
			throw new IllegalArgumentException(msg + key);
		}
		
		QueryDefCriteria crit = new QueryDefCriteria(indexName); 
		if (endRange == null) {
			crit.is(startRange);
		} else {
			crit.gte(startRange).lte(endRange);
		}
		this.indexCriteria = crit;
		return this;
	}
	
	public QueryDef namedIndexValue(String indexName, String value) {
		return namedIndexRange(indexName, value, null);
	}
	
	public QueryDefCriteria getIndexCriteria() {
		return indexCriteria;
	}
	
	public QueryDef skip(int n) {
		this.skip = n;
		return this;
	}
	
	public int getSkip() {
		return this.skip;
	}
	
	public int getLimit() {
		return this.limit;
	}
	
	public QueryDef limit(int n) {
		this.limit = n;
		return this;
	}

	public QueryFields fields() {
		return fields;
	}
	
	public QuerySort sort() {
		if (this.sort == null) {
			this.sort = new QuerySort();
		}
		return this.sort;
	}
	
    public void applyFilters(List<Map<String, Object>> items, Map<String, Object> params) {
        MatchQueryWalker matcher = null;
        if (!criteria.isEmpty()) {
        	matcher = new MatchQueryWalker(getQueryObject(params, false));
        }

        // loop through each row, apply field filters and aliases
    	QueryFields fields = fields();
    	Iterator<Map<String,Object>> rowItr = items.iterator();
    	while (rowItr.hasNext()) {
    		Map<String, Object> item = rowItr.next();
        	Map<String, Object> append = new HashMap<String, Object>();
			Iterator<String> fieldItr = item.keySet().iterator();
			while (fieldItr.hasNext()) {
				String key = fieldItr.next();
				
				// remove filtered-out fields
				if (!fields.isIncluded(key)) {
					fieldItr.remove();
				}
				
				// apply any transformations
				if (fields.transformers.containsKey(key)) {
					Object val = item.get(key);
					append.put(key, fields.transformers.get(key).transform(key, val));
				}
				
				// if alias was defined for this field, remove and replace
				if (fields.aliases.containsKey(key)) {
					append.put(fields.aliases.get(key), item.get(key));
					if (fields.isIncluded(key)) fieldItr.remove();
				}
			}
			item.putAll(append);
			
    		// apply middle tier filter logic/criteria
            if (matcher != null && !matcher.matches(item)) {
            	rowItr.remove();
            }
        }
    }
    
    public void applySorting(List<Map<String, Object>> items, Map<String, Object> params) {
    	final Map<String, Integer> sort = sort().getSortObject();
    	Collections.sort(items, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				for (String key : sort.keySet()) {
					int asc = sort.get(key);
					assert (asc == 1 || asc == -1);
					@SuppressWarnings({ "unchecked", "rawtypes" })
					Comparable<Object> v1 = (Comparable) m1.get(key), v2 = (Comparable) m2.get(key);
					
					if (v1 == v2) {
					} else if (v1 == null) {
				        return -1 * asc;
					} else if (v2 == null) {
						return +1 * asc;
			        } else {
			        	int comp = v1.compareTo(v2) * asc;
			        	if (comp != 0) return comp;
			        }
				}
				return 0;
			}
		});
    }
	
	/**
	 * Constructs a JDS query/filter URL from this QueryDef
	 */
	public String toURL(Map<String, Object> params, int start, int count) {
		if (params == null) params = new HashMap<String, Object>();
		StringBuilder ret = new StringBuilder("/vpr/");
		QueryDefCriteria indexCriteria = this.getIndexCriteria();
		if (indexCriteria == null) {
			String msg = "JDSQuery only supports DAOQuery's using named indexes";
			throw new IllegalArgumentException(msg);
		}
		
		// get the named-index criteria and interpolate its variables
		Map<String,Object> data = indexCriteria.buildCriteriaObject(params);
		Object val = (data != null) ? data.get(indexCriteria.getKey()) : null;
		
		// look for pid in params, if it's not there get it later from request
		// TODO: this is not the safest way to get the patient id
		String pid = null;
		if (params.containsKey("pid")) {
			pid = (String) params.get("pid");
		} else if (this.criteria.containsKey("pid")) {
			Map<String, Object> m = this.criteria.get("pid").buildCriteriaObject(null);
			pid = (String) m.get("pid");
		} else {
			// only works in ViewDefs....
			pid = "#{getParamStr('pid')}";
		}
		ret.append(pid);
		ret.append("/index/" + indexCriteria.getKey());
		
		// include the &range attribute (if necessary)
		if (val instanceof Map && ((Map) val).containsKey("$gte")) {
			Map<?,?> map = (Map<?,?>) val;
			ret.append("?range=" + map.get("$gte") + ".." + map.get("$lte"));
		} else if (val instanceof Map) {
			// empty map, do nothing....
		} else if (val != null) {
			ret.append("?range=" + val.toString());
		}
		
		// include the &filter attribute?
		Map<String, Object> filterData = getQueryObject(params, true);
		if (!filterData.isEmpty()) {
			JDSFilterBuilder builder = new JDSFilterBuilder(filterData);
			ret.append((ret.indexOf("?") > 0) ? "&filter=" : "?filter=");
			ret.append(builder.build(params));
		}
		
		// include &order attribute (either from the specified sort() or from a SortParam)
		Map<String, Integer> sortData = sort().getSortObject();
		if (!sortData.isEmpty()) {
			ret.append((ret.indexOf("?") > 0) ? "&order=" : "?order=");
			for (String key : sortData.keySet()) {
				ret.append(key);
				if (sortData.get(key) == -1) ret.append(" DESC");
			}
		} else if (params.containsKey("sort.ORDER_BY")) {
			ret.append((ret.indexOf("?") > 0) ? "&order=" : "?order=");
			ret.append(params.get("sort.ORDER_BY"));
		}
		
		// ensure start+limit attributes exist
		ret.append((ret.indexOf("?") > 0) ? "&" : "?");
		ret.append(String.format("start=%d&limit=%d", start, count));
		return ret.toString();
	}
	
	/**
	 * Compiles the query criteria down into specific search/filter values.  Substitutes
	 * any parameter references with appropriate values from the specified parameters.
	 * 
	 * This should be thread safe.
	 * 
	 * @param params  Parameters to substitute where necessary, may be null if there are no parameters to substitute.
	 */
	public Map<String, Object> getQueryObject(Map<String, Object> params) {
		return getQueryObject(params, false);
	}
	
	public Map<String, Object> getQueryObject(Map<String, Object> params, boolean serverOnly) {
		Map<String, Object> ret = new HashMap<String,Object>();
		for (String k : criteria.keySet()) {
			QueryDefCriteria c = criteria.get(k);
			if (c.isServerSide() != serverOnly) {
				continue;
			}
			Map<String, Object> data = c.buildCriteriaObject(params);
			if (data != null) {
				ret.putAll(data);
			}

		}
		return ret;
	}
	
	public class QuerySort {
		private Map<String, Integer> fieldSpec = new LinkedHashMap<String, Integer>();

		public QuerySort asc(String key) {
			fieldSpec.put(key, 1);
			return this;
		}
		
		public QuerySort desc(String key) {
			fieldSpec.put(key, -1);
			return this;
		}
		
		public Map<String, Integer> getSortObject() {
			return fieldSpec;
		}
	}
	
	public class QueryFields {
		private Map<String, Integer> fields = new HashMap<String, Integer>();
		private Map<String, String> aliases = new HashMap<String, String>();
		private Map<String, QueryFieldTransformer> transformers = new HashMap<String, QueryFieldTransformer>();
		private boolean defaultInclude = true;
		
		// builder functions --------------------------------------------
		
		public QueryFields include(String... keys) {
			defaultInclude = false;
			for (String key : keys) {
				fields.put(key, 1);
			}
			return this;
		}
		
		public QueryFields exclude(String... keys) {
			defaultInclude = true;
			for (String key : keys) {
				fields.put(key, 0);
			}
			return this;
		}
		
		public QueryFields alias(String fromKey, String toKey) {
			aliases.put(fromKey, toKey);
			return this;
		}
		
		public QueryFields transform(String field, QueryFieldTransformer transformer) {
			transformers.put(field, transformer);
			return this;
		}
		
		// getter/business logic functions ------------------------------
		
		public boolean isIncluded(String field) {
			Integer x = fields.get(field);
			if (x == null) {
				return defaultInclude;
			}
			return (x == 1);
		}
	}
	
	public static abstract class QueryFieldTransformer {
		public static class HTMLEscapeTransformer extends QueryFieldTransformer {
			@Override
			public Object transform(String field, Object value) {
				return HtmlUtils.htmlEscape((value != null) ? value.toString() : null);
			}
		}
		
		public static class ReplaceTransformer extends QueryFieldTransformer {
			private String with;
			private String replace;
			public ReplaceTransformer(String replace, String with) {
				this.replace = replace;
				this.with = with;
			}
			
			@Override
			public Object transform(String field, Object value) {
				if (value != null) {
					return value.toString().replace(this.replace, this.with);
				}
				return null;
			}
		}
		
		
		public abstract Object transform(String field, Object value);
	}
	
	/**
	 * Small helper function so this functionality doesn't need to be duplicated in the ViewDef.query and DAO.finder
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public BasicDBObject buildMongoQuery(Map<String, Object> params) {
		// build the mongo query: named index + additional criteria + pagination
		BasicDBObject q = new BasicDBObject(this.getQueryObject(params));
		
		// Need to process mongo queries a bit more (idx. prefix and convert to $elemMatch)
		QueryDefCriteria idxcrit = getIndexCriteria();
		if (idxcrit != null) {
			Map<String, Object> idxdata = idxcrit.buildCriteriaObject(params);
			if (idxdata != null) {
				Object idxval = idxdata.get(idxcrit.getKey());
				Criteria c = Criteria.where("idx." + idxcrit.getKey());
				if (idxval instanceof Map) {
					// TODO: this needs to be converted to an $elemMatch mongo operator
					c.gte(((Map) idxval).get("$gte")).lte(((Map) idxval).get("$lte"));
				} else {
					c.is(idxval);
				}
				q.putAll(c.getCriteriaObject());
			}
		}
		return q;
	}
}
