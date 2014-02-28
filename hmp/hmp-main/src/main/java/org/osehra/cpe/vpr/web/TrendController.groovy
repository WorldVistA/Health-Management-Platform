package org.osehra.cpe.vpr.web

import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.datetime.Precision
import org.osehra.cpe.feed.atom.Link
import org.osehra.cpe.jsonc.JsonCCollection
import org.osehra.cpe.vpr.NotFoundException
import org.osehra.cpe.vpr.Result
import org.osehra.cpe.vpr.UidUtils
import org.osehra.cpe.vpr.VitalSign
import org.osehra.cpe.vpr.mapping.ILinkService
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO
import org.osehra.cpe.vpr.pom.IPatientObject
import org.osehra.cpe.vpr.queryeng.query.QueryDef
import org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory
import org.osehra.cpe.vpr.ws.link.LinkRelation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView

//@RequestMapping(value = ["/trend/**", "/vpr/trend/**"])
@Controller
public class TrendController {
	public final static String LAB_INDEX = "lab-qualified-name/summary"
	public final static String VITAL_INDEX = "vs-qualified-name/summary"

	@Autowired
	ILinkService linkService;

	@Autowired
	IGenericPatientObjectDAO genericPatientObjectDao;

	@RequestMapping(value = "/vpr/trend/**", method= RequestMethod.GET, params="format=xml")
	String renderXml(HttpServletRequest request) {
		Link link = createLink(request);
		return "redirect:"+link.href;
	}

	@RequestMapping(value = "/vpr/trend/**", method= RequestMethod.GET, params="format=json")
	ModelAndView renderJson(HttpServletRequest request) {

		String uid = getUidFromUrl(request.getRequestURI())
		Class clazz = UidUtils.getDomainClassByUid(uid)
		def item = genericPatientObjectDao.findByUID(clazz, uid)
		String pid = ((IPatientObject)item).pid

		QueryDef qryDef = new QueryDef();
		qryDef.namedIndexRange(getIndex(item),item.qualifiedName, null);
		List<IPatientObject> items = genericPatientObjectDao.findAllByQuery(clazz, qryDef, ["pid": pid]);

		List data = createTrendData(items)
		JsonCCollection cr = JsonCCollection.create(data)
		cr.put('name', item?.qualifiedName)
		cr.put('type', 'line')
		cr.selfLink = createLink(request)?.href
		return contentNegotiatingModelAndView(cr);
	}

	protected String getIndex(IPatientObject item) {
		String index
		if(item instanceof Result){
			index = LAB_INDEX
		}else if(item instanceof VitalSign){
			index = VITAL_INDEX
		}else{
			throw new IllegalArgumentException('Trend  type is invalid. Valid types: result, vitalSign')
		}
		return index
	}

	protected List createTrendData(List<IPatientObject> items){
		List chartData = []
		for ( IPatientObject item : items) {
			Long jsDate = pitToJsDate(item?.observed)
			Float result = (item?.result.isNumber())?item.result as Float:null
			if(jsDate && result){
				def map = [x:jsDate, y:result]
				if(item.interpretationName){
					map.put('interpreted', item.interpretationName)
				}
				chartData.add(map)
			}
		}
		return chartData
	}

	protected Long pitToJsDate(PointInTime pit) {
		if(pit?.precision < Precision.DATE) return null
		return pit.promote().getCenter().toLocalDateTime().toDateTime().millis;
	}

	protected String getUidFromUrl(String uri){
		String url = URLDecoder.decode(uri);
		String uid = url.substring(url.indexOf("urn:"));
		if (!uid) {
			throw new BadRequestException("'uid' parameter is required");
		}
		return uid
	}

//	@RequestMapping(value = "/vpr/trend/**", method= RequestMethod.GET, params="format!=extjs")
//	String renderHtml(HttpServletRequest request) {
//		Link link = createLink(request);
//		return "redirect:"+link.href;
//	}

	@RequestMapping(value = "/vpr/trend/**", method= RequestMethod.GET, params="format=extjs")
	@ResponseBody
	String renderExtJs(@RequestParam("format") String format, HttpServletRequest request) {
		Link link = createLink(request);
		if (link.href.contains('?')) {
			link.href = link.href + '&format=' + format
		} else {
			link.href = link.href + '?format=' + format
		}
		System.out.println(link.href);
		return "{ xtype: 'trendpanel', url: '" + link.href + "' }";
	}

	private Link createLink(HttpServletRequest request) {
		String uid = getUidFromUrl(request.getRequestURI())
		def item = genericPatientObjectDao.findByUID(UidUtils.getDomainClassByUid(uid), uid);

		if (!item) throw new UidNotFoundException(UidUtils.getDomainClassByUid(uid), uid)

		List<Link> links = linkService.getLinks(item)

		Link link = links.find { it.rel == LinkRelation.TREND.toString() }
		if (!link) throw new NotFoundException("No trend found for item with uid=" + uid);
		return link;
	}
}
