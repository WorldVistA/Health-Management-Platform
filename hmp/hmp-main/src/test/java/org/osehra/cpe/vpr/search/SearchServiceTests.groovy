package org.osehra.cpe.vpr.search

import static org.osehra.cpe.vpr.search.SearchService.*
import static org.osehra.cpe.vpr.search.SolrMockito.mockSolrServer
import static org.osehra.cpe.vpr.search.SolrMockito.solrXmlResponse
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat
import static org.junit.matchers.JUnitMatchers.hasItem
import static org.junit.matchers.JUnitMatchers.hasItems
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

import java.rmi.server.UID

import org.apache.solr.client.solrj.SolrRequest
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.impl.XMLResponseParser
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrDocument
import org.apache.solr.common.params.SolrParams
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.springframework.core.env.Environment

class SearchServiceTests {

    SearchService searchService
    Environment mockEnvironment
    SolrServer mockSolrServer

    @Before
    public void setUp() {
        mockSolrServer = mockSolrServer()
        mockEnvironment = mock(Environment.class)

        searchService = new SearchService()
        searchService.setEnvironment(mockEnvironment)
        searchService.setSolrServer(mockSolrServer)
    }

    @Test
    void testCreateRecentMatchesSummaryItem() {
        SolrDocument doc = new SolrDocument();
        doc.addField(SearchService.UID, "urn:va:500:129:medication:401232R;O")
        doc.addField(DATETIME, "19960510")
        doc.addField(KIND, "Medication, Outpatient")
        doc.addField(SUMMARY, "AMPICILLIN CAP,ORAL T 1 TID")

        SummaryItem item = searchService.createRecentMatchesSummaryItem(doc)

        assertThat(item.uid, equalTo(doc.get(SearchService.UID)))
        assertThat(item.datetime, equalTo(doc.get(DATETIME)))
        assertThat(item.datetimeFormatted, equalTo(searchService.formatDateTime(doc.get(DATETIME))))
        assertThat(item.kind, equalTo(doc.get(KIND)))
        assertThat(item.summary, equalTo(doc.get(SUMMARY)))
    }

    @Test
    void testCreateSummaryItems() {
        QueryResponse r = new QueryResponse(new XMLResponseParser().processResponse(new StringReader('''<?xml version="1.0" encoding="UTF-8"?>
        <response>
        <lst name="responseHeader">
            <int name="status">0</int>
            <int name="QTime">0</int>
            <lst name="params">
                <str name="fl">uid,datetime,kind,summary</str>
                <arr name="fq"><str> pid:1</str><str>-alias:result</str><str>-alias:vital_sign</str></arr>
                <str name="q">ampicillin</str>
                <str name="rows">999</str>
            </lst>
        </lst>
        <result name="response" numFound="5" start="0">
            <doc>
                <str name="uid">urn:va:medication:500:129:medID401052R;O</str>
                <str name="datetime">19951020</str>
                <str name="kind">Medication, Outpatient</str>
                <str name="summary">AMPICILLIN CAP,ORAL TID</str>
            </doc>
            <doc>
                <str name="uid">urn:va:medication:500:129:medID401232R;O</str>
                <str name="datetime">19960510</str>
                <str name="kind">Medication, Outpatient</str>
                <str name="summary">AMPICILLIN CAP,ORAL T 1 TID</str>
            </doc>
            <doc>
                <str name="uid">urn:va:art:500:101</str>
                <str name="datetime">19940103074800.000</str>
                <str name="summary">AMPICILLIN</str>
            </doc>
            <doc>
                <str name="uid">urn:va:medication:500:129:medID61U;I</str>
                <str name="datetime">19950125150000.000</str>
                <str name="kind">Medication, Inpatient</str>
                <str name="summary">AMPICILLIN 250MG CAP Give:  PO Q8HA</str>
            </doc>
            <doc>
                <str name="uid">urn:va:medication:500:129:medID65U;I</str>
                <str name="datetime">19950317090000.000</str>
                <str name="kind">Medication, Inpatient</str>
                <str name="summary">AMPICILLIN 250MG CAP Give:  PO Q6H</str>
            </doc>
        </result>
        <lst name="highlighting">
            <lst name="urn:va:medication:500:129:medID401052R;O"/>
            <lst name="urn:va:medication:500:129:medID401232R;O"/>
            <lst name="urn:va:art:500:101"/>
            <lst name="urn:va:medication:500:129:medID61U;I"/>
            <lst name="urn:va:medication:500:129:medID65U;I"/>
        </lst>
        </response>''')), mockSolrServer)

        List<SummaryItem> items = searchService.createSummaryItems(r)

        assertThat(items.size(), equalTo(5))
    }

