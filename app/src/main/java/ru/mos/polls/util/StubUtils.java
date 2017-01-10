package ru.mos.polls.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Инкапсулирует создание заглушек
 *
 * @since 2.2.0
 */
public class StubUtils {

    /**
     * Читаем файл из res/raw и получаем его содержимое ввиде строки
     *
     * @param context текущий контест  {@link Context}
     * @param resId   идентификатор файла заглушки из папки res/raw
     * @return содержимое файла
     */
    public static String fromRaw(Context context, int resId) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(resId)));
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
        } catch (IOException ignored) {
        }
        return sb.toString();
    }

    /**
     * Возвращает содержимое файл заглушки как объект {@link JSONObject}<br/>
     * См. {@link #fromRaw(Context, int)}
     *
     * @param context текущий контест  {@link Context}
     * @param resId   идентификатор файла заглушки из папки res/raw
     * @return объект {@link JSONObject}
     */
    public static JSONObject fromRawAsJsonObject(Context context, int resId) {
        JSONObject result = null;
        try {
            result = new JSONObject(fromRaw(context, resId));
        } catch (JSONException ignored) {
        }
        return result;
    }

    /**
     * Возвращает содержимое файл заглушки как объект {@link JSONArray}<br/>
     * См. {@link #fromRaw(Context, int)}
     *
     * @param context текущий контест  {@link Context}
     * @param resId   идентификатор файла заглушки из папки res/raw
     * @return объект {@link JSONArray}
     */
    public static JSONArray fromRawAsJsonArray(Context context, int resId) {
        JSONArray result = null;
        try {
            result = new JSONArray(fromRaw(context, resId));
        } catch (JSONException ignored) {
        }
        return result;
    }

}
