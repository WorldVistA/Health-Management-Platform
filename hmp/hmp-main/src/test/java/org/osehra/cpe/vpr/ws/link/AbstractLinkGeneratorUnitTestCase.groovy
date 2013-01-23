package org.osehra.cpe.vpr.ws.link

import org.osehra.cpe.test.MockGrailsApplicationUnitTestCase
import org.codehaus.groovy.grails.plugins.codecs.URLCodec
import org.codehaus.groovy.grails.commons.ConfigurationHolder

abstract class AbstractLinkGeneratorUnitTestCase extends MockGrailsApplicationUnitTestCase {

    public static final String MOCK_SERVER_URL = 'http://www.example.org/foo'

    @Override protected void setUp() {
        super.setUp()

        loadCodec(URLCodec)

        def config = new ConfigObject()
        config.grails.serverURL = MOCK_SERVER_URL
        ConfigurationHolder.config = config
    }

}
