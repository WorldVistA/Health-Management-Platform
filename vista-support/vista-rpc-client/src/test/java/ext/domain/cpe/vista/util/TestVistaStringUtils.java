package org.osehra.cpe.vista.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * TODO: Document ${CLASS_NAME}
 */
public class TestVistaStringUtils extends TestCase {
    @Test
    public void testPiece1() {
        assertEquals("Piece1", VistaStringUtils.piece("Piece1^Piece2^Piece3", "^"));
    }

    @Test
    public void testPiece2() {
        assertEquals("Piece2", VistaStringUtils.piece("Piece1^Piece2^Piece3", "^", 2));
    }

    @Test
    public void testPiece3() {
        assertEquals("Piece3", VistaStringUtils.piece("Piece1^Piece2^Piece3", "^", 3));
    }

    @Test
    public void testPiece4() {
        assertEquals("", VistaStringUtils.piece("Piece1^Piece2^Piece3", "^", 4));
    }

    @Test
    public void testPiece5() {
        assertEquals("Piece1^Piece2", VistaStringUtils.piece("Piece1^Piece2^Piece3", "^", 1, 2));
    }

    @Test
    public void testPiece6() {
        assertEquals("Piece2^Piece3", VistaStringUtils.piece("Piece1^Piece2^Piece3", "^", 2, 3));
    }

    @Test
    public void testPiece7() {
        assertEquals("Piece2^Piece3", VistaStringUtils.piece("Piece1^Piece2^Piece3", "^", 2, 4));
    }

    @Test
    public void testPiece8() {
        assertEquals("Piece3", VistaStringUtils.piece("Piece1^Piece2^Piece3", "^", 3, 5));
    }

    @Test
    public void testPiece9() {
        assertEquals("", VistaStringUtils.piece("Piece1^Piece2^Piece3", "^", 4, 6));
    }

    @Test
    public void testTranslate1() {
        assertEquals("abcdefghabcde", VistaStringUtils.translate("ABCDEFGHABCDE", "ABCDEFGH", "abcdefgh"));
    }

    @Test
    public void testTranslate2() {
        assertEquals("abcdEFGHabcdE", VistaStringUtils.translate("ABCDEFGHABCDE", "ABCD", "abcde"));
    }

    @Test
    public void testTranslate3() {
        assertEquals("abcdeFGHabcde", VistaStringUtils.translate("ABCDEFGHABCDE", "ABCDEABC", "abcdefgh"));
    }

    @Test
    public void testTranslate4() {
        assertEquals("abcdeabcabcde", VistaStringUtils.translate("ABCDEFGHABCDE", "ABCDEFGH", "abcdeabc"));
    }

    @Test
    public void testCrc16() {
        assertEquals(12480, VistaStringUtils.crc16("A"));
        assertEquals(17697, VistaStringUtils.crc16("ABC"));
        assertEquals(40710, VistaStringUtils.crc16("AVIVA-VDEV.FO-SLC.DOMAIN.EXT"));
    }

    @Test
    public void testCrc16Hex() {
        assertEquals("30C0", VistaStringUtils.crc16Hex("A"));
        assertEquals("4521", VistaStringUtils.crc16Hex("ABC"));
        assertEquals("9F06", VistaStringUtils.crc16Hex("AVIVA-VDEV.FO-SLC.DOMAIN.EXT"));
    }

    @Test
    public void testSplitLargeStringIfNecessary() {
        assertThat((String) VistaStringUtils.splitLargeStringIfNecessary("foo"), is("foo"));

        char[] chars = new char[] {'a','b','c'};
        StringBuilder longString = new StringBuilder();
        for (char c : chars) {
            char[] bunchOChars = new char[245];
            Arrays.fill(bunchOChars, c);
            longString.append(bunchOChars);
        }
        longString.append("ddd");

        List<String> split = (List<String>) VistaStringUtils.splitLargeStringIfNecessary(longString.toString());
        assertThat(split.size(), is(4));
        assertThat(split.get(0).length(), is(245));
        assertThat(split.get(1).length(), is(245));
        assertThat(split.get(2).length(), is(245));
        assertThat(split.get(3).length(), is(3));
    }
}
