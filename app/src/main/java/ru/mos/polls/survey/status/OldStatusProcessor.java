package ru.mos.polls.survey.status;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Опрос устарел. Не понятно, попадают ли в этот статус уже пройденные пользователем опросы.
 */
public class OldStatusProcessor implements StatusProcessor {

    @Override
    public void process(View v) {
        v.setEnabled(false);
    }

    @Override
    public void processVotersPercents(View v) {
        v.setVisibility(View.GONE);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void processButtonContainerVisibility(View buttonContainer) {
        if (buttonContainer != null) {
            buttonContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void processChecked(View v, SurveyVariant surveyVariant) {
        if (v instanceof TextView && surveyVariant.isChecked()) {
            ((TextView) v).setTypeface(null, Typeface.BOLD);
        }
    }

}
