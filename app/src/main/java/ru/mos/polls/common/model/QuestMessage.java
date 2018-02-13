package ru.mos.polls.common.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import org.json.JSONObject;

import ru.mos.polls.R;
import ru.mos.polls.util.AgTextUtil;

/**
 * Сообщение от сервера при выполнении задания
 *
 * @since 1.9
 */
public class QuestMessage {
    private String title;
    private String text;

    public QuestMessage(JSONObject json) {
        if (json != null) {
            json = json.optJSONObject("message");
            if (json != null) {
                title = json.optString("title");
                text = json.optString("text");
            }
        }
    }

    public void show(Activity activity) {
        show(activity, false);
    }

    public void show(final Activity activity, final boolean needCloseActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.ag_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (needCloseActivity) {
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (needCloseActivity) {
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                }
            }
        });
        builder.show();
    }

    public boolean isEmpty() {
        return AgTextUtil.isEmpty(text);
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
