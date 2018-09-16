package com.github.FlorianSteenbuck.other.punycode;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static junit.framework.TestCase.assertEquals;

public class PunycodeTest {
    protected void testDecode(String from, String to) throws UnsupportedEncodingException {
        assertEquals(to, new Punycode(from).decode());
    }

    protected void testEncode(String from, String to) throws UnsupportedEncodingException {
        assertEquals(to, new Punycode(from).encode());
    }
    
    @Test
    public void decodeTest() throws UnsupportedEncodingException {
        testDecode("MLLER-KVA", "MüLLER");
        // TODO more tests
    }

    @Test
    public void encodeTest()  throws UnsupportedEncodingException {
        testEncode("\u2665", "g6h");
        assertEquals("-g6h", new Punycode("\u2665", Punycode.Feature.NEED_DELIMITER).encode());
        testEncode("", "");
        testEncode("mueller", "mueller");
        testEncode("müller", "mller-kva");
        testEncode("jürg", "jrg-hoa");
        testEncode("ä ö ü ß", "   -7kav3ivb");
        testEncode("中央大学", "fiq80yua78t");
    }
}