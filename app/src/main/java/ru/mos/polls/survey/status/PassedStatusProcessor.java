package ru.mos.polls.survey.status;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Отображение вариантов ответа для пройденного голосования
 *
 * @since 1.9.2
 */
public abstract class PassedStatusProcessor implements StatusProcessor {
    protected boolean isShowPollStats;

    public PassedStatusProcessor(boolean isShowPollStats) {
        this.isShowPollStats = isShowPollStats;
    }

    /**
     * Не отображаем проценты прохождения,
     * если за показ промежуточных результатов запрещен
     * @see ru.mos.polls.survey.Survey#isShowPollStats()
     * @param v
     */
    @Override
    public void processVotersPercents(View v) {
        v.setVisibility(isShowPollStats ? View.VISIBLE : View.GONE);
    }

    @Override
    public void processChecked(View v, SurveyVariant surveyVariant) {
        if (v instanceof TextView && surveyVariant.isChecked()) {
            ((TextView) v).setTypeface(null, Typeface.BOLD);
            ((TextView) v).setTextColor(v.getContext().getResources().getColor(android.R.color.black));
        }
    }
}
