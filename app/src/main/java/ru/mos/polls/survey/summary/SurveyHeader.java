package ru.mos.polls.survey.summary;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.questions.SurveyQuestion;

public class SurveyHeader extends FrameLayout {

    private TextView title;
    private TextView price;
    private TextView details;
    private TextView kind;
    private TextView reasonMark;
    private SurveyTitleView surveyTitleView;

    public SurveyHeader(Context context) {
        super(context);
        init();
    }

    public SurveyHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SurveyHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SurveyHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        removeAllViews();
        View headerView = getView();
        addView(headerView);
    }

    public void display(Survey survey) {
        if (!survey.getKind().isHearing()) {
            displayPrice(survey);
            displaySpecialMark(survey);
        }
        surveyTitleView.display(survey);
    }

    public void displayHearingInfo(Fragment fragment, Survey survey) {
        surveyTitleView.displayHearingInfo(fragment, survey);
    }

    public void display(Survey survey, SurveyQuestion surveyQuestion) {
        if (!survey.getKind().isHearing()) {
            displayPrice(survey);
            displaySpecialMark(survey);
        }
        surveyTitleView.display(survey, surveyQuestion);
    }

    public void setStateListener(SurveyTitleView.StateListener stateListener) {
        surveyTitleView.setStateListener(stateListener);
    }

    public void scrollToBegin(ListView view) {
        surveyTitleView.scrollToBegin(view);
    }

    public void scrollToEnd(ListView view) {
        surveyTitleView.scrollToEnd(view);
    }

    public void scrollToBegin(ScrollView view) {
        surveyTitleView.scrollToBegin(view);
    }

    public void scrollToEnd(ScrollView view) {
        surveyTitleView.scrollToEnd(view);
    }

    private View getView() {
        View result = View.inflate(getContext(), R.layout.survey_header, null);
        title = ButterKnife.findById(result, R.id.title);
        price = ButterKnife.findById(result, R.id.price);
        details = ButterKnife.findById(result, R.id.details);
        kind = ButterKnife.findById(result, R.id.kind);
        reasonMark = ButterKnife.findById(result, R.id.reasonMark);
        surveyTitleView = ButterKnife.findById(result, R.id.surveyTitleView);
        return result;
    }

    private void displayPrice(Survey survey) {
        price.setText(survey.getFormattedTitle(getContext()));
        price.setTextColor(getResources().getColor(survey.getColorForTitle()));
        price.setVisibility(survey.isActive() && survey.getPoints() == 0 ? GONE : VISIBLE);
    }

    private void displaySpecialMark(Survey survey) {
        boolean active = survey.isActive() || survey.isInterrupted();
        int visibility = GONE;
        if (active && !survey.getKind().isStandart()) {
            visibility = VISIBLE;
            int color = getContext().getResources().getColor(survey.getKind().getColor());
            kind.setTextColor(color);
            String value = survey.getKind().getLabel();
            if (survey.getKind().isHearing()) {
                SimpleDateFormat sdf = new SimpleDateFormat("до dd.MM.yyyy");
                value = sdf.format(survey.getEndDate());
            }
            kind.setText(value);
        }
        kind.setVisibility(visibility);
    }
}