    @Test
    void testAddRecentMatches() {

    	searchService = new SearchService(){		
			@Override
			public SearchPatientResults addRecentLabs(String queryText,
					String vprPatient, SearchPatientResults results) {
				SummaryItem summaryItem = new SummaryItem(uid:"123",summary:" Most recent Lab", kind:"result")	
				results.foundItems.add(summaryItem)
				return results
			}
			
			@Override
			public SearchPatientResults addRecentVitals(String queryText,
					String vprPatient, SearchPatientResults results) {
				SummaryItem summaryItem = new SummaryItem(uid:"456",summary:" Most recent Vital", kind:"vital-status")	
				results.foundItems.add(summaryItem)
				return results
			}		
		}
    					
    	SearchPatientResults results = searchService.addRecentMatches("glucose", "27", new SearchPatientResults());
		assertThat(results.foundItems.size(), equalTo(2))
    }

	@Test
	void testAddRecentLabs() {
		when(mockSolrServer.request(Matchers.any(SolrRequest.class))).thenAnswer(solrXmlResponse('''<?xml version="1.0" encoding="UTF-8"?>
<response>
	<lst name="responseHeader">
	<int name="status">0</int>
	<int name="QTime">2</int>
		<lst name="params">
			<str name="fl">uid,datetime,summary,url,typeName,kind,facility</str>
			<str name="sort">observed desc</str>
			<str name="q">glucose</str>
			<str name="group.field">qualified_name</str>
			<str name="group">true</str>
			<str name="fq">pid:27 AND domain:result</str>
			<str name="rows">999</str>
		</lst>
	</lst>
<lst name="grouped">
		<lst name="qualified_name">
		<int name="matches">29</int>
		<arr name="groups">
		<lst>
			<str name="groupValue">URINE GLUCOSE (URINE)</str>
			<result name="doclist" numFound="1" start="0">
				<doc>
					<str name="datetime">201205291802</str>
					<str name="domain">result</str>
					<str name="facility">SLC-FO HMP DEV</str>
					<str name="kind">Laboratory</str>
					<str name="summary">URINE GLUCOSE (URINE) Neg. mg/dL</str>
					<str name="uid">urn:va:F484:8:lab:CH;6879469.819787;690</str>
				</doc>
			</result>
		</lst>
		<lst>
			<str name="groupValue">GLUCOSE (SERUM)</str>
			<result name="doclist" numFound="26" start="0">
			<doc>
				<str name="datetime">201107070900</str>
				<str name="domain">result</str>
				<str name="facility">SLC-FO HMP DEV</str>
				<str name="kind">Laboratory</str>
				<str name="summary">GLUCOSE (SERUM) 90 mg/dL</str>
				<str name="uid">urn:va:F484:8:lab:CH;6889291.91;2</str>
			</doc>
			</result>
		</lst>
		<lst>
			<str name="groupValue">GLUCOSE (BLOOD)</str>
			<result name="doclist" numFound="2" start="0">
			<doc>
				<str name="datetime">199709150700</str>
				<str name="domain">result</str>
				<str name="facility">CAMP MASTER</str>
				<str name="kind">Laboratory</str>
				<str name="summary">GLUCOSE (BLOOD) 125&lt;em&gt;urn:hl7:observation-interpretation:H&lt;/em&gt; MG/DL</str>
				<str name="uid">urn:va:F484:8:lab:CH;7029083.92999;2</str>
			</doc>
			</result>
		</lst>
		</arr>
		</lst>
		</lst>
</response>
'''))
				
		SearchPatientResults results = searchService.addRecentLabs("glucose", "27", new SearchPatientResults());
		assertThat(results.foundItems.size(), equalTo(3))
		SummaryItem item = results.foundItems.get(0)
		assertThat(item.uid, equalTo('urn:va:F484:8:lab:CH;6879469.819787;690'))
		assertThat(item.summary,equalTo('URINE GLUCOSE (URINE) Neg. mg/dL'))
		assertThat(item.type,equalTo('result'))
		assertThat(item.kind,equalTo('Laboratory'))
		assertThat(item.where,equalTo('SLC-FO HMP DEV'))
		assertThat(item.datetime.toString(),equalTo('201205291802'))
	}
	
