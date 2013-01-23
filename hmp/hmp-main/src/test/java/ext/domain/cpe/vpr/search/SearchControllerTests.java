package org.osehra.cpe.vpr.search;

import org.osehra.cpe.jsonc.JsonCCollection;
import org.osehra.cpe.vpr.web.servlet.view.ContentNegotiatingViewResolver;
import org.osehra.cpe.vpr.web.view.AbstractGrailsConverterView;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.*;

public class SearchControllerTests {

    private SearchController c;
    private ISearchService mockSearchService;

    @Before
    public void setUp() throws Exception {
        mockSearchService = mock(ISearchService.class);

        c = new SearchController();
        c.setSearchService(mockSearchService);
    }

    @Test
    public void testQuery() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();

        SummaryItem mockSummaryItem = new SummaryItem();
        mockSummaryItem.setSummary("FOO");
        mockSummaryItem.setType("allergy");
        mockSummaryItem.setWhere("Camp Master");

        SearchPatientResults mockSearchResults = new SearchPatientResults();
        mockSearchResults.setOriginal("foo");
        mockSearchResults.setElapsed(123);
        mockSearchResults.setAltQuery("bar");
        mockSearchResults.getFoundItems().add(mockSummaryItem);

        when(mockSearchService.textSearchByPatient("foo", "23")).thenReturn(mockSearchResults);

        ModelAndView mav = c.query("1", "23", "foo", new PageRequest(0, 20), request);

        verify(mockSearchService).textSearchByPatient("foo", "23");

        assertThat(mav.getViewName(), equalTo(ContentNegotiatingViewResolver.DEFAULT_VIEW_NAME));
        JsonCCollection<SummaryItem> r = (JsonCCollection) mav.getModel().get(AbstractGrailsConverterView.DEFAULT_MODEL_KEY);
        assertNotNull(r);

        assertNotNull(r.data);
        assertThat((Integer) r.get("elapsed"), equalTo(mockSearchResults.getElapsed()));
        assertThat((String) r.get("original"), equalTo(mockSearchResults.getOriginal()));
        assertThat((String) r.get("altQuery"), equalTo(mockSearchResults.getAltQuery()));
        assertThat(r.getTotalItems(), equalTo(mockSearchResults.getFoundItems().size()));
        assertThat(r.getCurrentItemCount(), equalTo(mockSearchResults.getFoundItems().size()));
        assertThat(r.getItems(), hasItem(mockSummaryItem));
    }

    @Test
    public void testSuggest() throws Exception {
//        when(mockSearchService.textSuggestByPatient("foo", "23")).thenReturn(mockSearchResults);

        ModelAndView view = c.suggest("1", "23", "foo");
        assertThat(view.getViewName(), equalTo(ContentNegotiatingViewResolver.DEFAULT_VIEW_NAME));
        verify(mockSearchService).textSuggestByPatient("foo", "23");
    }
}
