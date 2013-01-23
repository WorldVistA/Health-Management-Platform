package org.osehra.cpe.vpr.search

import org.osehra.cpe.jsonc.JsonCCollection
import org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView

@Controller
class SearchController {

    @Autowired
    ISearchService searchService

    @RequestMapping(value = "/vpr/v{apiVersion}/{pid}/search")
    ModelAndView query(@PathVariable String apiVersion,
                       @PathVariable String pid,
                       @RequestParam String query,
                       Pageable pageable,
                       HttpServletRequest request) {
        SearchPatientResults searchResults = searchService.textSearchByPatient(query, pid)

        JsonCCollection<SummaryItem> jsonc = JsonCCollection.create(request, searchResults.foundItems)
        jsonc.put("elapsed", searchResults.elapsed)
        jsonc.put("original", searchResults.original)
        jsonc.put("altQuery", searchResults.altQuery)
        jsonc.put("corrections", searchResults.corrections)

        Map echoParams = new GrailsParameterMap(request)
        echoParams.keySet().removeAll(['controller', 'action'] as Set)
        return contentNegotiatingModelAndView(jsonc);
    }

    @RequestMapping(value = "/vpr/v{apiVersion}/{pid}/suggest")
    ModelAndView suggest(@PathVariable String apiVersion,
                         @PathVariable String pid,
                         @RequestParam String prefix) {
        List<String> suggestResults = searchService.textSuggestByPatient(prefix, pid)
		def resultsMap = []
		for(String suggestedResult in suggestResults){
			//Turn list into pair of values to make combo box to work. 
			resultsMap.add([id:suggestedResult, displayText:suggestedResult])
		}
        return contentNegotiatingModelAndView(JsonCCollection.create(resultsMap));
    }
}
