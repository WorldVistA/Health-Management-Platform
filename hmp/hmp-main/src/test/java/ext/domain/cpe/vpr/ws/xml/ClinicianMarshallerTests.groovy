package EXT.DOMAIN.cpe.vpr.ws.xml

import EXT.DOMAIN.cpe.test.MockGrailsApplicationUnitTestCase
import EXT.DOMAIN.cpe.vpr.Clinician
import grails.converters.XML
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit

class ClinicianMarshallerTests extends MockGrailsApplicationUnitTestCase {

    protected void setUp() {
        super.setUp();

        XMLUnit.ignoreWhitespace = true
        XML.registerObjectMarshaller(new ClinicianMarshaller())
    }

    void testSupports() {
        ClinicianMarshaller m = new ClinicianMarshaller()
        assertTrue(m.supports(new Clinician(name: 'Foo')))
    }

    void testMarshalClinician() {
               def expected = "<bar><class>${Bar.class.name}</class><clinician>Foo</clinician></bar>"
        String xml = new XML(new Bar(clinician: new Clinician(name: 'Foo'))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }
}

class Bar {
    Clinician clinician
}
