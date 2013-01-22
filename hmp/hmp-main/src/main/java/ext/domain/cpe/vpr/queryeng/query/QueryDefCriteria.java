package EXT.DOMAIN.cpe.vpr.queryeng.query;

import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryDefCriteria {
	private static final Object NOT_SET = new Object();

	private boolean serverSide = false;
	private String key;
	private Object isValue = NOT_SET;
	private List<QueryDefCriteria> criteriaChain;
	private LinkedHashMap<String, Object> criteria = new LinkedHashMap<String, Object>();
	
	public QueryDefCriteria() {
		this.criteriaChain = new ArrayList<QueryDefCriteria>();
	}
	
	public QueryDefCriteria(String key) {
		this(key, false);
	}

	public QueryDefCriteria(String key, boolean serverSide) {
		this.criteriaChain = new ArrayList<QueryDefCriteria>();
		this.criteriaChain.add(this);
		this.key = key;
		this.serverSide = serverSide;
	}

	protected QueryDefCriteria(List<QueryDefCriteria> criteriaChain, String key) {
		this.criteriaChain = criteriaChain;
		this.criteriaChain.add(this);
		this.key = key;
	}

	public static QueryDefCriteria where(String key) {
		return new QueryDefCriteria(key);
	}
	
	public static QueryDefCriteria client(String key) {
		return new QueryDefCriteria(key, false);
	}
	
	public static QueryDefCriteria server(String key) {
		return new QueryDefCriteria(key, true);
	}
	
	public boolean isServerSide() {
		return this.serverSide;
	}
	
	public String getKey() {
		return this.key;
	}
	
	// primary query operators --------------------------------------------------

	public QueryDefCriteria and(String key) {
		return new QueryDefCriteria(this.criteriaChain,key);
	}

	public QueryDefCriteria is(Object o) {
		if (isValue != NOT_SET) {
			String msg = "Multiple 'is' values declared. You need to use 'and' with multiple criteria";
			throw new IllegalArgumentException(msg);
		} else if (this.criteria.size() > 0) {
			Object[] critAry = this.criteria.keySet().toArray();
			if ("$not".equals(critAry[this.criteria.size() - 1])) {
				String msg = "Invalid query: 'not' can't be used with 'is' - use 'ne' instead.";
				throw new IllegalArgumentException(msg);
			}
		}
		this.isValue = ParamRef.valueOf(o);
		return this;
	}
	
	private QueryDefCriteria addCriteria(String operator, Object val) {
		criteria.put(operator, ParamRef.valueOf(val));
		return this;
	}

	public QueryDefCriteria ne(Object o) {
		return nin(o);
	}

	public QueryDefCriteria lt(Object o) {
		return addCriteria("$lt", o);
	}

	public QueryDefCriteria lte(Object o) {
		return addCriteria("$lte", o);
	}

	public QueryDefCriteria gt(Object o) {
		return addCriteria("$gt", o);
	}

	public QueryDefCriteria gte(Object o) {
		return addCriteria("$gte", o);
	}
	
	public QueryDefCriteria between(Object o1, Object o2) {
		addCriteria("$gte", o1);
		return addCriteria("$lte", o2);
	}
	
	public QueryDefCriteria in(Object o) {
		return addCriteria("$in", o);
	}

	public QueryDefCriteria in(Object... o) {
		if (o.length > 1 && o[1] instanceof Collection) {
			String msg = "You can only pass in one argument of type "; 
			throw new IllegalArgumentException(msg + o[1].getClass().getName());
		}
		return in(Arrays.asList(o));
	}

	public QueryDefCriteria in(Collection<?> c) {
		return addCriteria("$in", c);
	}
	
	public QueryDefCriteria nin(Object... o) {
		if (o.length > 1 && o[1] instanceof Collection) {
			String msg = "You can only pass in one argument of type "; 
			throw new IllegalArgumentException(msg + o[1].getClass().getName());
		}
		return nin(Arrays.asList(o));
	}
	
	public QueryDefCriteria nin(Collection<?> o) {
		return addCriteria("$nin", o);
	}
	
	/* Not implemented yet in JDS/QueryMatcher
	public QueryDefCriteria nin(Object... o) {
		return nin(Arrays.asList(o));
	}

	public QueryDefCriteria not() {
		return addCriteria("$not", null);
	}
	*/
	
	// operator groups ---------------------------------------------------------

	public QueryDefCriteria orOperator(QueryDefCriteria... criteria) {
		List<Map<String,Object>> bsonList = createCriteriaList(criteria);
		criteriaChain.add(new QueryDefCriteria("$or").is(bsonList));
		return this;
	}

	public QueryDefCriteria norOperator(QueryDefCriteria... criteria) {
		List<Map<String,Object>> bsonList = createCriteriaList(criteria);
		criteriaChain.add(new QueryDefCriteria("$nor").is(bsonList));
		return this;
	}

	public QueryDefCriteria andOperator(QueryDefCriteria... criteria) {
		List<Map<String,Object>> bsonList = createCriteriaList(criteria);
		criteriaChain.add(new QueryDefCriteria("$and").is(bsonList));
		return this;
	}
	
	// build/construct/compile criteria -----------------------------------------
	protected static Object interpolateValues(Object val, Map<String, Object> params) {
		if (val instanceof ParamRef) {
			ParamRef pval = (ParamRef) val;
			Object ret = params.get(pval.getKey());
			
			if (pval.filterOut(ret)) {
				// dont include null/empty values that are conditional
				return null;
			} else {
				return ret;
			}
		} else if(val instanceof SpelRef) {
			SpelRef sr = (SpelRef)val;
			if (sr.filterOut(params)) {
				return null;
			} else {
				return sr.evaluateWithParams(params);
			}
		} else if (val instanceof Map) {
			Map map = (Map) val;
			Map ret = new HashMap();
			for (Object mapkey : map.keySet()) {
				Object mapval = map.get(mapkey);
				mapval = interpolateValues(mapval, params);
				if (mapval != null) {
					ret.put(mapkey, mapval);
				}
			}
			return ((ret.isEmpty()) ? null : ret);
		} else if (val instanceof List) {
			List newList = new ArrayList();
			for (Object o : (List) val) {
				Object ret = interpolateValues(o, params);
				if (ret != null) {
					newList.add(ret);
				}
			}
			return ((newList.isEmpty()) ? null : newList);
		} else {
			return val;
		}
	}
	
	public Map<String,Object> buildCriteriaObject(Map<String, Object> params) {
		Map<String,Object> ret = new HashMap<String, Object>();
		if (this.criteriaChain.size() == 1) {
			ret.putAll(criteriaChain.get(0).getSingleCriteriaObject());
		} else {
			for (QueryDefCriteria c : this.criteriaChain) {
				Map<String,Object> dbo = c.getSingleCriteriaObject();
				for (String k : dbo.keySet()) {
					setValue(ret, k, dbo.get(k));
				}
			}
		}
		
		if (params != null) {
			ret = (Map<String, Object>) interpolateValues(ret, params);
		}
		return ret;
	}

	protected  Map<String,Object> getSingleCriteriaObject() {
		Map<String,Object> dbo = new HashMap<String,Object>();
		boolean not = false;
		for (String k : this.criteria.keySet()) {
			if (not) {
				Map<String,Object> notDbo = new HashMap<String,Object>();
				notDbo.put(k, this.criteria.get(k));
				dbo.put("$not", notDbo);
				not = false;
			} else {
				if ("$not".equals(k)) {
					not = true;
				} else {
					dbo.put(k, this.criteria.get(k));
				}
			}
		}
		Map<String,Object> queryCriteria = new HashMap<String,Object>();
		if (isValue != NOT_SET) {
			queryCriteria.put(this.key, this.isValue);
			queryCriteria.putAll(dbo);
		} else {
			queryCriteria.put(this.key, dbo);
		}
		return queryCriteria;
	}

	private List<Map<String,Object>> createCriteriaList(QueryDefCriteria[] criteria) {
		ArrayList<Map<String,Object>> bsonList = new ArrayList<Map<String,Object>>();
		for (QueryDefCriteria c : criteria) {
			bsonList.add(c.buildCriteriaObject(null));
		}
		return bsonList;
	}

	private void setValue(Map<String,Object> dbo, String key, Object value) {
		Object existing = dbo.get(key);
		if (existing == null) {
			dbo.put(key, value);
		} else {
			throw new IllegalStateException(
					"Due to limitations of the com.mongodb.BasicDBObject, "
							+ "you can't add a second '" + key
							+ "' expression specified as '" + key + " : "
							+ value + "'. " + "Criteria already contains '"
							+ key + " : " + existing + "'.");
		}
	}
	
	// TODO: probably don't really need this right now.
	public class StrEval {
		private String str;

		public StrEval(String str) {
			this.str = str;
		}
		
		public Object getValue(RenderTask renderer) {
			//return evalQueryString(renderer, str);
			return null;
		}
	}
	
	public static interface SpelRef {
		boolean filterOut(Map<String, Object> params);
		Object evaluateWithParams(Map<String, Object> params);
	}
	
	public static class ParamRef {
		private String key;
		private boolean conditional;

		public ParamRef(String key, boolean conditional) {
			this.key = key;
			this.conditional = conditional;
		}
		
		public boolean filterOut(Object val) {
			boolean isEmpty = val == null;
			if (!isEmpty && val instanceof Iterable<?>) {
				isEmpty = !((Iterable<?>) val).iterator().hasNext();
			}
			
			return (isEmpty && isConditional());
		}
		
		public boolean isConditional() {
			return this.conditional;
		}
		
		public String getKey() {
			return key;
		}
		
		public Object getValue(Map<String,Object> params) {
			if (params != null && params.containsKey(key)) {
				return params.get(key);
			} 
			throw new IllegalArgumentException("The query criteria value :" + key + " could not be resolved");
		}
		
		public static Object valueOf(Object val) {
			// replace :string and ?:string with param refs
			if (val instanceof String) {
				String valstr = val.toString();
				if (valstr.startsWith("?:")) {
					val = new ParamRef(valstr.substring(2), true);
				} else if (valstr.startsWith(":")) {
					val = new ParamRef(valstr.substring(1), false);
				}
			}

			return val;
		}
		
		public static ParamRef valueOf(String key, boolean conditional) {
			return new ParamRef(key, conditional);
		}
	}
	
}
