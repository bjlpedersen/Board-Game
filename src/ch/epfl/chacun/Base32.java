package ch.epfl.chacun;

public class Base32 {
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private Base32() {}

    public static boolean isValid(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!ALPHABET.contains(String.valueOf(string.charAt(i)))) return false;
        }
        return true;
    }

    public static String encodeBits5(int num) {
        int shiftedNum = num & 0b11111;
        return String.valueOf(ALPHABET.charAt(shiftedNum));
    }

    public static String encodeBits10(int num) {
        int shiftedNum = num & 0b1111111111;
        return String.valueOf(ALPHABET.charAt(shiftedNum));
    }

    public static int decode(String s) {
        double result = 0;
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.length() > 1) {
                int index = i == 1 ? 0 : 1;
                result = result + Math.pow(32, i) * indexOf(ALPHABET, s.charAt(index));
            } else {
                result = result+ Math.pow(32, i) * indexOf(ALPHABET, s.charAt(i));
            }
        }
        return (int) result;
    }

    private static int indexOf(String s, char c) {
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == c) return i;
        }
        return -1;
    }

}
