package ch.epfl.chacun;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Base32Test {

    @Test
    void isValidReturnsTrueForValidString() {
        assertTrue(Base32.isValid("ABCD"));
    }

    @Test
    void isValidReturnsFalseForInvalidString() {
        assertFalse(Base32.isValid("ABCD1"));
    }

    @Test
    void isValidReturnsTrueForEmptyString() {
        assertTrue(Base32.isValid(""));
    }

    @Test
    void encodeBits5ReturnsCorrectEncoding() {
        assertEquals("T", Base32.encodeBits5(19));
    }

    @Test
    void encodeBits5ReturnsCorrectEncodingForBoundaryValue() {
        assertEquals("A", Base32.encodeBits5(0));
    }

    @Test
    void encodeBits10ReturnsCorrectEncoding() {
        assertEquals("K", Base32.encodeBits10(10));
    }

    @Test
    void encode5BitsWorksForLargeNumber() {
        assertEquals("L", Base32.encodeBits5(235));
    }

    @Test
    void encodeBits10ReturnsCorrectEncodingForBoundaryValue() {
        assertEquals("A", Base32.encodeBits10(0));
    }

    @Test
    void decodeReturnsCorrectValue() {
        assertEquals(0, Base32.decode("A"));
    }

    @Test
    void decodeReturnsCorrectValueForNonBoundaryValue() {
        assertEquals(10, Base32.decode("K"));
    }
}
