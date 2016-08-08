package ru.mos.polls.helpers;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import ru.mos.polls.R;

/**
 * Набор вспомогательных методов для работы с текстом
 *
 * @since 1.0
 */
public class TextHelper {

    /**
     * Строки подсказок ограничений используемые в {@link ru.mos.polls.survey.variants.values.CharVariantValue}
     *
     * @since 1.9.4
     */
    public static class Constraints {
        public static String getHintTextOnlyOne(Context context, int max, int length) {
            String result = context.getResources().getQuantityString(R.plurals.constraints_text_by_only_one, max);
            return String.format(result, max, length);
        }

        public static String getHintTextMinMax(Context context, int min, int max, int length) {
            String result = context.getResources().getQuantityString(R.plurals.constraints_text_by_min_max, max);
            return String.format(result, min, max, length);
        }

        public static String getHintOnlyOne(Context context, int max) {
            String result = context.getResources().getQuantityString(R.plurals.constraints_only_one, max);
            return String.format(result, max);
        }

        public static String getHintMinMax(Context context, int min, int max) {
            String result = context.getResources().getQuantityString(R.plurals.constraints_min_max, max);
            return String.format(result, min, max);
        }

        public static String getCharsCount(Context context, int length, int max) {
            String count = context.getResources().getQuantityString(R.plurals.characters_count, length);
            String left = context.getResources().getQuantityString(R.plurals.characters_left, length);
            if (length == max) {
                return String.format(context.getResources().getString(R.string.characters_max), String.format(count, length));
            }
            return left + " " + String.format(count, length);
        }
    }

    public static String capitalizeFirstLatter(String s) {
        final String result;
        if (TextUtils.isEmpty(s)) {
            result = s;
        } else {
            if (s.length() == 1) {
                result = s.toUpperCase();
            } else {
                result = s.substring(0, 1).toUpperCase() + s.substring(1);
            }
        }
        return result;
    }

    public static String getString(JSONObject jsonObject, String tag, String defaultValue) {
        String result = defaultValue;
        if (jsonObject != null) {
            result = jsonObject.optString(tag);
            if (TextUtils.isEmpty(result) || "null".equalsIgnoreCase(result)) {
                result = defaultValue;
            }
        }
        return result;
    }

    public static String md5(final String s) {
        String result = "";
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            result = hexString.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return result;
    }

    /**
     * Проверка наличия непредсмотренных символов в промо-коде
     * Промокод должен содержать только латиницу, цифры и символ "-"
     *
     * @param promoCode - значение введенного промокода
     * @return - true, если промо-код состоит только из предсмотренных символов
     */
    public static boolean isPromoValid(String promoCode) {
        return FormatValidator.validate(promoCode, FormatValidator.PROMO_CODE_PATTERN);
    }

    public static boolean isEmailValid(String email) {
        return FormatValidator.validate(email, FormatValidator.EMAIL_PATTERN);
    }

    public static boolean hasOnlyDigitsAndLatinSymbols(String target) {
        return FormatValidator.validate(target, FormatValidator.ONLY_DIGITS_AND_LATIN_SYMBOL_PATTERN);
    }

    public static boolean hasUpperLowerCaseAndDigitsAndLatinSymbols(String target) {
        return FormatValidator.validate(target, FormatValidator.LOWER_UPPER_CASE_AND_DIGITS_AND_SYMBOLS);
    }

    public static boolean hasDigitsAndSymbolsInOneCaseOrSymbolsInOtherCases(String target) {
        return FormatValidator.validate(target, FormatValidator.COMBINATION_DIGITS_AND_SYMBOLS_ONLY_ONE_CASE_OR_SYMBOLS_IN_OTHER_CASES_PATTERN);
    }

    public static boolean hasNotSpecialSymbols(String target) {
        return FormatValidator.validate(target, FormatValidator.HAS_NOT_SPECIAL_SYMBOLS_PATTERN);
    }

    /**
     * Класс для работы с регулярными выражениями
     */
    public static class FormatValidator {
        /**
         * Формат символов, допустимых в промокодах
         */
        public static final String PROMO_CODE_PATTERN = "[a-zA-Z0-9-]*";

        /**
         * Формат дял проверки email
         */
        public static final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        /**
         * Только латинские буквы и цифры
         */
        public static final String ONLY_DIGITS_AND_LATIN_SYMBOL_PATTERN = "(^[a-z]+$|^[0-9]+$|^[A-Z]+$)";

        /**
         * Содержит комбинацию цифр и букв в одном регистре или только комбинацию букв в разных регистрах
         */
        public static final String COMBINATION_DIGITS_AND_SYMBOLS_ONLY_ONE_CASE_OR_SYMBOLS_IN_OTHER_CASES_PATTERN = "(^[a-z0-9]+$|^[A-Z0-9]+$|^[a-zA-Z]+$)";

        /**
         * Отсутсвие спецсимволов в строке
         */
        public static final String HAS_NOT_SPECIAL_SYMBOLS_PATTERN = ".\\W.";

        /**
         * Верхний и нижний регистр, латинские буквы и цифры
         */
        public static final String LOWER_UPPER_CASE_AND_DIGITS_AND_SYMBOLS = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{5,}$";

        /**
         * Вызывать для проверки строки на соотвествие формату
         *
         * @param target  - проверяемая строка
         * @param pattern - формат
         * @return true, если соответствует формату
         */
        public static boolean validate(final String target, String pattern) {
            return Pattern
                    .compile(pattern)
                    .matcher(target)
                    .matches();
        }

    }

}
