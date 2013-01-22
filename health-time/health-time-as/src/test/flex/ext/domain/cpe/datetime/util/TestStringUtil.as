package EXT.DOMAIN.cpe.datetime.util {

import flexunit.framework.TestCase;

public class TestStringUtil extends TestCase {
    public function testIsEmpty():void {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("bob"));
        assertFalse(StringUtil.isEmpty("  bob  "));
    }

    public function testIsNotEmpty():void {
        assertFalse(StringUtil.isNotEmpty(null));
        assertFalse(StringUtil.isNotEmpty(""));
        assertTrue(StringUtil.isNotEmpty(" "));
        assertTrue(StringUtil.isNotEmpty("bob"));
        assertTrue(StringUtil.isNotEmpty("  bob  "));
    }

    public function testIsBlank():void {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank(" "));
        assertFalse(StringUtil.isBlank("bob"));
        assertFalse(StringUtil.isBlank("  bob  "));
    }

    public function testIsNotBlank():void {
        assertFalse(StringUtil.isNotBlank(null));
        assertFalse(StringUtil.isNotBlank(""));
        assertFalse(StringUtil.isNotBlank(" "));
        assertTrue(StringUtil.isNotBlank("bob"));
        assertTrue(StringUtil.isNotBlank("  bob  "));
    }

    public function testTrimToNull():void {
        assertNull(StringUtil.trimToNull(null));
        assertNull(StringUtil.trimToNull(""));
        assertNull(StringUtil.trimToNull("     "));
        assertEquals("abc", StringUtil.trimToNull("abc"));
        assertEquals("abc", StringUtil.trimToNull("    abc    "));
    }

    public function testDefaultIfEmpty():void {
        assertEquals("NULL", StringUtil.defaultIfEmpty(null, "NULL"));
        assertEquals("NULL", StringUtil.defaultIfEmpty("", "NULL"))
        assertEquals("bat", StringUtil.defaultIfEmpty("bat", "NULL"));
    }

    public function testSubstitute():void {
        var s:String = "{0}, {1}!";
        assertEquals("Hello, world!", StringUtil.substitute(s, "Hello", "world"));
    }
}
}
