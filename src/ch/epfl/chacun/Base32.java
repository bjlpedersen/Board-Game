package ch.epfl.chacun;

/**
 * This class provides static methods for encoding and decoding integers to and from Base32.
 *
 * @author Bjork Pedersen (376143)
 */
public class Base32 {
    /**
     * The Base32 alphabet.
     */
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    /**
     * Private constructor to prevent instantiation.
     *
     * @throws AssertionError always
     */
    private Base32() {
    }

    /**
     * Checks if a string is a valid Base32 encoded string.
     *
     * @param string The string to check.
     * @return true if the string is valid Base32, false otherwise.
     */
    public static boolean isValid(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!ALPHABET.contains(String.valueOf(string.charAt(i)))) return false;
        }
        return true;
    }

    /**
     * Encodes a 5-bit number into a Base32 string.
     *
     * @param num The number to encode.
     * @return The Base32 encoded string.
     */
    public static String encodeBits5(int num) {
        int shiftedNum = num & 0b11111;
        return String.valueOf(ALPHABET.charAt(shiftedNum));
    }

    /**
     * Encodes a 10-bit number into a Base32 string.
     *
     * @param num The number to encode.
     * @return The Base32 encoded string.
     */
    public static String encodeBits10(int num) {
        int firstShiftedNum = (num >> 5) & 0b11111;
        int secondShiftedNum = num & 0b11111;
        return ALPHABET.charAt(firstShiftedNum) + String.valueOf(ALPHABET.charAt(secondShiftedNum));
    }

    /**
     * Decodes a Base32 string into an integer.
     *
     * @param s The Base32 string to decode.
     * @return The decoded integer.
     */
    public static int decode(String s) {
        double result = 0;
        if (s.length() > 1) {
            for (int i = s.length() - 1; i >= 0; --i) {
                int index = i == 1 ? 0 : 1;
                result = result + Math.pow(32, i) * indexOf(ALPHABET, s.charAt(index));
            }
        } else {
            for (int i = s.length() - 1; i >= 0; --i) {
                result = result + Math.pow(32, i) * indexOf(ALPHABET, s.charAt(i));
            }
        }
        return (int) result;
    }

    /**
     * Returns the index of a character in a string.
     *
     * @param s The string to search.
     * @param c The character to find.
     * @return The index of the character, or -1 if the character is not found.
     */
    private static int indexOf(String s, char c) {
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == c) return i;
        }
        return -1;
    }

}
