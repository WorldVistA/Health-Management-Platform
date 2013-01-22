package EXT.DOMAIN.cpe.vpr.dao.solr;


import org.apache.solr.common.SolrInputDocument
import org.junit.Before
import org.junit.Test
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.nullValue

import static org.junit.Assert.assertThat
import static org.junit.matchers.JUnitMatchers.hasItems
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNull
import static org.junit.Assert.assertArrayEquals

public class TestDomainObjectToSolrInputDocument {

    private DomainObjectToSolrInputDocument converter;

    @Before
    public void setUp() throws Exception {
        converter = new DomainObjectToSolrInputDocument(SolrUtil.getSolrMappingsForClasses(Solr1.class,
                Solr1Disabled.class,
                Solr1Except.class,
                Solr1IdOverride.class,
                Solr1Only.class,
                Solr1Override.class,
                Solr2.class,
                Solr2Prefix.class));
    }

    @Test
    public void convertNull() {
        assertThat(converter.convert(null), nullValue());
    }

    @Test
    public void testConvert() {
        SolrInputDocument doc = converter.convert(new Solr1(id: 23, version: 0, aString: 'foo'))

        assertThat(doc.size(), equalTo(6))

        assertThat(doc.getFieldValue("title"), equalTo("myobjecttitle"))
        assertThat(doc.getFieldValue("id"), equalTo("solr1-23"))
        assertThat(doc.getFieldValue("a_float"), equalTo(0.0f))
        assertThat(doc.getFieldValue("a_long"), equalTo(0L))
        assertThat(doc.getFieldValue("an_int"), equalTo(0))
        assertThat(doc.getFieldValue("a_string"), equalTo("foo"))
    }

    @Test
    void testExcept() {
        SolrInputDocument doc = converter.convert(new Solr1Except(id: 23, version: 0, aString: 'foo'))

        assertThat(doc.size(), equalTo(4))

        assertThat(doc.getFieldValue("id"), equalTo("solr1except-23"))
        assertThat(doc.getFieldValue("a_long"), equalTo(0L))
        assertThat(doc.getFieldValue("an_int"), equalTo(0))
        assertThat(doc.getFieldValue("astringanothername_s"), equalTo("foo"))
    }

    @Test
    void testOnly() {
        Date date = new Date()
        SolrInputDocument doc = converter.convert(new Solr1Only(id: 23, version: 0, aString: 'foo', aLong: 42L, aDate: date))

        assertThat(doc.size(), equalTo(2))

        assertThat(doc.getFieldValue("a_long"), equalTo(42L))
        assertThat(doc.getFieldValue("a_date"), equalTo(date))
    }

    @Test
    void testIdNameAndValueOverrideAndAliasOverride() {
        SolrInputDocument doc = converter.convert(new Solr1IdOverride(id: 23, astring: 'foo'))

        assertThat(doc.size(), equalTo(6))

        assertThat(doc.getFieldValue("fooId"), equalTo("foo"))
        assertThat(doc.getFieldValue("alias"), equalTo("fubar"))
    }

    @Test
    void testValueOverride() {
        SolrInputDocument doc = converter.convert(new Solr1Override(id: 23, version: 0, aString: 'foo'))

        assertThat(doc.size(), equalTo(5))

        assertThat(doc.getFieldValue("a_string"), equalTo("FOO"))
    }

    @Test
    void testArrayValueOverride() {
        SolrInputDocument doc = converter.convert(new Solr2(id: 23, categories: "foo;bar;baz"))

        assertThat(doc.getFieldValues("categories"), hasItems(['foo', 'bar', 'baz'].toArray()))
    }

    @Test
    void testEnumProperty() {
        SolrInputDocument doc = converter.convert(new Solr2(id: 23, testenum: TESTENUM.ACTIVE))

        assertThat(doc.getFieldValue("testenum"), equalTo(TESTENUM.ACTIVE))
    }

    @Test
    void testHasManyPropertyMappedAsComponent() {
        Solr2 solr2 = new Solr2(id: 23)
        solr2.addToSolrs(new Solr1(id: 1, aString: "one"))
        solr2.addToSolrs(new Solr1(id: 2, aString: "two"))
        solr2.addToSolrs(new Solr1(id: 3, aString: "three"))

        SolrInputDocument doc = converter.convert(solr2)

//        assertThat(doc.size(), equalTo(12))

        assertThat(doc.getFieldValue("id"), equalTo("solr2-23"))

        assertThat(doc.getFieldValues("solr_id"), hasItems(["solr1-1", "solr1-2", "solr1-3"].toArray()))
        assertThat(doc.getFieldValues("solr_a_string"), hasItems(['one', 'two', 'three'].toArray()))
    }

    @Test
    void testHasManyOverride() {
        Solr2 solr2 = new Solr2(id: 23)
        solr2.addToSolrsWithOverride(new Solr1(id: 1, aLong: 1L))
        solr2.addToSolrsWithOverride(new Solr1(id: 2, aLong: 2L))
        solr2.addToSolrsWithOverride(new Solr1(id: 3, aLong: 3L))

        SolrInputDocument doc = converter.convert(solr2)

        assertThat(doc.getFieldValues("solrs_with_override"), hasItems([1L, 2L, 3L].toArray()))
    }

