package EXT.DOMAIN.cpe.feed.atom.xml

import grails.test.GrailsUnitTestCase
import org.custommonkey.xmlunit.XMLUnit
import grails.converters.XML
import EXT.DOMAIN.cpe.feed.atom.Person
import org.custommonkey.xmlunit.Diff


class PersonMarshallerTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()

        XMLUnit.ignoreWhitespace = true
        XML.registerObjectMarshaller(new PersonMarshaller(), 1)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSupports() {
        PersonMarshaller m = new PersonMarshaller()
        assertTrue(m.supports(new Person(name: 'Fred Flintstone')))
    }

    void testMarshalPersonWithName() {
        def expected = '''
<bar>
<fred>
<name>flintstone</name>
</fred>
<class>EXT.DOMAIN.cpe.feed.atom.xml.Bar</class>
</bar>
'''
        String xml = new XML(new Bar(fred: new Person(name: 'flintstone'))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }

    void testMarshalPerson() {
        def expected = '''
<bar>
<fred>
<name>flintstone</name>
<uri>http://www.example.org</uri>
<email>fred@example.org</email>
</fred>
<class>EXT.DOMAIN.cpe.feed.atom.xml.Bar</class>
</bar>
'''
        String xml = new XML(new Bar(fred: new Person(name: 'flintstone', email: 'fred@example.org', uri: 'http://www.example.org'))).toString()
        def xmlDiff = new Diff(expected, xml)
        assertTrue(xmlDiff.toString(), xmlDiff.similar())
    }
}

class Bar {
    Person fred
}
