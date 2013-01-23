package org.osehra.cpe.vpr.queryeng;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A table is just basically a list of maps, or a tuple store in more academic terminology.
 * This one is build to look like a traditional relational table, with rows, columns and cells.
 * 
 * Additionally, it enforces the notion of a Primary Key (PK).  A column who's value is required to exist (and be
 * unique) for each row.  Currently primary key values must be strings.
 * 
 * Rows/Cells can be retrieved either by primary key value (getRow(String)), or by row index (getRow(int)). 
 * The first row is always index 0. Data can be added row at a time (via addRow(...)) or cell by cell (via setCell(...)).
 * 
 * Unlike a traditional DB table, not all rows are required to have the same columns.  Nor is there any restriction on the
 * number of columns or inherit performance issues with having a large number of columns.
 * 
 * Table implements the collections interface, giving it all sorts of functionality like addAll(), remove(), etc.
 *  
 * TODO: Allow any type of object to be a primary key value, don't force them to be strings.
 * TODO: Consider creating a new Row class, that extends Map and acts like a dictionary class (forces string key).
 * TODO: Need to rename getColumn*() to getField*()
 */
public class Table extends AbstractCollection<Map<String,Object>> {
	private String pk;
	private Set<String> fieldidx;
	private Map<String,Map<String,Object>> pkidx;
	private List<Map<String,Object>> data;
	
	public Table(String pk) {
		this.pk = pk;
		init();
	}
	
	protected void init() {
		this.data = new ArrayList<Map<String,Object>>();
		this.pkidx = new LinkedHashMap<String,Map<String,Object>>();
		this.fieldidx = new HashSet<String>();
		
		// primary key is the first column in the index
		this.fieldidx.add(getPK());
	}
	
	public String getPK() {
		return this.pk;
	}
	
	///////////////////// ADD/SET DATA METHODS ///////////////////////////
	
	/**
	 * Adds/Appends fields to a row designated by the primary key field in the map.
	 * 
	 * If no row exists yet, then a new one is created, otherwise any new values are appended to the row.
	 * 
	 * If existing values are there, they are over-written by the new values.
	 * 
	 * Throws an error if the map is missing a PK value.
	 */
	protected boolean addRow(Map<String,Object> row) {
		return addRow(row, -1);
	}
	
	protected boolean addRow(Map<String,Object> row, int idx) {
		if (row == null) {
			return false;
		}
		Object pkval = row.get(getPK());
		if (pkval == null ) {
			throw new RuntimeException("Missing PK value.");
		} 
		String pkstr = pkval.toString().trim();
		if (pkstr.length() == 0) {
			throw new RuntimeException("Empty PK value.");
		}
		
		return appendRow(pkstr, row, idx);
	}
	
	public boolean add(Map<String, Object> e, int idx) {
		return addRow(e, idx);
	}
	
	public boolean appendVal(String pkval, String key, Object val) {
		return appendRow(pkval, buildRow(key, val));
	}
	
	public boolean appendRow(String pkval, Map<String, Object> row) {
		return appendRow(pkval, row, -1);
	}
	
	/**
	 * Append the row values to an existing row, anything not specified in row is not changed, anything that is 
	 * will be overwritten.  If the row does not exist, then it will be created
	 * 
	 * This is a very high traffic method, essentially all add/append/set operations run though here.  So this is the
	 * main place where synchronization is needed
	 * 
	 * @param pkval
	 * @param row
	 * @param rowidx
	 * @return
	 */
	public boolean appendRow(String pkval, Map<String, Object> row, int rowidx) {
		return appendRow(pkval, row, rowidx, false);
	}
		
	private boolean appendRow(String pkval, Map<String, Object> row, int rowidx, boolean replace) {
		if (pkval == null || data == null) return false;
		if (row.containsKey(getPK()) && !row.get(getPK()).equals(pkval)) {
			throw new IllegalArgumentException("row PK value and data PK value are incompatible");
		}
		
		synchronized (data) {
			// update column list
			fieldidx.addAll(row.keySet());
			if (pkidx.containsKey(pkval) && !replace) {
				// row already exists, append
				pkidx.get(pkval).putAll(row);
			} else {
				// new row, copy to fresh map in case row is immutible
				row = new HashMap<String, Object>(row);
				pkidx.put(pkval, row);
				
				// if row index exists, store the row at the specified index, otherwise append
				if (rowidx >= 0) {
					data.add(rowidx, row);
					return true;
				}
				return data.add(row);
			}
		}
		return true;
	}
	
	public boolean appendRowIdx(int rowidx, Map<String, Object> data) {
		Map<String, Object> row = getRowIdx(rowidx);
		Object pkval = (row != null) ? row.get(getPK()) : null;
		if (pkval == null) return false;
		return appendRow(pkval.toString(), data, rowidx);
	}
	
	public boolean replaceRow(String pkval, Map<String, Object> row) {
		return appendRow(pkval, row, -1, true);
	}
	
