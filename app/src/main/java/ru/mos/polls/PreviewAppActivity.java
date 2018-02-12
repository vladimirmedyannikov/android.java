package ru.mos.polls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.subscribes.controller.SubscribesUIController;

/**
 * Экран для ввода email и подписок пользователя
 * Показывается один раз при первом входе в приложение после авторизации
 * @since 1.9.4
 */
public class PreviewAppActivity extends ToolbarAbstractActivity {
    private static final String PREFS = "email_prefs";
    private static final String WAS_SHOWED = "was_showed";

    public static void start(Context context) {
        Intent start = new Intent(context, PreviewAppActivity.class);
        context.startActivity(start);
    }

    public static boolean isNeedPreview(Context context) {
        return !PreviewAppActivity.wasShowed(context)
                && !PreviewAppActivity.hasEmail(context);
    }

    public static boolean hasEmail(Context context) {
        AgUser agUser = new AgUser(context);
        return !TextUtils.isEmpty(agUser.getEmail());
    }

    public static boolean wasShowed(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        return preferences.getBoolean(WAS_SHOWED, false);
    }

    public static void setShowed(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        preferences.edit().putBoolean(WAS_SHOWED, true).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEmailView();
    }

    private void setEmailView() {
        SubscribesUIController subscribesUIController = new SubscribesUIController(this);
        SubscribesUIController.EmailHelpListener emailHelpListener = new SubscribesUIController.EmailHelpListener() {
            @Override
            public void onSkip() {
                cancel();
            }

            @Override
            public void onSave(QuestMessage questMessage) {
                cancel();
            }

            @Override
            public void onError() {
            }
        };
        View view = subscribesUIController.getViewForEmail(emailHelpListener);
        setContentView(view);
    }

    private void cancel() {
        setShowed(PreviewAppActivity.this);
        finish();
    }

}
