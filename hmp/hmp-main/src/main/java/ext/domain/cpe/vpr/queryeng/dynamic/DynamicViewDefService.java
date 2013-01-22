package EXT.DOMAIN.cpe.vpr.queryeng.dynamic;

import EXT.DOMAIN.cpe.vpr.RosterService;
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class DynamicViewDefService implements ApplicationContextAware,
		IDynamicViewDefService {
	
	private Map<String, ViewDef> dynamicViewDefs = new HashMap<String, ViewDef>();

	@Autowired
	IPatientDAO patientDAO;
	
	@Autowired
	IViewDefDefDAO vddDAO;
	
	@Autowired
	ApplicationContext ctx;
	
	@Autowired
	RosterService rosterService;
	
	@Override
	public ViewDef getViewDefByName(String name) {
		ViewDef vd = null;//dynamicViewDefs.get(name);
		if(vd==null) {
			ViewDefDef vdd = vddDAO.findByName(name);
			if(vdd!=null) {
//				Map<String, ViewDefDefColDef> colViewDefs = new HashMap<String, ViewDefDefColDef>();
//				for(ViewDefDefColDef cdef: vdd.cols) {
//					String view = cdef.viewdefCode;
//					if (ctx.containsBean(view)) {
//						colViewDefs.put(cdef.fieldName, ctx.getBean(view, ViewDef.class));
//					}
//				}
				try {
					vd = (ViewDef) Class.forName(vdd.primaryViewDefClassName)
							.getConstructor(RosterService.class, IPatientDAO.class, TreeSet.class, ApplicationContext.class)
							.newInstance(rosterService, patientDAO, vdd.cols, ctx);
					vd.init(null);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return vd;
	}

	@Override
	public void setViewDefDef(ViewDefDef def) {
		vddDAO.save(def);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// TODO: Is the intent here for handling post-context stuff? Pre-loading viewdefs or something?
	}

}
