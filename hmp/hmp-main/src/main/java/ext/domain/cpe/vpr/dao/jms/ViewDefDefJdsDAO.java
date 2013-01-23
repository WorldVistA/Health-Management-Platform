package org.osehra.cpe.vpr.dao.jms;

import org.osehra.cpe.param.ParamService;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.osehra.cpe.vpr.pom.jds.JdsDaoSupport;
import org.osehra.cpe.vpr.queryeng.dynamic.IViewDefDefDAO;
import org.osehra.cpe.vpr.queryeng.dynamic.ViewDefDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

public class ViewDefDefJdsDAO extends JdsDaoSupport implements IViewDefDefDAO {

    @Autowired
    ParamService paramService;

	TreeSet<ViewDefDef> myDefs = new TreeSet<ViewDefDef>();
	
	@Override
	public void save(ViewDefDef obj) {
		obj.prepareForBjw();
		myDefs.add(obj);
		paramService.setUserParam("VPR VDD", obj.getName(), POMUtils.toJSON(obj));
	}

	@Override
	public ViewDefDef findByName(String name) {
		ViewDefDef rslt = null;
		for(ViewDefDef vdd: myDefs) {
			if(vdd.getName().equals(name)) {
				rslt = vdd;
			}
		}
		if(rslt==null) {
			// Try to find in paramService;
			String paramVal = paramService.getUserParam("VPR VDD", name);
			if(paramVal!=null) {
				Map<String, Object> mp = POMUtils.parseJSONtoMap(paramVal);
				try {
					rslt = new ViewDefDef(mp);
					rslt.setBjw((ArrayList<String>)mp.get("bjw"));
					rslt.restoreFromBjw();
					myDefs.add(rslt);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return rslt;
	}

	@Override
	public List<ViewDefDef> findAll() {
		List<String> ids = paramService.getUserParamInstanceIDs("VPR VDD");
		for(String id: ids) {
			ViewDefDef gd = findByName(id);
			myDefs.add(gd);
		}
		ViewDefDef[] rslt = new ViewDefDef[0];
		return Arrays.asList(myDefs.toArray(rslt));
	}

	@Override
	public void delete(ViewDefDef obj) {
		paramService.clearUserParam("VPR VDD", obj.getName());
		myDefs.remove(obj);
	}

}
