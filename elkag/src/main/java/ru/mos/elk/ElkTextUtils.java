package ru.mos.elk;

import org.json.JSONObject;

public abstract class ElkTextUtils {
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
}