    @Test
    void testHasManyPrefix() {
        Solr2Prefix solr2 = new Solr2Prefix(id: 23)
        solr2.addToSolrs(new Solr1(id: 1, aLong: 1L))
        solr2.addToSolrs(new Solr1(id: 2, aLong: 2L))
        solr2.addToSolrs(new Solr1(id: 3, aLong: 3L))

        SolrInputDocument doc = converter.convert(solr2)

        assertThat(doc.size(), equalTo(8))

        assertThat(doc.getFieldValue("id"), equalTo("solr2prefix-23"))
        assertThat(doc.getFieldValues("one_id"), hasItems("solr1-1", "solr1-2", "solr1-3"))
        assertThat(doc.getFieldValues("one_title"), hasItems("myobjecttitle", "myobjecttitle", "myobjecttitle"))
        assertThat(doc.getFieldValues("one_a_float"), hasItems(0.0f, 0.0f, 0.0f))
        assertThat(doc.getFieldValues("one_a_long"), hasItems(1L, 2L, 3L))
        assertThat(doc.getFieldValues("one_an_int"), hasItems(0, 0, 0))
        assertThat(doc.getFieldValue("one_a_string"), nullValue())
        assertThat(doc.getFieldValue("one_a_date"), nullValue())
    }

    @Test
    void testCreateSolrDocumentBasic() {
        def now = new Date()
        def s = new Solr1(id: 25, aString: "mystring", anInt: 2, aFloat: 1.2f, aDate: now)

        SolrInputDocument doc = converter.convert(s)
        assertEquals "solr1-25", doc["id"].value
        assertEquals "myobjecttitle", doc["title"].value
        assertEquals "mystring", doc["a_string"].value
        assertEquals 2, doc["an_int"].value
        assertEquals 1.2f, doc["a_float"].value
        assertEquals 0L, doc["a_long"].value
        assertEquals now, doc["a_date"].value
    }

    @Test
    void testCreateSolrDocumentDisabled() {
        def s = new Solr1Disabled(id: 25, aString: "mystring", anInt: 2, aFloat: 1.2f, aDate: new Date())

        assertNull(converter.convert(s))
    }

    @Test
    void testCreateSolrDocumentExcept() {
        def s = new Solr1Except(id: 25, aString: "mystring", anInt: 2, aFloat: 1.2f, aDate: new Date())

        SolrInputDocument doc = converter.convert(s)
        assertEquals "solr1except-25", doc["id"].value
        assertEquals "mystring", doc["astringanothername_s"].value
        assertEquals 2, doc["an_int"].value
        assertEquals 0L, doc["a_long"].value
    }

    @Test
    void testCreateSolrDocumentOnly() {
        def now = new Date()
        def s = new Solr1Only(id: 25, aString: "mystring", anInt: 2, aFloat: 1.2f, aDate: now)

        SolrInputDocument doc = converter.convert(s)
        assertEquals 0L, doc["a_long"].value
        assertEquals now, doc["a_date"].value
    }

    @Test
    void testCreateSolrDocumentOverride() {
        def s = new Solr1Override(id: 25, aString: "mystring", anInt: 2, aFloat: 1.2f)

        SolrInputDocument doc = converter.convert(s)
        assertEquals "MYSTRING", doc["a_string"].value
        assertEquals 2, doc["an_int"].value
        assertEquals 1.2f, doc["a_float"].value
        assertEquals 0, doc["a_long"].value
    }

    @Test
    void testCreateSolrDocumentCollectionValuedOverride() {
        def s2 = new Solr2(categories: "foo;bar;baz")

        SolrInputDocument doc = converter.convert(s2)

        assertArrayEquals(['foo', 'bar', 'baz'].toArray(), doc["categories"].values.toArray())
    }

    @Test
    void testCreateSolrDocumentEnum() {
        def s2 = new Solr2(testenum: TESTENUM.NEW)

        SolrInputDocument doc = converter.convert(s2)

        assertEquals TESTENUM.NEW, doc["testenum"].value
    }

    @Test
    void testCreateSolrDocumentHasManyComponent() {
        def solrs = [new Solr1(id: 1, aString: 'one'), new Solr1(id: 2, aString: 'two'), new Solr1(id: 3, aString: 'three')]

        def s2 = new Solr2()
        solrs.each {
            s2.addToSolrs it
        }

        SolrInputDocument doc = converter.convert(s2)

        assertArrayEquals(["myobjecttitle", "myobjecttitle", "myobjecttitle"].toArray(), doc["solr_title"].values.toArray())
        assertArrayEquals(["solr1-1", "solr1-2", "solr1-3"].toArray(), doc["solr_id"].values.toArray())
        assertArrayEquals(["one", "two", "three"].toArray(), doc["solr_a_string"].values.toArray())
    }
}
