package ru.mos.polls.survey.summary;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.questions.SurveyQuestion;


public class QuestionsView extends LinearLayout {
    private Drawable answeredDrawable, passedDrawable;

    public QuestionsView(Context context) {
        super(context);
        init();
    }

    public QuestionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public QuestionsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutParams(attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    private void setLayoutParams(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.QuestionsView,
                0, 0);

        try {
            answeredDrawable = a.getDrawable(R.styleable.QuestionsView_drawableAnswered);
            passedDrawable = a.getDrawable(R.styleable.QuestionsView_drawablePassed);
        } finally {
            a.recycle();
        }

    }

    public void display(Survey survey, Callback callback) {
        removeAllViews();
        if (callback == null) {
            callback = Callback.STUB;
        }

        TextView header = (TextView) View.inflate(getContext(), R.layout.layout_title_questions, null);
        if (survey.getKind().isHearing()) {
            header.setText(getContext().getString(R.string.your_position));
        }
        addView(header);

        int index = 0;
        if (survey != null) {
            for (SurveyQuestion surveyQuestion : survey.getFilteredQuestionList()) {
                View surveyQuestionView = View.inflate(getContext(), R.layout.item_survey_question, null);

                setTitle(survey, surveyQuestionView, surveyQuestion, ++index);
                setShortQuestion(surveyQuestionView, surveyQuestion);
                setCallback(surveyQuestionView, survey, surveyQuestion, callback);

                addView(surveyQuestionView);
                /**
                 * Для публичного слушания выводим
                 * только первый главный вопрос
                 */
                if (survey.getKind().isHearing()) {
                    break;
                }
            }
        }
    }

    public void setTitle(Survey survey, View view, SurveyQuestion surveyQuestion, int index) {
        TextView titleView = ButterKnife.findById(view, R.id.title);
        String title = String.format(getContext().getString(R.string.title_survey_question), index);
        titleView.setText(title);
        /**
         * отображаем прогресс вопроса
         * если голосование прошедшее,
         * то отображаем вопросы как не прошедшие
         */
        boolean isVerifyOk = false;
        try {
            surveyQuestion.verify();
            isVerifyOk = true;
        } catch (VerificationException ignored) {
        }
        ImageView progress = ButterKnife.findById(view, R.id.progress);
        Drawable drawable = answeredDrawable;
        if (survey.isOld() || !isVerifyOk) {
            drawable = passedDrawable;
        }
        progress.setImageDrawable(drawable);
        if (survey.getKind().isHearing()) {
            titleView.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
        }
    }

    public void setShortQuestion(View view, SurveyQuestion surveyQuestion) {
        TextView shortQuestionView = ButterKnife.findById(view, R.id.shortQuestion);
        shortQuestionView.setText(surveyQuestion.getQuestionText().getQuestion());
    }

    private void setCallback(View view, final Survey survey, final SurveyQuestion surveyQuestion, final Callback callback) {
        view.findViewById(R.id.questionContainer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onQuestionClicked(survey, surveyQuestion.getId());
            }
        });
    }

    public interface Callback {
        Callback STUB = new Callback() {
            @Override
            public void onQuestionClicked(Survey survey, long questionId) {
            }
        };

        void onQuestionClicked(Survey survey, long questionId);
    }

}
