package EXT.DOMAIN.cpe.vpr.web.taglib

import EXT.DOMAIN.cpe.param.ParamService
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import static org.mockito.Mockito.times
import static org.mockito.Mockito.never

import static org.mockito.Mockito.verify
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.mock.web.MockHttpServletRequest

import static org.mockito.Mockito.verifyNoMoreInteractions
import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.*

class ParamTagLibTests {

    private ParamService mockParamService
    private ByteArrayOutputStream mockOutputStream;
    private ParamTagLib taglib
    private MockHttpServletRequest mockRequest

    @BeforeClass
    static void setUpClass() {
        Object.metaClass.encodeAsHTML = { -> org.codehaus.groovy.grails.plugins.codecs.HTMLCodec.encode(delegate) }
    }

    @Before
    void setUp() {
        taglib = new ParamTagLib();
        mockRequest = new MockHttpServletRequest()
        taglib.metaClass.request = mockRequest;
        taglib.paramService = mockParamService = mock(ParamService.class)
        mockOutputStream = new ByteArrayOutputStream();
        taglib.metaClass.getOut << { -> mockOutputStream };
    }

    @Test
    void testParamValTag() {
        when(mockParamService.getUserParamVal("VPR USER PREF", "ext.theme")).thenReturn("foo");

        taglib.paramVal(param: "VPR USER PREF", key:"ext.theme", defaultVal:"bar")

        assert mockOutputStream.toString() == "foo"
		//no more setting attribute on request
        assertThat(mockRequest.getAttribute("VPR USER PREF" + ":" + "ext.theme").toString(), is(equalTo("null")));
		
        mockOutputStream.reset();
		
        taglib.paramVal(param: "VPR USER PREF", key:"ext.theme", defaultVal:"bar")
        assert mockOutputStream.toString() == "foo"
		verify(mockParamService, times(2)).getUserParamVal("VPR USER PREF", "ext.theme")
        verifyNoMoreInteractions(mockParamService)
    }

    @Test
    void testUserPrefTag() {
        when(mockParamService.getUserParamVal("VPR USER PREF", "ext.theme")).thenReturn("foo");

        taglib.userPref(key:"ext.theme", defaultVal:"bar")

        assert mockOutputStream.toString() == "foo"
        //assertThat(mockRequest.getAttribute("VPR USER PREF" + ":" + "ext.theme").toString(), is(equalTo("foo")));
		assertThat(mockRequest.getAttribute("VPR USER PREF" + ":" + "ext.theme").toString(), is(equalTo("null")));
		

        mockOutputStream.reset();

        taglib.paramVal(param: "VPR USER PREF", key:"ext.theme", defaultVal:"bar")
        assert mockOutputStream.toString() == "foo"
		
		verify(mockParamService, times(2)).getUserParamVal("VPR USER PREF", "ext.theme")
        verifyNoMoreInteractions(mockParamService)
    }
}
