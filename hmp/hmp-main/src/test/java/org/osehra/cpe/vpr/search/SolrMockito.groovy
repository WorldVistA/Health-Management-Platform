package org.osehra.cpe.vpr.search

import org.apache.solr.common.util.NamedList
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import org.apache.solr.client.solrj.ResponseParser
import org.apache.solr.client.solrj.impl.XMLResponseParser
import org.apache.solr.client.solrj.SolrRequest
import org.mockito.ArgumentMatcher
import org.mockito.Mockito

import static java.lang.reflect.Modifier.isAbstract
import org.apache.solr.client.solrj.SolrServer
import static org.mockito.Mockito.mock
import org.apache.solr.client.solrj.request.QueryRequest
import org.apache.solr.client.solrj.SolrQuery
import static org.mockito.Matchers.argThat

class SolrMockito {

    static SolrServer mockSolrServer() {
        return mock(SolrServer.class, new MockSolrServerDefaultAnswer());
    }

    static XmlSolrQueryResponseAnswer solrXmlResponse(String xml) {
        return new XmlSolrQueryResponseAnswer(xml);
    }

    /**
     * Mockito {link Answer} to mock SolrServer.request() with a String xml response
     *
     * @see Answer
     * @see org.apache.solr.client.solrj.SolrServer
     */
    public static class XmlSolrQueryResponseAnswer implements Answer<NamedList<Object>> {

        private ResponseParser parser;
        private String xml;

        XmlSolrQueryResponseAnswer(String xml) {
            this.xml = xml;
            this.parser = new XMLResponseParser();
        }

        NamedList<Object> answer(InvocationOnMock invocation) {
            return parser.processResponse(new StringReader(xml))
        }
    }

    /**
     * Mockito Answer that will return the mock for abstract methods and will call the real method for concrete methods.
     *
     * @see "http://stackoverflow.com/questions/1087339/using-mockito-to-test-abstract-classes"
     */
    public static class MockSolrServerDefaultAnswer implements Answer<Object> {

        public Object answer(InvocationOnMock invocation) {
            Answer<Object> answer = null;

            if (isAbstract(invocation.getMethod().getModifiers())) {
                answer = Mockito.RETURNS_DEFAULTS;
            } else {
                answer = Mockito.CALLS_REAL_METHODS;
            }

            return answer.answer(invocation);
        }
    }
}
