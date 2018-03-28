package ru.mos.polls.survey.summary;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.common.view.HtmlTitleView;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.hearing.gui.view.HearingInfoView;
import ru.mos.polls.survey.hearing.model.Exposition;
import ru.mos.polls.survey.hearing.model.Meeting;
import ru.mos.polls.survey.hearing.state.ExpositionState;
import ru.mos.polls.survey.hearing.state.MeetingState;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.ui.SurveyFragment;


public class SurveyTitleView extends HtmlTitleView {
    private int contentChangingType = SIMPLE;

    private TextView price;
    private HearingInfoView hearingInfoView;
    @BindView(R.id.hearingQuestion)
    TextView hearingQuestion;

    private long pollId, questionId;
    private boolean isNeedHearingInfo;

    public SurveyTitleView(Context context) {
        super(context);
    }

    public SurveyTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SurveyTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_survey_title;
    }

    public void setIsNeedHearingInfo(boolean isNeedHearingInfo) {
        this.isNeedHearingInfo = isNeedHearingInfo;
    }

    public void display(Survey survey) {
        if (survey != null) {
            pollId = survey.getId();
            display(survey.getTitle(),
                    survey.getTextShortHtml(),
                    survey.getTextFullHtml());
            displayPriceForHearing(survey);
            /**
             * Выводить заголовок на странице голосования всегда
             */
            displayTitle(survey);
        }
    }

    public void display(Survey survey, SurveyQuestion question) {
        if (question != null) {
            pollId = survey.getId();
            questionId = question.getId();
            SurveyQuestion.Text text = question.getQuestionText();
            display(text.getQuestion(),
                    text.getQuestionShort(),
                    text.getQuestionFull());
            /**
             * Для публичных слушаний логика отображения заголовка меняется
             * Всегда показывает стоимость и заголовок для страницы вопроса {@link SurveyFragment}
             * C версии 2.2.0 показываем стоимость и вопрос для публичного слушания, заголовок убираем
             */
            displayPriceForHearing(survey);
            if (survey.getKind().isHearing()) {
                title.setVisibility(GONE);
                hearingQuestion.setVisibility(VISIBLE);
                hearingQuestion.setText(text.getQuestion());
            }
        }
    }

    public void scrollToBegin(ListView questionContainer) {
        questionContainer.setSelectionFromTop(0, 0);
    }


    public void scrollToEnd(ListView questionContainer) {
        questionContainer.setSelectionFromTop(1, 0);
    }

    public void scrollToBegin(ScrollView header) {
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        scrollTo(header, getTop());
    }

    public void scrollToEnd(ScrollView header) {
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        scrollTo(header, getTop() + getMeasuredHeight());
    }

    public void scrollTo(ScrollView scrollView, int positionY) {
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        scrollView.smoothScrollTo(0, positionY);
    }

    @Override
    protected View getView() {
        View result = super.getView();
        price = ButterKnife.findById(result, R.id.price);
        hearingQuestion = ButterKnife.findById(result, R.id.hearingQuestion);
        hearingInfoView = ButterKnife.findById(result, R.id.hearingInfo);
        hearingInfoView.setVisibility(View.GONE);
        return result;
    }

    @Override
    protected void onExpanded() {
        super.onExpanded();
        Statistics.pollsEnterMoreInfo(pollId, questionId);
        GoogleStatistics.Survey.pollsEnterMoreInfo(pollId, questionId);
    }

    /**
     * Отображаем заголовок голосования, количество баллов и дату проведеения голосования
     * только для публичных слушаний
     *
     * @param survey текущее голосование
     * @since 2.0
     */
    private void displayPriceForHearing(Survey survey) {
        if (survey.getKind().isHearing()) {
            /**
             * display price
             */
            int res = R.array.survey_points_pluse;
            if (survey.isPassed()) {
                res = R.array.poll_passed_points;
            }
            String result = PointsManager.getSuitableString(getContext(), res, survey.getPoints());
            result = String.format(result, survey.getPoints());
            price.setText(survey.isOld() ? survey.getFormattedTitle(getContext()) : result);
            price.setTextColor(getResources().getColor(survey.getColorForTitle()));
            price.setVisibility(View.VISIBLE);
        }
    }

    private void displayTitle(Survey survey) {
        if (survey.getTitle() != null && !TextUtils.isEmpty(survey.getTitle())) {
            title.setText(survey.getTitle());
            title.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Отображение экспозиций и собраний
     */
    public void displayHearingInfo(final NavigateFragment fragment, final Survey survey) {
        if (survey.getKind().isHearing()) {
            hearingInfoView.setVisibility(View.VISIBLE);
            hearingInfoView.display(survey);
            hearingInfoView.setCallback(new HearingInfoView.Callback() {
                @Override
                public void onMeeting(Meeting meeting) {
                    fragment.navigateTo(new MeetingState(survey.getId(), meeting, survey.getTitle()), BaseActivity.class);
                }

                @Override
                public void onExposition(Exposition exposition) {
                    fragment.navigateTo(new ExpositionState(exposition, survey.getTitle()), BaseActivity.class);
                }
            });
        }
    }
}
