package ru.mos.polls.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONObject;

import java.nio.CharBuffer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class AgTextUtil {
    /**
     * Для проверки email
     */
    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String RUSSIAN_REG = "([А-ЯЁ][а-яё]+[\\-\\s]?){1,}";
    public static final Pattern russianPattern = Pattern.compile(RUSSIAN_REG);

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

    /**
     * @param email {@link String}
     * @return true - если формат соответствует формату
     */
    public static boolean isEmailValid(String email) {
        return Pattern
                .compile(EMAIL_PATTERN)
                .matcher(email)
                .matches();
    }

    public static boolean validateRus(String text) {
        Matcher matcher = russianPattern.matcher(text);
        return matcher.matches();
    }

    public static <T> String listToString(List<T> list) {
        String res = "";
        for (int i = 0; i < list.size(); i++) {
            res += list.get(i).toString();
            if (i != list.size() - 1) res += "\n";
        }
        return res;
    }

    public static String getPhoneFormat(String phone) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, "RU");
            phone = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
        }
        return phone;
    }

    public static boolean isStringNoEmpty(String text) {
        return text != null && !"null".equalsIgnoreCase(text);
    }

    public static String getString(JSONObject jsonObject, String tag, String defaultValue) {
        String result = jsonObject.optString(tag);
        if (isEmpty(result)) {
            result = defaultValue;
        }
        return result;
    }

    public static boolean isEmpty(String target) {
        return android.text.TextUtils.isEmpty(target)
                || "null".equalsIgnoreCase(target);
    }

    public static String stripDigit(String text) {
        return text.replaceAll("[^0-9.]", "");
    }

    public static boolean checkSNILSsum(char[] array) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += array[i] * (9 - i);
        }
        return false;
    }
}
