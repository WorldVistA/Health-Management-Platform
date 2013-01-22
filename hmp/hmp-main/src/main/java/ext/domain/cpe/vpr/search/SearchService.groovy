package EXT.DOMAIN.cpe.vpr.search

import org.apache.solr.client.solrj.SolrQuery

import org.apache.solr.common.SolrDocumentList;
import java.security.acl.Group;
import java.util.List;
import org.apache.solr.client.solrj.response.Group;


import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.datetime.format.HL7DateTimeFormat
import EXT.DOMAIN.cpe.datetime.format.PointInTimeFormat
import java.util.Map.Entry
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrQuery.ORDER
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.response.GroupCommand
import org.apache.solr.client.solrj.response.GroupResponse
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.response.SpellCheckResponse
import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrDocumentList
import org.apache.solr.common.SolrException
import org.apache.solr.common.params.GroupParams
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class SearchService implements ISearchService, EnvironmentAware {

    @Autowired
    SolrServer solrServer

    Environment environment

    public static final String PID = 'pid'
    public static final String UID = 'uid'
    public static final String DOMAIN = 'domain'
    public static final String KIND = 'kind'
    public static final String SUMMARY = 'summary'
    public static final String QUALIFIED_NAME = 'qualified_name'
    public static final String DATETIME = 'datetime'
    public static final String FACILITY = 'facility'
    public static final String PHRASE = 'phrase'
    public static final String URL_FIELD = 'url'

    SearchPatientResults textSearchByPatient(String queryText, String vprPatient) {
        SearchPatientResults results = new SearchPatientResults()
        results.original = queryText
        queryText = queryText.toLowerCase().replaceAll(' or ', ' || ')

        addResults(queryText, vprPatient, results)
        if (results.errorMessage.length()) return results

        if (results.foundItems.size() == 0) {
            // add the suggestions to corrections and altQuery
            attemptCorrection(queryText, vprPatient, results)
            addResults(results?.altQuery, vprPatient, results)
            if (results.foundItems.size() == 0) {
                results.altQuery = ''
            }
        }
        return results
    }

    List<String> textSuggestByPatient(String prefix, String vprPatient) {
        SolrQuery query = new SolrQuery("*:*")
        query.addFilterQuery("${PID}:${vprPatient}".toString())
        query.addFacetField(PHRASE)
        query.setFacetPrefix(prefix?.toLowerCase())
		query.setFacetMinCount(1)
        query.setRows(0)

        QueryResponse response = solrServer.query(query);
        List<String> results = response.getFacetField(PHRASE)?.values*.name
        return results
    }

    void addResults(String queryText, String vprPatient, SearchPatientResults results) {
        if (!queryText) return

        addRecentMatches(queryText, vprPatient, results)
        if (results.errorMessage.length()) return

        addOtherMatches(queryText, vprPatient, results)
    }

    // use facets to see which results match and return only the most recent ----------------------

    /* get the matching facets for the query and add the most recent of each facet to the results
       expects the queryText to already be encoded */

    SearchPatientResults addRecentMatches(String queryText, String vprPatient, SearchPatientResults results) {
		addRecentLabs(queryText,vprPatient,results)
		addRecentVitals(queryText,vprPatient,results)
		return results
	}

	SearchPatientResults addRecentLabs(String queryText, String vprPatient, SearchPatientResults results) {
			// find out which lab results are available
		SolrQuery query = new SolrQuery('"'+ queryText+'"')
		query.addField(PHRASE)
		query.addFilterQuery("${PID}:${vprPatient}")
		query.addFilterQuery("${DOMAIN}:result")
		query.addSortField("observed", ORDER.desc);
		query.setFields(UID, DATETIME, SUMMARY, URL_FIELD, DOMAIN, KIND, FACILITY)
		query.set(GroupParams.GROUP, true);
		query.set(GroupParams.GROUP_FIELD, "qualified_name");
		query.setParam("q.op", "AND")
		query.setRows(999)

		return getSolrResults(results, query)
	}

	private getSolrResults(SearchPatientResults results, SolrQuery query) {
		try {

			QueryResponse response = solrServer.query(query);
			results.elapsed += response.getQTime()

			List<GroupCommand> commands = response?.groupResponse?.values
			for(GroupCommand cmnd : commands){
				for(Group group : cmnd.values){
					//Group potentially can have more then one result, we are interested in first one.
					SummaryItem summaryItem = createSummaryItem(group.result.get(0))
					summaryItem.count = group?.result?.numFound
					results.foundItems.add(summaryItem)
				}
			}
		} catch (SolrException e) {
			results.errorMessage = e.getMessage()
		}

		return results
	}

	SearchPatientResults addRecentVitals(String queryText, String vprPatient, SearchPatientResults results) {
		// find out which vitals are available
		SolrQuery query = new SolrQuery('"'+ queryText+'"')
		query.addField(PHRASE)
		query.addFilterQuery("${PID}:${vprPatient}")
		query.addFilterQuery("${DOMAIN}:vital_sign")
		query.addSortField("observed", ORDER.desc);
		query.setFields(UID, DATETIME, SUMMARY, URL_FIELD, DOMAIN, KIND, FACILITY)
		query.set(GroupParams.GROUP, true);
		query.set(GroupParams.GROUP_FIELD, "qualified_name");
		query.setParam("q.op", "AND")
		query.setRows(999)
		
		return getSolrResults(results, query)
	}

    // TODO: is this method similar or different than createSummaryItem()?
    SummaryItem createRecentMatchesSummaryItem(SolrDocument doc) {
        SummaryItem item = new SummaryItem()
        item.uid = doc.getFieldValue(UID).toString()
        item.datetime = doc.getFieldValue(DATETIME).toString()
        item.datetimeFormatted = formatDateTime(item.datetime)
        item.summary = doc.getFieldValue(SUMMARY).toString()
        item.type = doc.getFieldValue(DOMAIN).toString()
        item.kind = doc.getFieldValue(KIND).toString()
        item.where = doc.getFieldValue(FACILITY).toString()
        return item
    }

    // perform regular search on non-result domains -----------------------------------------------

    /* Search domains other than results and add those to the list
       expects the queryText to already be encoded */

    SearchPatientResults addOtherMatches(String queryText, String vprPatient, SearchPatientResults results) {
		SolrQuery query = new SolrQuery('"'+ queryText +'"')
		query.addField(PHRASE)
        query.addFilterQuery("${PID}:${vprPatient}")
        query.addFilterQuery("-${DOMAIN}:vital_sign")
        query.addFilterQuery("-${DOMAIN}:result")
		query.addSortField("observed", ORDER.desc)
		query.setFields(UID, DATETIME, SUMMARY, URL_FIELD, DOMAIN, KIND, FACILITY)
        query.setHighlight(true)
        query.addHighlightField("body").addHighlightField("subject")
        query.setHighlightFragsize(72)
        query.setRows(999)
        query.setParam("q.op", "AND")
        query.setSortField("datetime", ORDER.desc)

        QueryResponse response = solrServer.query(query)
        results.foundItems.addAll(createSummaryItems(response))
        results.elapsed += response.getQTime()

        return results
    }

    List<SummaryItem> createSummaryItems(QueryResponse response) {
        List<SummaryItem> items = []
        for (SolrDocument it : response.results) {
            SummaryItem summaryItem = createSummaryItem(it)
            setSummaryItemHighlight(summaryItem, response)
            items.add(summaryItem)
        }
        return items
    }

    void setSummaryItemHighlight(SummaryItem summaryItem, QueryResponse response) {
        if (response.highlighting.get(summaryItem.uid) != null) {
            List<String> highlightSnippets = []
            for (Entry<String, List<String>> entry : response.highlighting.get(summaryItem.uid).entrySet()) {
                highlightSnippets.addAll(entry.value)
            }
            if (!highlightSnippets.isEmpty())
                summaryItem.highlight = highlightSnippets.get(0) // TODO: include more than one snippet here?
        }
    }

    SummaryItem createSummaryItem(SolrDocument doc) {
        SummaryItem item = new SummaryItem()
        //fl=id,datetime,summary,url,domain,kind,facility
        item.uid = doc.getFieldValue(UID)
        item.datetime = doc.getFieldValue(DATETIME) ?: "Unknown Time"
        if (item.datetime == "Unknown Time") {
            item.datetimeFormatted = null
        } else {
            item.datetimeFormatted = formatDateTime(item.datetime)
        }
        item.summary = doc.getFieldValue(SUMMARY) ?: "Unknown"
        item.type = doc.getFieldValue(DOMAIN)
        item.kind = doc.getFieldValue(KIND) ?: "Unknown Type"
        item.where = doc.getFieldValue(FACILITY)
        return item
    }

    // handle suggestions (auto-match and spell checking) -----------------------------------------
    void attemptCorrection(String queryText, String vprPatient, SearchPatientResults results) {
        ModifiableSolrParams query = new ModifiableSolrParams()
        query.set("qt", "/spell")
        query.set("q", queryText)
        query.set("spellcheck", true)
        query.set("spellcheck.collate", true)
        query.set("maxCollationTries", 1)

        QueryResponse response = solrServer.query(query)
        SpellCheckResponse spellCheckResponse = response?.spellCheckResponse

        results.altQuery = spellCheckResponse?.collatedResult
        results.corrections = (spellCheckResponse?.suggestions*.alternatives).flatten()
    }

    String formatDateTime(String x) {
        if (!x) return null
        PointInTime t = HL7DateTimeFormat.parse(x)
        return PointInTimeFormat.forPointInTime(t).print(t)
    }
}
