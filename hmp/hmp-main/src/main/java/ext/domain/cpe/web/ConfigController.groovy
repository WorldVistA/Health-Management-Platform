package EXT.DOMAIN.cpe.web

import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView
import com.fasterxml.jackson.databind.ObjectMapper
import EXT.DOMAIN.cpe.jsonc.JsonCResponse
import EXT.DOMAIN.cpe.param.ParamService
import EXT.DOMAIN.cpe.vpr.IAppService
import EXT.DOMAIN.cpe.vpr.pom.POMUtils
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.IViewDefDefDAO
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.ViewDefDef
import EXT.DOMAIN.cpe.vpr.queryeng.dynamic.columns.ViewDefDefColDef
import EXT.DOMAIN.cpe.vpr.service.IPatientDomainService

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/config/**")
class ConfigController {
		@Autowired
		GrailsApplication grailsApplication
	
		@Autowired
		IPatientDomainService patientDomainService
		
		@Autowired
		ParamService paramService
	
		@Autowired
		IAppService appService
		
		@Autowired
		IViewDefDefDAO vddDAO
		
		@Autowired
		ApplicationContext ctx
		
		@Autowired
		ObjectMapper mapper
		
		@RequestMapping(method = RequestMethod.GET)
		ModelAndView index() {
			return new ModelAndView("/config/index", [:])
		}
		
		@RequestMapping(value="panels", method = RequestMethod.GET)
		public ModelAndView panels(HttpServletRequest request, HttpServletResponse response)
		{
			// build up the param set
			def params = new GrailsParameterMap(request);
			
			// Return JSON of panels;
			List<ViewDefDef> rslt = vddDAO.findAll();
			return contentNegotiatingModelAndView(rslt);
		}
		
		static LinkedHashMap samplePanels = [contentType: "application/json", response: [[id: 1, name: 'Test 1'],[id: 2, name: 'Test 2']]]
		static int lastId = 2;
		
		@RequestMapping(value="addPanel", method = RequestMethod.POST)
		public void addPanel(@RequestParam(required = true) String name, @RequestParam(required = true) String primaryViewDefClassName, HttpServletRequest request, HttpServletResponse response)
		{
			if(name)
			{
				ViewDefDef vdef = new ViewDefDef();
				vdef.setName(name);
				vdef.setPrimaryViewDefClassName(primaryViewDefClassName);
				vddDAO.save(vdef);
			}
		}
	
		@RequestMapping(value="dropPanel", method = RequestMethod.POST)
		public void dropPanel(@RequestParam(required = true) String panelName, HttpServletRequest request, HttpServletResponse response)
		{
			if(panelName!=null && vddDAO.findByName(panelName)) {
				vddDAO.delete(vddDAO.findByName(panelName));
			}
		}
	
		@RequestMapping(value=["panelColumns","panelColumns/{panelId}"], method=RequestMethod.GET)
		public ModelAndView panelColumns(@RequestParam(required=true) String panelName, HttpServletRequest request, HttpServletResponse response)
		{
			ViewDefDef vdef = vddDAO.findByName(panelName);
			ArrayList<Map<String, Object>> rslt = new ArrayList<Map<String, Object>>();
			for(ViewDefDefColDef cdef: vdef.getCols()) {
				rslt.add(cdef.getData());
			}
			return contentNegotiatingModelAndView(rslt);
		}
		
		@RequestMapping(value=['dropColumn'], method=RequestMethod.POST)
		public void dropColumn(@RequestParam(required=true) String colName, @RequestParam(required=true) String panelName, HttpServletRequest request, HttpServletResponse response) {
			
			// build up the param set
			ViewDefDef vdef = vddDAO.findByName(panelName);
			if(vdef) {
				ArrayList<ViewDefDefColDef> dropCols = new ArrayList<ViewDefDefColDef>();
				for(ViewDefDefColDef col: vdef.cols){
					if(col.getName().equalsIgnoreCase(colName)) {
						dropCols.add(col);
					}
				}
				for(ViewDefDefColDef dcol: dropCols) {
					vdef.cols.remove(dcol);
				}
			}
			vddDAO.save(vdef);
		}
		
		@RequestMapping(value=['addColumn'], method=RequestMethod.POST)
		public void addColumn(@RequestParam(required=true) String panelName, HttpServletRequest request, HttpServletResponse response) {
			
			// build up the param set
			def params = new GrailsParameterMap(request);
			def name = params.get('panelName');
			ViewDefDef vdef = vddDAO.findByName(name);
			if(vdef) {
				def cols = params.get('columns');
				if(cols instanceof String) {
					Map<String, Object> colMap = POMUtils.parseJSONtoMap(cols);
					if(colMap.code) {
						vdef.addColumn(getViewDefDefColDef(colMap.code));
					}
				} else if(cols instanceof String[]) {
					for(String col: (String[])cols) {
						Map<String, Object> colMap = POMUtils.parseJSONtoMap(col);
						if(colMap.code) {
							vdef.addColumn(getViewDefDefColDef(colMap.code));
						}
					}
				}
			}
			vddDAO.save(vdef);
		}
		
		@RequestMapping(value=['setViewDefColumnSequence'], method=RequestMethod.POST)
		public ModelAndView setViewDefColumnSequence(
			@RequestParam(required=true) String panelName, 
			@RequestParam(required=true) String[] sequence,
			HttpServletRequest request, 
			HttpServletResponse response) {
			ViewDefDef vdef = vddDAO.findByName(panelName);
			boolean mod = false;
			if(vdef) {
				for(String s: sequence) {
					Map seq = POMUtils.parseJSONtoMap(s);
					String fldName = seq.get('fieldName');
					if(fldName) {
						ViewDefDefColDef cd = vdef.getColByName(fldName);
						if(cd && seq.get('sequence') && ! cd.sequence.equals(seq.get('sequence'))) {
							cd.sequence = seq.get('sequence');
							vdef.cols.remove(cd);
							vdef.cols.add(cd);
							mod = true;
						}
					}
				}
			}
			if(mod) {
				vddDAO.save(vdef);
			}
		}
		
		@RequestMapping(value=['setViewDefColumnProperties'], method=RequestMethod.POST)
		public ModelAndView setViewDefColumnProperties(@RequestParam(required=true) String panelName, @RequestParam(required=true) String colName, HttpServletRequest request, HttpServletResponse response) {
			ViewDefDef vdef = vddDAO.findByName(panelName);
			def parms = new GrailsParameterMap(request);
			def configProperties = parms.get("configProperties");
			def viewdefFilters = parms.get("viewdefFilters");
			def fieldName = parms.get("fieldName");
			def sequence = parms.get("sequence");
			def colId = parms.get('colId');
			if(vdef) {
				ViewDefDefColDef vdcdef = vdef.getColBySequence(sequence);
				if(vdcdef) {
					if(fieldName) {
						vdcdef.setFieldName(fieldName);
					}
					vdcdef.setConfigProperties(configProperties);
					vdcdef.setViewdefFilters(viewdefFilters);
				}
			}
			vddDAO.save(vdef);
			return contentNegotiatingModelAndView(JsonCResponse.create(request, [message: 'Column saved successfully.']));
		}
		
		@RequestMapping(value=['getColumnConfigOptions'], method=RequestMethod.GET)
		public ModelAndView getColumnConfigOptions(@RequestParam(required=true) String code, HttpServletRequest request, HttpServletResponse response) {
			def options = [:];
			ViewDefDefColDef vddcd = getViewDefDefColDef(code);
			options['viewdefFilterOptions'] = vddcd.getViewdefFilterOptions();
			options['configOptions'] = vddcd.getConfigOptions();
			options['description'] = vddcd.getDescription();
			return contentNegotiatingModelAndView(options);
		}
		
		private ViewDefDefColDef getViewDefDefColDef(String vddcd) {
			// if the view is an exact bean name
			if (ctx.containsBean(vddcd)) {
				return ctx.getBean(vddcd, ViewDefDefColDef.class);
			}
			return null;
		}
}
