package ru.mos.polls.survey.status;

import android.view.View;

/**
 * Опрос пройден пользователем и уже завершился (т.е. другие пользователи его не смогут пройти).
 */
public class PassedEndedStatusProcessor extends PassedStatusProcessor {
    public PassedEndedStatusProcessor(boolean isShowPollStats) {
        super(isShowPollStats);
    }

    @Override
    public void process(View v) {
        v.setEnabled(false);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void processButtonContainerVisibility(View buttonContainer) {

    }
}