	@Test
	public void testAddRecentVitals() throws Exception {
		when(mockSolrServer.request(Matchers.any(SolrRequest.class))).thenAnswer(solrXmlResponse('''<?xml version="1.0" encoding="UTF-8"?>				
<response>
<lst name="responseHeader">
<int name="status">0</int>
<int name="QTime">39</int>
<lst name="params">
<str name="fl">uid,datetime,summary,url,domain,kind,facility</str>
<str name="sort">observeddesc</str>
<str name="q">pulse</str>
<str name="group.field">qualified_name</str>
<str name="group">true</str>
<str name="fq">pid:27ANDdomain:vital_sign</str>
<str name="rows">999</str>
</lst></lst>
<lst name="grouped">
	<lst name="qualified_name">
	<int name="matches">40</int>
	<arr name="groups">
	<lst>
		<str name="groupValue">PULSE</str>
		<result name="doclist" numFound="29" start="0">
			<doc>
				<str name="datetime">201107011430</str>
				<str name="domain">vital_sign</str>
				<str name="facility">SLC-FOHMPDEV</str>
				<str name="kind">VitalSign</str>
				<str name="summary">PULSE97/min</str>
				<str name="uid">urn:va:F484:8:vs:32556</str>
			</doc>
		</result>
	</lst>
	<lst>
		<str name="groupValue">PULSEOXIMETRY</str>
		<result name="doclist" numFound="11" start="0">
		<doc>
			<str name="datetime">201107011430</str>
			<str name="domain">vital_sign</str>
			<str name="facility">SLC-FOHMPDEV</str>
			<str name="kind">VitalSign</str>
			<str name="summary">PULSEOXIMETRY94%</str>
			<str name="uid">urn:va:F484:8:vs:32559</str>
		</doc>
	    </result>
	</lst>
	</arr>
	</lst>
	</lst>
</response>
'''))
		
		SearchPatientResults results = searchService.addRecentVitals("pulse", "27", new SearchPatientResults());
		assertThat(results.foundItems.size(), equalTo(2))
		SummaryItem item = results.foundItems.get(1)
		assertThat(item.uid, equalTo('urn:va:F484:8:vs:32559'))
		assertThat(item.summary,equalTo('PULSEOXIMETRY94%'))
		assertThat(item.type,equalTo('vital_sign'))
		assertThat(item.kind,equalTo('VitalSign'))
		assertThat(item.where,equalTo('SLC-FOHMPDEV'))
		assertThat(item.datetime.toString(),equalTo('201107011430'))

	}
	
