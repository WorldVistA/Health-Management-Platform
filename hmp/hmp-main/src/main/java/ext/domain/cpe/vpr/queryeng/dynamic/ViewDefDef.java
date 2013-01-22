package EXT.DOMAIN.cpe.vpr.queryeng.dynamic;

import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;
import EXT.DOMAIN.cpe.vpr.pom.POMUtils;
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns.ViewDefDefColDef;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

public final class ViewDefDef extends AbstractPOMObject implements Comparable<ViewDefDef> {
	public ViewDefDef(Map<String, Object> vals) {
		super(vals);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrimaryViewDefClassName() {
		return primaryViewDefClassName;
	}
	public void setPrimaryViewDefClassName(String primaryViewDefClassName) {
		this.primaryViewDefClassName = primaryViewDefClassName;
	}

	@JsonIgnore
	public TreeSet<ViewDefDefColDef> getCols() {
		return cols;
	}
	@JsonIgnore
	public void addColumn(ViewDefDefColDef col) {
		col.sequence = (cols.size()==0?1:cols.last().sequence+1);
		cols.add(col);
	}
	
	@JsonIgnore
	public ViewDefDefColDef getColBySequence(Integer seq) {
		ViewDefDefColDef rslt = null;
		for(ViewDefDefColDef cd: cols) {
			if(cd.getSequence().equals(seq)) {
				rslt = cd;
			}
		}
		return rslt;
	}
	@JsonIgnore
	public ViewDefDefColDef getColBySequence(String seq) {
		Integer seqi = Integer.parseInt(seq);
		ViewDefDefColDef rslt = null;
		for(ViewDefDefColDef cd: cols) {
			if(cd.getSequence().equals(seqi)) {
				rslt = cd;
			}
		}
		return rslt;
	}

	@JsonIgnore
	public void setCols(TreeSet<ViewDefDefColDef> cols) {
		this.cols = cols;
	}
	String name;
	String primaryViewDefClassName;

	@JsonIgnore
	TreeSet<ViewDefDefColDef> cols = new TreeSet<ViewDefDefColDef>(); 
	
	ArrayList<String> bjw = new ArrayList<String>();

	public ArrayList<String> getBjw() {
		return bjw;
	}
	
	public void setBjw(
			ArrayList<String> bjw) {
		this.bjw = bjw;
	}
	
	@JsonIgnore
	public void prepareForBjw() {
		bjw = new ArrayList<String>();
		for(ViewDefDefColDef vddcd: cols) {
			Map<String, Object> bs = vddcd.getData();
			bs.put("@class", vddcd.getClass().getName());
			bjw.add(POMUtils.toJSON(bs));
		}
	}
	@JsonIgnore
	public void restoreFromBjw() {
		cols = new TreeSet<ViewDefDefColDef>();
		for(String s: bjw) {
			Map<String, Object> mp = POMUtils.parseJSONtoMap(s);
			String className = mp.get("@class").toString();
			if(className!=null) {
				try {
					ViewDefDefColDef col = (ViewDefDefColDef)Class.forName(className).getConstructor(Map.class).newInstance(mp);
					if(mp.get("sequence")!=null) {
						col.sequence = Integer.parseInt(mp.get("sequence").toString());
					}
					cols.add(col);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int compareTo(ViewDefDef o) {
		return this.getName().compareTo(o.getName());
	}
}
