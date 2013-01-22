package EXT.DOMAIN.cpe.vpr.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import EXT.DOMAIN.cpe.jsonc.JsonCCollection;
import EXT.DOMAIN.cpe.vpr.queryeng.Table;
import EXT.DOMAIN.cpe.vpr.termeng.Concept;
import EXT.DOMAIN.cpe.vpr.termeng.TermEng;
import EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TermController {
	@Autowired
	private TermEng termEng;
	
	@RequestMapping(value="/term/sources")
	public ModelAndView sources() {
		return ModelAndViewFactory.contentNegotiatingModelAndView(termEng.getCodeSystemMap());
	}
	
	@RequestMapping(value="/term/search")
	public ModelAndView search(@RequestParam(value="query",required=true) String search, HttpServletRequest request) {
		List<Concept> ret = new ArrayList<Concept>();
		for (String str : termEng.search(search)) {
			ret.add(termEng.getConcept(str));
		}
		
//		JsonCCollection<String> jsonc = JsonCCollection.create(request, termEng.search(search));
		JsonCCollection<Concept> jsonc = JsonCCollection.create(request, ret);

        return ModelAndViewFactory.contentNegotiatingModelAndView(jsonc);
	}
	
	@RequestMapping(value="/term/{urn}")
	@ResponseBody
	public ModelAndView fetch(@PathVariable("urn") String urn) {
		return ModelAndViewFactory.contentNegotiatingModelAndView(termEng.getConceptData(urn));
	}
	
	@RequestMapping(value="/term/tree/{urn}")
	@ResponseBody
	public ModelAndView tree(@PathVariable("urn") String urn) {
		Concept c = termEng.getConcept(urn);
		if (c == null) return null;
		
//		List<Map> ret = new ArrayList<Map>();
//		ret.add(Table.buildRow("text", c.getDescription(), "leaf", false, "expanded", true, "children"));
		
		String ret = "{text: " + c.getDescription() + ", expanded: true, children: [	"
				+ "{ text: 'Relationships', leaf: true }, {text: 'Attrs', leaf: true}, {text:'Parents', leaf:true]} ";
		
		return ModelAndViewFactory.contentNegotiatingModelAndView(ret);
	}
	
	@RequestMapping(value = "/term/display")
	ModelAndView display(@RequestParam(value="urn") String urn) {
		Concept c = termEng.getConcept(urn);
		if (c == null) {
			throw new BadRequestException("unknown frame uid: " + urn);
		}

		return new ModelAndView("/frame/concept", Table.buildRow("c", c, "eng", termEng));
	}
	
	@RequestMapping(value = "/term/display/{urn}")
	ModelAndView display2(@PathVariable(value="urn") String urn) {
		return display(urn);
	}
}
