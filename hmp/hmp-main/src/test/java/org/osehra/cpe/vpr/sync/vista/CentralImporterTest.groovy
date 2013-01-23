package org.osehra.cpe.vpr.sync.vista

import org.osehra.cpe.vpr.Patient

import org.springframework.core.convert.converter.Converter
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.fail
import org.springframework.core.env.Environment

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.equalTo

class CentralImporterTest {

    Patient pt
    CentralImporter importer

    @Before
    public void setUp() {
        pt = new Patient([pid: "1", familyName: "FOO", givenNames: "BAR"])

        importer = new CentralImporter()
        importer.importers = [
                'foo': new FooImporter(),
                'bar': new BarImporter()
        ]
        importer.afterPropertiesSet()
    }

    @Test
    void testAfterPropertiesSet() {
        importer = new CentralImporter()
        try {
            importer.afterPropertiesSet()
            fail('expected illegal argument exception for importers not set')
        } catch (IllegalArgumentException e) {
            // NOOP
        }
    }

    @Test
    void testConvertDispatchesToAppropriateImporter() {
        VistaDataChunk chunk = MockVistaDataChunks.createFromJson('''
{"hello": "Hello, Foo"}
''', pt, "foo")

        Object result = importer.convert(chunk)
        assertThat(result, equalTo("Hello, Foo"))

        chunk = MockVistaDataChunks.createFromJson('''
{"goodbye":"Goodbye, Bar"}
''', pt, "bar")

        result = importer.convert(chunk)
        assertThat(result, equalTo("Goodbye, Bar"))
    }

    @Test(expected = ImportException)
    void testConvertUnknownDomainName() {
        VistaDataChunk chunk = MockVistaDataChunks.createFromJson('''
{"goodbye":"Get out of here, Baz!"}
''', pt, "baz")

        importer.convert(chunk)
    }

    public static class FooImporter implements Converter<VistaDataChunk, String> {
        String convert(VistaDataChunk item) {
            return item.json.path("hello").textValue()
        }
    }

    public static class BarImporter implements Converter<VistaDataChunk, String> {
        String convert(VistaDataChunk item) {
            return item.json.path("goodbye").textValue()
        }
    }

    public static class FredImporter implements Converter<VistaDataChunk, String> {
        String convert(VistaDataChunk item) {
            String r = item.json.path("hello").textValue()
            return r.replaceAll("Foo", 'Fred')
        }
    }

    public static class BarneyImporter implements Converter<VistaDataChunk, String> {
        String convert(VistaDataChunk item) {
            String r = item.json.path("goodbye").textValue()
            return r.replaceAll("Bar", 'Barney')
        }
    }
}
