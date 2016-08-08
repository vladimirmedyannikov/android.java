package ru.mos.polls.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.questions.SurveyQuestion;

/**
 * Компонент для отображения промежуточных результатов голосования
 * Для отображения промежуточной информации
 * использовать {@link #display(ru.mos.polls.survey.Survey, ru.mos.polls.survey.questions.SurveyQuestion)}
 *
 *     вики метода серверсада для получения необходимой информации по промежуточным голосованиям</a>
 *
 * @since 1.9.2
 */
public class VotersView  extends LinearLayout {
    private static final int NOT_SET = -1;
    private View votersInfoContainerView;
    private TextView votersCountTextView, votersVariantsCountTextView;

    public VotersView(Context context) {
        super(context);
        init();
    }

    public VotersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public VotersView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    private void init() {
        setOrientation(VERTICAL);
        removeAllViews();
        View v = getView();
        addView(v);
    }

    private View getView() {
        View result = View.inflate(getContext(), R.layout.layout_voters_info_container, null);
        votersInfoContainerView = ButterKnife.findById(result, R.id.votersInfoContainer);
        votersCountTextView = ButterKnife.findById(result, R.id.votersCount);
        votersVariantsCountTextView =  ButterKnife.findById(result, R.id.votersVariantsCount);
        return result;
    }

    public void display(Survey survey) {
        display(survey, null);
    }

    public void display(Survey survey, @Nullable SurveyQuestion question) {
        if (survey.isShowPollStats()) {
            switch (survey.getStatus()) {
                case PASSED:
                    if (survey.isEnded()) {
                        findViewById(R.id.votersHint).setVisibility(View.GONE);
                    }
                    votersInfoContainerView.setVisibility(View.VISIBLE);
                    int votersCount = survey.getVotersCount();
                    int votersVariantsCount = NOT_SET;
                    if (question != null) {
                        votersCount = question.getVotersCount();
                        votersVariantsCount = question.getVotersVariantsCount();
                    }
                    votersCountTextView.setText(
                            String.format(getContext().getString(R.string.voters_all_count), votersCount));
                    if (votersVariantsCount != NOT_SET) {
                        votersVariantsCountTextView.setText(
                                String.format(getContext().getString(R.string.voters_variants_all_count), votersVariantsCount));
                        votersVariantsCountTextView.setVisibility(View.VISIBLE);
                    } else {
                        votersVariantsCountTextView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    votersInfoContainerView.setVisibility(View.GONE);
                    break;
            }
        } else {
            votersInfoContainerView.setVisibility(View.GONE);
        }
    }
}
