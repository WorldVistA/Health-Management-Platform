package org.osehra.cpe.vpr.web


import org.osehra.cpe.vista.rpc.RpcTemplate
import org.osehra.cpe.vpr.IAppService
import org.osehra.cpe.vpr.pom.IPatientDAO
import org.osehra.cpe.vpr.pom.jds.JdsTemplate
import org.osehra.cpe.vpr.queryeng.dynamic.IDynamicViewDefService
import org.osehra.cpe.vpr.queryeng.dynamic.IViewDefDefDAO;
import org.osehra.cpe.vpr.queryeng.dynamic.PatientPanelViewDef
import org.osehra.cpe.vpr.queryeng.dynamic.ViewDefDefColDef
import org.osehra.cpe.vpr.queryeng.ViewDef
import org.osehra.cpe.vpr.queryeng.dynamic.columns.ViewDefDefColDef
import org.osehra.cpe.vpr.queryeng.ViewDefRenderer
//import org.osehra.cpe.vpr.queryeng.ViewDefRollup
import org.osehra.cpe.vpr.viewdef.RenderTask
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2
import org.osehra.cpe.vpr.viewdef.ViewDefRenderer2.JSONViewRenderer2
import grails.converters.JSON

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource

import org.apache.solr.client.solrj.SolrServer
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.convert.ConversionService
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView
import org.osehra.cpe.vpr.pom.POMUtils

@Controller
public class FacilityController {
	
	@Autowired
	ApplicationContext ctx;
		
	@RequestMapping(['/facility/roomlist'])
	ModelAndView roomList(HttpServletRequest req, HttpServletResponse rsp) {
		
	}
	
}
