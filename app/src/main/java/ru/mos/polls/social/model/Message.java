package ru.mos.polls.social.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import ru.mos.polls.R;

/**
 * Структура для хранения данных кастомного сообщения о постинге
 */
public class Message {

    @SerializedName("title")
    private String title;
    @SerializedName("text")
    private String text;

    public Message(JSONObject jsonObject) {
        if (jsonObject != null) {
            title = jsonObject.optString("title");
            text = jsonObject.optString("text");
        }
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public boolean isEmpty() {
        boolean result = true;
        if (!isEmpty(text)) {
            result = false;
        }
        return result;
    }

    /**
     * Показываем кастомный диалог после чекина/отправки опроса
     *
     * @param context
     */
    public void showCustomMessage(final Context context) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (!TextUtils.isEmpty(title) && !"null".equalsIgnoreCase(title)) {
                builder.setTitle(title);
            }
            builder.setMessage(text);
            builder.setPositiveButton(R.string.survey_done_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        } catch (Exception ignored) {
        }
    }

    private boolean isEmpty(String target) {
        return TextUtils.isEmpty(target) || "null".equalsIgnoreCase(target);
    }
}
