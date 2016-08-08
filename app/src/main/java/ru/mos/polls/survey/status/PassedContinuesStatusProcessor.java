package ru.mos.polls.survey.status;

import android.view.View;

/**
 * Пройден пользователем, но продолжается (т.е. другие пользователи могут его пройти).
 */
public class PassedContinuesStatusProcessor extends PassedStatusProcessor {

    public PassedContinuesStatusProcessor(boolean isShowPollStats) {
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
