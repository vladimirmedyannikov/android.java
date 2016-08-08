package ru.mos.polls.survey;

import android.view.View;

import ru.mos.polls.survey.variants.SurveyVariant;

public interface StatusProcessor {

    void process(View v);

    void processVotersPercents(View v);

    boolean isEnabled();

    void processButtonContainerVisibility(View buttonContainer);

    void processChecked(View v, SurveyVariant surveyVariant);
}
