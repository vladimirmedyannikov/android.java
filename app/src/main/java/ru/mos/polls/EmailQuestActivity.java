package ru.mos.polls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.subscribes.controller.SubscribesUIController;

/**
 * Задание на заполнение email пользователя
 * ui экрана берется из {@link ru.mos.polls.subscribes.controller.SubscribesUIController}
 *
 * @since 1.9
 */
public class EmailQuestActivity extends ToolbarAbstractActivity {
    public static void startActivity(Context context) {
        Intent start = new Intent(context, EmailQuestActivity.class);
        context.startActivity(start);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());
        TitleHelper.setTitle(this, getString(R.string.title_email_quest));
    }

    private View getView() {
        SubscribesUIController subscribesUIController
                = new SubscribesUIController(this);
        SubscribesUIController.EmailHelpListener emailHelpListener = new SubscribesUIController.EmailHelpListener() {
            @Override
            public void onSkip() {
            }

            @Override
            public void onSave(QuestMessage questMessage) {
                if (questMessage != null && !questMessage.isEmpty()) {
                    questMessage.show(EmailQuestActivity.this, true);
                } else {
                    finish();
                }
            }

            @Override
            public void onError() {
            }
        };
        return subscribesUIController.getViewForEmail(emailHelpListener, true);
    }
}
