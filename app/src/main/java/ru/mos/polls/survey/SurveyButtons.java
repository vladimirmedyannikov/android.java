package ru.mos.polls.survey;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.summary.ProgressView;


public class SurveyButtons extends LinearLayout {
    private TextView nextQuestionButton, prevQuestionButton;
    private TextView resultButton;
    private Survey survey;
    private SurveyFragment fragment;

    public SurveyButtons(Context context) {
        super(context);
        init();
    }

    public SurveyButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SurveyButtons(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("NewApi")
    public SurveyButtons(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        removeAllViews();
        View v = getView();
        addView(v);
    }

    public void setFragment(SurveyFragment fragment) {
        this.fragment = fragment;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    protected View getView() {
        View result = View.inflate(getContext(), R.layout.layout_button_container_survey, null);
        nextQuestionButton = ButterKnife.findById(result, R.id.nextQuestion);
        prevQuestionButton = ButterKnife.findById(result, R.id.prevQuestion);
        resultButton = ButterKnife.findById(result, R.id.result);
        resultButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.doResult();
            }
        });
        nextQuestionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.doNext();
                fragment.checkingForParentId();
            }
        });
        prevQuestionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.doPrev();
                fragment.checkingForParentId();
            }
        });
        ButterKnife.bind(this);
        return result;
    }

    public void renderButtons() {
        renderResultButton();
        renderQuestButtons();
        processButtonContainerForOld();
    }

    private void setShareButton() {
        /**
         * Голосование пройдено {@link Survey}
         */
        resultButton.setEnabled(true);
        resultButton.setVisibility(View.VISIBLE);
        resultButton.setTextColor(getResources().getColor(R.color.white));
        resultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.poll_navigation_green_selector));
        resultButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.line, 0, R.drawable.line, 0);
        resultButton.setText(R.string.share);
    }

    private void setResultButtonDone() {
        /**
         * Перекрашиваем кнопку "Завершить голосование" {@link #resultButton} в зависмости от того, есть ли ответ на вопрос
         * Первоначально окрашиваем ее так, будто есть ответ на вопрос {@link SurveyQuestion}
         */
        resultButton.setVisibility(View.VISIBLE);
        resultButton.setTextColor(getResources().getColor(R.color.white));
        resultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_poll_result_button));
        resultButton.setEnabled(true);
    }

    private void renderResultButton() {
        /**
         * Если в голосовании один вопрос, то убираем панель прогресса {@link ProgressView}
         * и кнопки навигации по вопросам "назад" {@link #prevQuestionButton} и "вперед" {@link #nextQuestionButton}
         * При это кнопку "Завершить гоосование" (она же "Поделиться") {@link #resultButton} окрашивается в определенные цвета
         * и расширятеся по всей ширине экрана
         */
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) resultButton.getLayoutParams();
        if (survey.getFilteredQuestionList().size() == 1) {
            params.setMargins(0, 0, 0, 0);
            resultButton.setLayoutParams(params);
            setResultButtonDone();
            if (!isSurveyVerifyOk()) {
                /**
                 * Ответа на вопрос голосования {@link Survey} нет
                 */
                resultButton.setTextColor(getResources().getColor(R.color.gray_light));
                resultButton.setBackgroundColor(getResources().getColor(R.color.gray_background_next_or_prev));
                resultButton.setEnabled(false);
            }
            if (survey.isPassed()) {
                setShareButton();
            }
        } else {
            /**
             * В голосовании {@link Survey} несколько вопросов {@link SurveyQuestion}<br/>
             * Навигационные кнопки показываем {@link #prevQuestionButton} {@link #nextQuestionButton}
             */
            params.setMargins((int) convertDpToPixel(50), 0, (int) convertDpToPixel(50), 0);
            resultButton.setLayoutParams(params);
            if (survey.isPassed()) {
                setShareButton();
            } else {
                /**
                 * Голосование не пройдено {@link Survey}
                 */
                if (!isSurveyVerifyOk()) {
                    resultButton.setVisibility(View.GONE);
                } else {
                    setResultButtonDone();
                    if (survey.isPassed()) {
                        resultButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.line, 0, R.drawable.line, 0);
                    }
                }
            }
        }
    }

    private void renderQuestButtons() {
        /**
         * Отрисовка состояние навигационных кнопок {@link #prevQuestionButton} {@link #nextQuestionButton}
         */
        if (survey.getFilteredQuestionList().size() == 1) {
            /**
             * Не показываем , если один вопрос {@link SurveyQuestion}
             */
            nextQuestionButton.setVisibility(View.GONE);
            prevQuestionButton.setVisibility(View.GONE);
        } else {
            /**
             * Общая логика отрисовки состояния навигационных кнопок следующий<br/>
             * Если на странице вопроса голосования не выбран вариант ответа, то кнопки серые {@link R.drawable.poll_navigation_gray_selector}<br/>
             * Если на странице вопроса голосования выбран вариант ответа, то кнопки зеленые {@link R.drawable.poll_navigation_green_selector} <br/>
             * Для первого и последнего вопроса в голосовании кнопки неактивные, то есть белдно-серые {@link R.color.gray_light}<br/>
             */
            boolean hasAnswerForCurrentQuestion = hasAnswerForCurrentQuestion();
            boolean hasNext = survey.checkNext();
            boolean hasPrev = survey.getCurrentPageIndex() > 0;
            renderQuestButton(nextQuestionButton, hasNext, hasAnswerForCurrentQuestion);
            renderQuestButton(prevQuestionButton, hasPrev, hasAnswerForCurrentQuestion);
        }
    }

    private void renderQuestButton(TextView view, boolean hasNext, boolean hasAnswer) {
        view.setEnabled(hasNext);
        view.setTextColor(getResources().getColor(R.color.black_light));
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.poll_navigation_gray_selector));
        setDrawwableArrowGrayDark(view);
        if (!hasNext) {
            view.setTextColor(getResources().getColor(R.color.gray_light));
            setDrawwableArrowGrayLight(view);
        }
        if (hasAnswer) {
            if (hasNext) {
                if (!survey.isOld()) {
                    view.setTextColor(getResources().getColor(R.color.white));
                    view.setBackgroundDrawable(getResources().getDrawable(R.drawable.poll_navigation_green_selector));
                    setDrawwableArrowWhite(view);
                }
            } else {
                view.setTextColor(getResources().getColor(R.color.gray_light));
            }
        }
        view.setVisibility(View.VISIBLE);
    }

    private void setDrawwableArrowWhite(TextView view) {
        if (view == nextQuestionButton) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_white_right, 0);
        }
        if (view == prevQuestionButton) {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_white_left, 0, 0, 0);
        }
    }

    private void setDrawwableArrowGrayLight(TextView view) {
        if (view == nextQuestionButton) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_lightgrey_right, 0);
        }
        if (view == prevQuestionButton) {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_lightgrey_left, 0, 0, 0);
        }
    }

    private void setDrawwableArrowGrayDark(TextView view) {
        if (view == nextQuestionButton) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_darkgrey_right, 0);
        }
        if (view == prevQuestionButton) {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_darkgrey_left, 0, 0, 0);
        }
    }

    public void checkSurveyStatus(long questionId, SharedPreferencesSurveyManager manager, final SurveyFragment.Callback callback) {
        int pageIndex = survey.getQuestionsOrder().indexOf(questionId);
        if (survey.isActive() || survey.isInterrupted()) {
            manager.fill(survey);
            try {
                survey.setCurrentPageIndex(pageIndex);
            } catch (IndexOutOfBoundsException ignored) {
                survey.setCurrentPageIndex(0);
            }
            if (survey.getKind().isHearing()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) resultButton.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                resultButton.setLayoutParams(params);
                setResultButtonDone();
            }
        } else if (survey.isPassed() || survey.isOld()) {
            survey.setCurrentPageIndex(pageIndex);

            for (SurveyQuestion question : survey.getQuestionsList()) {
                question.setPassed(true);
            }
            if (survey.isPassed()) {
                resultButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
                            @Override
                            public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                                socialPostValue.setId(survey.getId());
                                callback.onPosting(socialPostValue);
                            }

                            @Override
                            public void onCancel() {
                                fragment.getActivity().finish();
                            }
                        };
                        SocialUIController.showSocialsDialogForPoll(fragment.getDisposable(), (BaseActivity) fragment.getActivity(), survey, false, listener, null);
                    }
                });
            } else {
                resultButtonOld();
            }
        }
    }

    private boolean isSurveyVerifyOk() {
        List<SurveyQuestion> questionList = survey.getFilteredQuestionList();
        boolean isAllQuestionHasAnswer = true;
        for (SurveyQuestion question : questionList) {
            try {
                question.verify();
            } catch (VerificationException e) {
                isAllQuestionHasAnswer = false;
            }
        }
        return isAllQuestionHasAnswer;
    }

    private boolean hasAnswerForCurrentQuestion() {
        boolean isVerifyOk = false;
        try {
            long questionId = survey.getCurrentQuestionId();
            SurveyQuestion surveyQuestion = survey.getQuestion(questionId);
            surveyQuestion.verify();
            isVerifyOk = true;
        } catch (VerificationException ignored) {
        }
        return isVerifyOk;
    }

    public float convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void processButtonContainerForOld() {
        if (survey.isOld()) {
            resultButtonOld();
        }
    }

    private void resultButtonOld() {
        if (survey.getFilteredQuestionList().size() > 1) {
            resultButton.setVisibility(View.GONE);
        } else {
            fragment.getActivity().findViewById(R.id.buttonContainer).setVisibility(View.GONE);
        }
    }

    public interface CallBack {

        void doPrev();

        void doNext();

        void doResult();
    }
}