    @Test
    void testAddOtherMatches() {
        when(mockSolrServer.request(Matchers.any(SolrRequest.class))).thenAnswer(solrXmlResponse('''<?xml version="1.0" encoding="UTF-8"?>
        <response>
        <lst name="responseHeader">
            <int name="status">0</int>
            <int name="QTime">0</int>
            <lst name="params">
                <str name="fl">uid,datetime,kind,summary</str>
                <arr name="fq"><str> pid:1</str><str>-alias:result</str><str>-alias:vital_sign</str></arr>
                <str name="q">ampicillin</str>
                <str name="rows">999</str>
            </lst>
        </lst>
        <result name="response" numFound="5" start="0">
            <doc>
                <str name="uid">urn:va:medication:500:129:medID401052R;O</str>
                <str name="datetime">19951020</str>
                <str name="kind">Medication, Outpatient</str>
                <str name="summary">AMPICILLIN CAP,ORAL TID</str>
            </doc>
            <doc>
                <str name="uid">urn:va:medication:500:129:medID401232R;O</str>
                <str name="datetime">19960510</str>
                <str name="kind">Medication, Outpatient</str>
                <str name="summary">AMPICILLIN CAP,ORAL T 1 TID</str>
            </doc>
            <doc>
                <str name="uid">urn:va:art:500:101</str>
                <str name="datetime">19940103074800.000</str>
                <str name="summary">AMPICILLIN</str>
            </doc>
            <doc>
                <str name="uid">urn:va:medication:500:129:medID61U;I</str>
                <str name="datetime">19950125150000.000</str>
                <str name="kind">Medication, Inpatient</str>
                <str name="summary">AMPICILLIN 250MG CAP Give:  PO Q8HA</str>
            </doc>
            <doc>
                <str name="uid">urn:va:medication:500:129:medID65U;I</str>
                <str name="datetime">19950317090000.000</str>
                <str name="kind">Medication, Inpatient</str>
                <str name="summary">AMPICILLIN 250MG CAP Give:  PO Q6H</str>
            </doc>
        </result>
        <lst name="highlighting">
            <lst name="urn:va:medication:500:129:medID401052R;O"/>
            <lst name="urn:va:medication:500:129:medID401232R;O"/>
            <lst name="urn:va:art:500:101"/>
            <lst name="urn:va:medication:500:129:medID61U;I"/>
            <lst name="urn:va:medication:500:129:medID65U;I"/>
        </lst>
        </response>'''))

        SearchPatientResults results = searchService.addOtherMatches("ampicillin", "23", new SearchPatientResults());
        assertThat(results.foundItems.size(), equalTo(5))

        ArgumentCaptor<SolrRequest> arg = ArgumentCaptor.forClass(SolrRequest.class);
        verify(mockSolrServer).request(arg.capture())

        SolrRequest solrRequest = arg.value;
        assertThat(solrRequest.path, equalTo("/select"))
        assertThat(solrRequest.params.get("q"), equalTo("\"ampicillin\""))
        assertThat(solrRequest.params.getParams("fq").toList(), hasItems("pid:23", "-domain:result", "-domain:vital_sign"))
        assertThat(solrRequest.params.get("fl"), equalTo([SearchService.UID, DATETIME, SUMMARY, URL_FIELD, DOMAIN, KIND, FACILITY].join(',')))
        assertThat(solrRequest.params.getBool("hl"), equalTo(true))
        assertThat(solrRequest.params.getParams("hl.fl").toList(), hasItems("body", "subject"))
        assertThat(solrRequest.params.getInt("hl.fragsize"), equalTo(72))
        assertThat(solrRequest.params.getInt("rows"), equalTo(999))
        assertThat(solrRequest.params.get("q.op"), equalTo("AND"))
    }

    @Test
    void testSuggestByPatient() {
        when(mockSolrServer.request(Matchers.any(SolrRequest.class))).thenAnswer(solrXmlResponse('''<?xml version="1.0" encoding="UTF-8"?>
        <response>
        <lst name="responseHeader">
            <int name="status">0</int>
            <int name="QTime">0</int>
            <lst name="params">
                <str name="facet">true</str>
                <str name="q">*:*</str>
                <str name="facet.prefix">glu</str>
                <str name="facet.field">phrase</str>
                <str name="fq"> pid:1</str>
                <str name="rows">0</str>
            </lst>
        </lst>
        <result name="response" numFound="909" start="0"/>
        <lst name="facet_counts">
            <lst name="facet_queries"/>
            <lst name="facet_fields">
                <lst name="phrase">
                    <int name="glucose (serum)">25</int>
                    <int name="glucose (blood)">2</int>
                    <int name="glucosamine po">0</int>
                    <int name="glucosamine cap">1</int>
                </lst>
            </lst>
            <lst name="facet_dates"/>
        </lst>
        </response>
'''))

        List<String> results = searchService.textSuggestByPatient("glu", "23");
        assertThat(results, hasItems("glucose (serum)", "glucose (blood)", "glucosamine po", "glucosamine cap"))

        ArgumentCaptor<SolrRequest> arg = ArgumentCaptor.forClass(SolrRequest.class);
        verify(mockSolrServer).request(arg.capture())

        SolrRequest solrRequest = arg.value;
        assertThat(solrRequest.path, equalTo("/select"))
        assertThat(solrRequest.params.get("q"), equalTo("*:*"))
        assertThat(solrRequest.params.get("fq"), equalTo("pid:23"))
        assertThat(solrRequest.params.getBool("facet"), equalTo(true))
        assertThat(solrRequest.params.get("facet.field"), equalTo("phrase"))
        assertThat(solrRequest.params.get("facet.prefix"), equalTo("glu"))
        assertThat(solrRequest.params.getInt("rows"), equalTo(0))
    }

//	@Test
//	public void testSuggestCaseNonSensitive() throws Exception {
//		when(mockSolrServer.request(Matchers.any(SolrRequest.class))).thenAnswer(solrXmlResponse('''<?xml version="1.0" encoding="UTF-8"?><response/>'''))
//        List<String> results = searchService.textSuggestByPatient("MiXedCASe", "12");
//		
//        ArgumentCaptor<SolrRequest> arg = ArgumentCaptor.forClass(SolrRequest.class);
//        verify(mockSolrServer).request(arg.capture())
//
//        SolrRequest solrRequest = arg.value;
//        assertThat(solrRequest.params.get("facet.prefix"), equalTo("mixedcase"))
//	}