	/**
	 * Clears the row with the specified key, leaves the row in place in its original order,
	 * but clears all the values except for the primary key value.
	 */
	public void clearRow(String pkval) {
		if (!pkidx.containsKey(pkval)) {
			return;
		}
		
		Map<String, Object> row = pkidx.get(pkval);
		row.clear();
		row.put(getPK(), pkval);
	}
	
	public void setCell(String pk, String key, Object val) {
		appendRow(pk, buildRow(key, val), -1);
	}

	/**
	 * Returns a list of all the unique columns in this table.
	 * @deprecated Use getField() instead.  Its identical.
	 */
	@Deprecated
	public Set<String> getColumns() {
		return getFields();
	}
	
	/**
	 * Returns a list of all the unique fields in this table (across all rows)
	 */
	public Set<String> getFields() {
		return fieldidx;
	}
	
	/**
	 * @deprecated Use getFieldValues(String) instead, its identical.
	 */
	@Deprecated
	public List<Object> getColumnValues(String colkey) {
		return getFieldValues(colkey);
	}
	
	/**
	 * Returns a list of all the row values for a field.
	 */
	public List<Object> getFieldValues(String field) {
		ArrayList<Object> ret = new ArrayList<Object>();
		for (String pkval : pkidx.keySet()) {
			Object val = getCell(pkval, field);
			if (val != null) {
				ret.add(val);
			}
		}
		return ret;
	}
	
	/**
	 * @return Returns a list of unique values in the specified column.
	 */
	public Set<Object> getUniqueValues(String colkey) {
		HashSet<Object> ret = new HashSet<Object>();
		ret.addAll(getColumnValues(colkey));
		return ret;
	}
	
	///////////////////////// GET/QUERY METHODS /////////////////////////
	
	/**
	 * Returns the primary key values in the order they were added.  Same as getColumnValues(getPK())
	 * @return
	 */
	public Set<String> getPKValues() {
		return pkidx.keySet();
	}
	
	public Map<String, Object> getRowIdx(int idx) {
		if (idx >= data.size() || idx < 0) {
			return null;
		}
		return Collections.unmodifiableMap(data.get(idx));
	}
	
	public int indexOf(Map<String, Object> row) {
		return data.indexOf(row);
	}
	
	public Map<String, Object> getRow(String pkval) {
		Map<String, Object> ret = pkidx.get(pkval);
		if (ret != null) {
			return Collections.unmodifiableMap(ret);
		}
		return null;
	}
	
	public Collection<Map<String, Object>> getRows() {
		return Collections.unmodifiableCollection(pkidx.values());
	}

	public Object getCellIdx(int idx, String colkey) {
		Map<String, Object> ret = getRowIdx(idx);
		if (ret == null) {
			return null;
		}
		return ret.get(colkey);
	}

	public Object getCell(String pkval, String colkey) {
		Map<String,Object> row = getRow(pkval);
		if (row == null) {
			return null;
		}
		return row.get(colkey);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int i=0;
		for (Map<String,Object> row : getRows()) {
			sb.append("\n" + ++i + "\t");
			for (String key : row.keySet()) {
				sb.append(key + ":" + row.get(key) + ", ");
			}
		}
		return sb.toString();			
	}

	//////// COLLECTIONS INTERFACE IMPLEMENTATION /////////////////
	
	@Override
	public Iterator<Map<String, Object>> iterator() {
		return pkidx.values().iterator();
	}
	
	@Override
	public boolean add(Map<String, Object> e) {
		return addRow(e);
	}
	
	public boolean remove(int idx) {
		return super.remove(data.remove(idx));
	}
	
	@Override
	public boolean remove(Object o) {
		data.remove(o);
		return super.remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		data.removeAll(c);
		return super.removeAll(c);
	}
	
	@Override
	public int size() {
		return pkidx.size();
	}
	
	@Override
	public void clear() {
		init();
	}
	
	/////// STATIC HELPER METHODS //////////////////////
	
	
	/**
	 * Quick way to build a row (Map), must be an even number of values, (key1=val2,key2=val2,etc.)
	 */
	public static Map<String,Object> buildRow(Object... vals) {
		HashMap<String,Object> row = new HashMap<String,Object>();
		for (int i = 0; i < vals.length; i++) {
			String key = vals[i++].toString();
			Object val = (i < vals.length) ? vals[i] : null;
			row.put(key, val);
		}
		return row;
	}

	/**
	 * TODO: The idea here is to provide a custom interator that will sort by a field (other than the PK)
	 * 
	 * TODO: Maybe multiple fields?
	 * @author brian
	 *
	 */
	private class SortedTableIterator implements Iterator<Map<String, Object>> {
		public SortedTableIterator(Table table, String sortField) {
//			List<String> ret = getFieldValues(sortField);
//			Collections.sort(ret);
		}
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Map<String, Object> next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
