package ru.mos.polls.survey.status;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Опрос активен, то есть не ещё пройден пользовтаелем и доступен к прохождению.
 */
public class ActiveStatusProcessor implements StatusProcessor {

    @Override
    public void process(View v) {
        v.setEnabled(true);
    }

    @Override
    public void processVotersPercents(View v) {
        v.setVisibility(View.GONE);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void processButtonContainerVisibility(View buttonContainer) {

    }

    @Override
    public void processChecked(View v, SurveyVariant surveyVariant) {
        if (v instanceof TextView && surveyVariant.isChecked()) {
            ((TextView) v).setTypeface(null, Typeface.BOLD);
        }
    }

}