    // void testParseTextSuggestions() {
    //        /* sample query --
    //       http://localhost:8983/solr/select?q=*:*&fq=%2Bpid%3A1&facet=true&facet.field=phrase&facet.prefix=glu&rows=0
    //        */
    //        def suggestions = '''<?xml version="1.0" encoding="UTF-8"?>
    //        <response>
    //        <lst name="responseHeader">
    //            <int name="status">0</int>
    //            <int name="QTime">0</int>
    //            <lst name="params">
    //                <str name="facet">true</str>
    //                <str name="q">*:*</str>
    //                <str name="facet.prefix">glu</str>
    //                <str name="facet.field">phrase</str>
    //                <str name="fq"> pid:1</str>
    //                <str name="rows">0</str>
    //            </lst>
    //        </lst>
    //        <result name="response" numFound="909" start="0"/>
    //        <lst name="facet_counts">
    //            <lst name="facet_queries"/>
    //            <lst name="facet_fields">
    //                <lst name="phrase">
    //                    <int name="glucose (serum)">25</int>
    //                    <int name="glucose (blood)">2</int>
    //                    <int name="glucosamine po">0</int>
    //                    <int name="glucosamine cap">1</int>
    //                </lst>
    //            </lst>
    //            <lst name="facet_dates"/>
    //        </lst>
    //        </response>'''
    //
    //        List items = searchService.parseTextSuggest(suggestions)
    //        assertEquals 3, items.size()
    //        assertEquals 'glucosamine cap', items[0]
    //        assertEquals 'glucose (serum)', items[2]
    //    }
    //
    //    void testParseOtherWithHilite() {
    //        /* sample query --
    //        http://localhost:8983/solr/select?q=allergy&fq=%2Bpid%3A1&fq=-alias%3Aresult&fq=-alias%3Avital_sign&fl=id,datetime,kind,summary&hl=true&hl.fl=body,subject&rows=99
    //        */
    //        def results = '''<?xml version="1.0" encoding="UTF-8"?>
    //        <response>
    //        <lst name="responseHeader">
    //            <int name="status">0</int>
    //            <int name="QTime">16</int>
    //            <lst name="params">
    //                <str name="fl">uid,datetime,kind,summary</str>
    //                <str name="q">allergy</str>
    //                <str name="hl.fl">body,subject</str>
    //                <arr name="fq"><str>+pid:1</str><str>-domain:result</str><str>-domain:vital_sign</str></arr>
    //                <str name="hl">true</str>
    //                <str name="rows">99</str>
    //            </lst>
    //        </lst>
    //        <result name="response" numFound="4" start="0">
    //            <doc>
    //                <str name="uid">urn:va:tiu:500:1023</str>
    //                <str name="datetime">19990301111000.000</str>
    //                <str name="kind">Document</str><str name="summary">JEANIE'S TITLE</str>
    //            </doc>
    //            <doc>
    //                <str name="uid">urn:va:tiu:500:947</str>
    //                <str name="datetime">19990105141600.000</str>
    //                <str name="kind">Document</str>
    //                <str name="summary">JEANIE'S TITLE</str>
    //            </doc>
    //            <doc>
    //                <str name="uid">urn:va:tiu:500:942</str>
    //                <str name="datetime">19990105094953.000</str>
    //                <str name="kind">Document</str>
    //                <str name="summary">JEANIE'S TITLE</str>
    //            </doc>
    //            <doc>
    //                <str name="uid">urn:va:tiu:500:782</str>
    //                <str name="datetime">19980608105151.000</str>
    //                <str name="kind">Document</str>
    //                <str name="summary">JEANIE'S TITLE</str>
    //            </doc>
    //        </result>
    //        <lst name="highlighting">
    //            <lst name="urn:va:tiu:500:1023">
    //                <arr name="body">
    //                    <str>want to find out what the &lt;em&gt;allergy&lt;/em&gt; is for this patient.  Let</str>
    //                </arr>
    //            </lst>
    //            <lst name="urn:va:tiu:500:947">
    //                <arr name="body">
    //                    <str>want to find out what the &lt;em&gt;allergy&lt;/em&gt; is for this patient.  Let</str>
    //                </arr>
    //            </lst>
    //            <lst name="urn:va:tiu:500:942">
    //                <arr name="body">
    //                    <str>:This is a test for and many other things that we want to find out what the &lt;em&gt;allergy&lt;/em&gt;</str>
    //                </arr>
    //            </lst>
    //            <lst name="urn:va:tiu:500:782">
    //                <arr name="body">
    //                    <str> out what the &lt;em&gt;allergy&lt;/em&gt; is for this patient.</str>
    //                </arr>
    //            </lst>
    //        </lst>
    //        </response>'''
    //
    //        List items = searchService.parseOtherResults(results)
    //        assertEquals 4, items.size()
    //        assertEquals "JEANIE'S TITLE", items[1].summary
    //        assertEquals '19980608105151.000', items[3].datetime
    //        assertEquals ':This is a test for and many other things that we want to find out what the <em>allergy</em>', items[2].highlight
    //        int elapsed = searchService.parseQueryTime(results)
    //        assertEquals 16, elapsed
    //    }
    //
    //    void testSpellCheck() {
    //        /* sample query --
    //        http://localhost:8983/solr/spell?q=gastrointestinul%20problum&spellcheck=true&spellcheck.collate=true
    //           to build the index the first time --
    //        http://localhost:8983/solr/spell?q=diabetus&spellcheck=true&spellcheck.collate=true&spellcheck.build=true
    //        */
    //        def results = '''<?xml version="1.0" encoding="UTF-8"?>
    //        <response>
    //        <lst name="responseHeader">
    //            <int name="status">0</int>
    //            <int name="QTime">0</int>
    //        </lst>
    //        <result name="response" numFound="0" start="0"/>
    //        <lst name="spellcheck">
    //            <lst name="suggestions">
    //                <lst name="gastrointestinul">
    //                    <int name="numFound">1</int>
    //                    <int name="startOffset">0</int>
    //                    <int name="endOffset">16</int>
    //                    <arr name="suggestion">
    //                        <str>gastrointestinal</str>
    //                    </arr>
    //                </lst>
    //                <lst name="problum">
    //                    <int name="numFound">1</int>
    //                    <int name="startOffset">17</int>
    //                    <int name="endOffset">24</int>
    //                    <arr name="suggestion">
    //                       <str>problem</str>
    //                       <str>problemo</str>
    //                    </arr>
    //                </lst>
    //                <str name="collation">gastrointestinal problem</str>
    //            </lst>
    //        </lst>
    //        </response>
    //'''
    //        String alt = searchService.parseAlternateQuery(results)
    //        assertEquals 'gastrointestinal problem', alt
    //        List items = searchService.parseCorrections(results)
    //        assertEquals 3, items.size()
    //        assertEquals 'problem', items[1]
    //    }
    //
    //
}

