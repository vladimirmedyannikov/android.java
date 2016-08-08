package ru.mos.polls.helpers;

import android.view.View;

/**
 * Логика отображения маркеров опросов.
 * Сейчас есть только один маркер - special, но вероятно будут ещё.
 */
public class SurveyMarkProcessor {

    private final View markerSpecialView;

    public SurveyMarkProcessor(View specialView) {
        markerSpecialView = specialView;
    }

    public void processMark(String mark) {
        if ("special".equals(mark)) {
            markerSpecialView.setVisibility(View.VISIBLE);
        } else {
            markerSpecialView.setVisibility(View.GONE);
        }
    }

}
