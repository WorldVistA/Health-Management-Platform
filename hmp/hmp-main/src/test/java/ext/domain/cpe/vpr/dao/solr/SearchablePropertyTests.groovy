package EXT.DOMAIN.cpe.vpr.dao.solr

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.matchers.JUnitMatchers.hasItems
import static org.hamcrest.core.IsNull.nullValue

class SearchablePropertyTests extends GroovyTestCase {

    void testConstruct() {
        SearchableProperty p = new SearchableProperty(new Solr1(), 'aLong', 'foo');

        assertEquals(Solr1, p.owningClass)
        assertEquals('aLong', p.propertyName)
        assertEquals(long, p.propertyType)
        assertEquals('foo', p.fieldName)

        assertNull(p.boost)
        assertNull(p.valueClosure)

        def converterClosure = { it.toUpperCase() }

        p = new SearchableProperty(new Solr1(), 'aString', 'bar', null, 1.2, converterClosure);

        assertEquals(Solr1, p.owningClass)
        assertEquals('aString', p.propertyName)
        assertEquals(String, p.propertyType)
        assertEquals('bar', p.fieldName)

        assertEquals(1.2, p.boost)
        assertSame(converterClosure, p.valueClosure)
    }

    void testGetDefaultFieldValue() {
        SearchableProperty p = new SearchableProperty(new Solr1(aString: "foo"), 'aString', 'a_string');

        assertThat(p.fieldName, equalTo("a_string"))
        assertThat(p.fieldValue, equalTo("foo"))
    }

    void testGetExplicitFieldValue() {
        SearchableProperty p = new SearchableProperty(new Solr1(aString: "foo"), 'aString', 'bar', "baz");

        assertThat(p.fieldName, equalTo("bar"))
        assertThat(p.fieldValue, equalTo("baz"))
    }

    void testGetValueClosureFieldValueFromDefault() {
        def converterClosure = { it.toUpperCase() }
        SearchableProperty p = new SearchableProperty(new Solr1(aString: "foo"), 'aString', "bar", null, null, converterClosure);

        assertThat(p.fieldName, equalTo("bar"))
        assertThat(p.fieldValue, equalTo("FOO"))
    }

    void testGetValueClosureFieldValueFromIntermediateValue() {
        def converterClosure = { it.toUpperCase() }
        SearchableProperty p = new SearchableProperty(new Solr1(aString: "foo"), 'aString', "bar", "baz", null, converterClosure);

        assertThat(p.fieldName, equalTo("bar"))
        assertThat(p.fieldValue, equalTo("BAZ"))
    }

    void testGetValueClosureFieldValueFromCollection() {
        Solr2 solr2 = new Solr2(id: 23)
        solr2.addToSolrs(new Solr1(id: 1, aString: "one"));
        solr2.addToSolrs(new Solr1(id: 2, aString: "two"));
        solr2.addToSolrs(new Solr1(id: 3, aString: "three"));

        def converterClosure = { Set solrs -> solrs.collect { it.aString } }

        SearchableProperty p = new SearchableProperty(solr2, 'solrs', 'solrs', null, null, converterClosure);

        assertThat(p.fieldName, equalTo("solrs"))
        assertThat(p.fieldValue, hasItems(["one", "two", "three"].toArray()))
    }

    void testGetFieldValueFromCollection() {
        SortedSet<Solr1> solrs = new TreeSet<Solr1>();
        solrs.add(new Solr1(id: 1, aString: "one"));
        solrs.add(new Solr1(id: 2, aString: "two"));
        solrs.add(new Solr1(id: 3, aString: "three"));

        SearchableProperty p = new SearchableProperty(solrs, 'aString', 'a_string', null, null, null);

        assertThat(p.fieldName, equalTo("a_string"))
        assertThat(p.fieldValue, hasItems(["one", "two", "three"].toArray()))
    }

    void testGetFieldValueFromCollectionWithAllValuesNull() {
        SortedSet<Solr1> solrs = new TreeSet<Solr1>();
        solrs.add(new Solr1(id: 1, aString: null));
        solrs.add(new Solr1(id: 2, aString: null));
        solrs.add(new Solr1(id: 3, aString: null));

        SearchableProperty p = new SearchableProperty(solrs, 'aString', 'a_string', null, null, null);

        assertThat(p.fieldName, equalTo("a_string"))
        assertThat(p.fieldValue, nullValue())
    }
}
