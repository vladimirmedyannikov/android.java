package ru.mos.polls.util;

import java.nio.CharBuffer;


public abstract class AgTextUtil {

    public static String stripNonDigits(CharSequence input) {
        if (input == null)
            return null;
        if (input.length() == 0)
            return "";
        char[] result = new char[input.length()];
        int cursor = 0;
        CharBuffer buffer = CharBuffer.wrap(input);
        while (buffer.hasRemaining()) {
            char chr = buffer.get();
            if (chr > 47 && chr < 58)
                result[cursor++] = chr;
        }
        return new String(result, 0, cursor);
    }

    public static String stripLengthText(String text, int maxLength) {
        String result = "";
        if (text != null) {
            result = text.substring(0, maxLength) + "...";
        }
        return result;
    }
}
