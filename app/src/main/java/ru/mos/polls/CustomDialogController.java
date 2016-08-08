package ru.mos.polls;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;


public abstract class CustomDialogController {
    public static final String PREFS = "custom_dialog_prefs";
    public static final String IS_NEED_SHARE = "is_need_share";

    public static void disableShare(Context context) {
        setShareAbility(context, false);
    }

    public static void enableShare(Context context) {
        setShareAbility(context, true);
    }

    public static boolean isShareEnable(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(IS_NEED_SHARE, true);
    }

    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static void showShareDialog(Context context, String message, ActionListener listener) {
        if (listener == null) {
            listener = ActionListener.STUB;
        }
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setView(getView(context, dialog, message, listener));
        dialog.setCancelable(false);
        try {
            dialog.show();
        } catch (Exception ignored) {
        }
    }

    private static View getView(final Context context, final Dialog dialog, String message, final ActionListener actionListener) {
        View result = View.inflate(context, R.layout.layout_custom_result_dialog, null);
        TextView messageView = (TextView) result.findViewById(R.id.message);
        messageView.setText(message);
        result.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onYes(dialog);
            }
        });
        result.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onCancel(dialog);
            }
        });
        result.findViewById(R.id.disable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableShare(context);
                actionListener.onDisable(dialog);
            }
        });
        return result;
    }

    public static void setShareAbility(Context context, boolean enable) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(IS_NEED_SHARE, enable).apply();
    }

    public interface ActionListener {
        ActionListener STUB = new ActionListener() {
            @Override
            public void onYes(Dialog dialog) {
            }

            @Override
            public void onCancel(Dialog dialog) {
            }

            @Override
            public void onDisable(Dialog dialog) {
            }
        };

        /**
         * Да
         *
         * @param dialog
         */
        void onYes(Dialog dialog);

        /**
         * Нет
         *
         * @param dialog
         */
        void onCancel(Dialog dialog);

        /**
         * Больше не предлагать
         *
         * @param dialog
         */
        void onDisable(Dialog dialog);
    }

}
