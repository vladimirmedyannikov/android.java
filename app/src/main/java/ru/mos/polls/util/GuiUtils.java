package ru.mos.polls.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Набор вспомогательных методов для UI
 *
 * @since 2.1.0
 */
public class GuiUtils {

    public static int getStartPosition(String source, String target) {
        int position = 0;
        if (source != null && target != null) {
            Pattern word = Pattern.compile(target);
            Matcher match = word.matcher(source);
            while (match.find()) {
                position = match.start();
                break;
            }
        }
        return position;
    }

    public static int getEndPosition(String source, String target) {
        int position = 0;
        if (source != null && target != null) {
            Pattern word = Pattern.compile(target);
            Matcher match = word.matcher(source);
            while (match.find()) {
                position = match.end();
                break;
            }
        }
        return position;
    }

    public static void setTextAndVisibility(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Скрываем клавиатуру
     *
     * @param v любая {@link View}
     */
    public static void hideKeyboard(View v) {
        if (v != null && v.getContext() != null) {
            if (!v.isFocused()) {
                v.requestFocus();
            }
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(View v) {
        if (v != null && v.getContext() != null) {
            if (!v.isFocused()) {
                v.requestFocus();
            }
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    /**
     * Добавление слушателя {@link OnSoftInputStateListener} на состояние клавиатуры<br/>
     *
     * @param screenRootView           корневой {@link View} в разметке экрана
     * @param onSoftInputStateListener callback {@link OnSoftInputStateListener}
     */
    public static void addSoftInputStateListener(final View screenRootView, final OnSoftInputStateListener onSoftInputStateListener) {
        screenRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private static final int HEIGHT_ROOT_THRESHOLD = 100;

            @Override
            public void onGlobalLayout() {
                final int thresold = screenRootView.getHeight() / 3;
                final int rootViewHeight = screenRootView.getRootView().getHeight();
                final int viewHeight = screenRootView.getHeight();
                int heightDiff = rootViewHeight - viewHeight;
                if (heightDiff > thresold) {
                    if (onSoftInputStateListener != null) {
                        onSoftInputStateListener.onOpened();
                    }
                } else {
                    if (onSoftInputStateListener != null) {
                        onSoftInputStateListener.onClosed();
                    }
                }
            }
        });
    }

    public static void openEmailForFeedback(Context context) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Обращение в службу технической поддержки приложения");
        i.setType("text/plain");
        try {
            context.startActivity(Intent.createChooser(i, "Выберите приложение.."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Для обращения в службу технической поддержки необходимо исопльзовать E-mail клиент, установите любой E-mail клиент", Toast.LENGTH_SHORT).show();
        }
    }

    public static void displayUnknownError(Context context) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Произошла непредвиденная ошибка, попробуйте повторить операцию позже");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        try {
            builder.show();
        } catch (WindowManager.BadTokenException ignored) {
        }
    }

    public static void displayUnknownError(Context context, DialogInterface.OnClickListener okListener) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Произошла непредвиденная ошибка, попробуйте повторить операцию позже");
        builder.setPositiveButton("OK", okListener);
        try {
            builder.show();
        } catch (WindowManager.BadTokenException ignored) {
        }
    }


    public static void displayOkMessage(Context context, int message, DialogInterface.OnClickListener okListener) {
        if (context == null) return;
        displayOkMessage(context, context.getString(message), okListener);
    }

    public static void displayOkMessage(Context context, int message, int title, DialogInterface.OnClickListener okListener) {
        if (context == null) return;
        displayOkMessage(context, context.getString(message), context.getString(title), okListener);
    }

    public static void displayOkMessage(Context context, String message, DialogInterface.OnClickListener okListener) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("OK", okListener);
        try {
            builder.show();
        } catch (WindowManager.BadTokenException ignored) {
            ignored.printStackTrace();
        }
    }

    public static void displayOkMessage(Context context, String message, String title, DialogInterface.OnClickListener okListener) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", okListener);
        try {
            builder.show();
        } catch (WindowManager.BadTokenException ignored) {
        }
    }

    public static boolean checkEditTextFieldsIsEmpty(EditText... views) {
        for (EditText view : views) {
            if (view.getText().toString().trim().isEmpty()) return true;
        }
        return false;
    }

    public static void showError(Context context, EditText view, int messageId) {
        view.setError(context.getString(messageId));
        view.requestFocus();
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static void setStatusBarColor(Activity activity, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(activity, color));
        }
    }

    public static void displayAreYouSureDialogTitle(Context context, String message, String title, DialogInterface.OnClickListener okListener) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("Да", okListener);
        builder.setNegativeButton("Нет", okListener);
        builder.create().show();
    }

    public static void displayYesOrNotDialog(Context context, int messageId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener notListener) {
        if (context == null) return;
        displayYesOrNotDialog(context, context.getString(messageId), okListener, notListener);
    }

    public static void displayYesOrNotDialog(Context context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener notListener) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("Да", okListener);
        builder.setNegativeButton("Нет", notListener);
        builder.create().show();
    }

    public static void browseAppInGooglePlayMarket(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public interface OnSoftInputStateListener {

        void onOpened();

        void onClosed();
    }

}
