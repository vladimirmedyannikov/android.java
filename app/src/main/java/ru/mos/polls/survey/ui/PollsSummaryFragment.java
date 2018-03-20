package ru.mos.polls.survey.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentSurveySummaryBinding;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.polls.survey.SurveySummaryFragment;
import ru.mos.polls.survey.vm.PollsSummaryFragmentVM;

public class PollsSummaryFragment extends MenuBindingFragment<PollsSummaryFragmentVM, FragmentSurveySummaryBinding> implements SurveyActivity.BackPressedListener {

    private Survey survey = null;
    private SurveySummaryFragment.Callback callback = SurveySummaryFragment.Callback.STUB;
    private boolean isHearing;
    public static final String EXTRA_SURVEY_ID = "extra_survey_id";

    public static PollsSummaryFragment newInstance(Survey survey, boolean isHearing, SurveySummaryFragment.Callback callback) {
        PollsSummaryFragment instance = new PollsSummaryFragment();
        instance.setSurvey(survey);
        instance.setHearing(isHearing);
        instance.setCallback(callback);
        return instance;
    }


    @Override
    protected PollsSummaryFragmentVM onCreateViewModel(FragmentSurveySummaryBinding binding) {
        PollsSummaryFragmentVM pollsSummaryFragmentVM = new PollsSummaryFragmentVM(this, binding);
        pollsSummaryFragmentVM.setSurvey(survey);
        pollsSummaryFragmentVM.setHearing(isHearing);
        pollsSummaryFragmentVM.setCallback(callback);
        return pollsSummaryFragmentVM;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_survey_summary;
    }

    @Override
    public int getMenuResource() {
        int menu = 0;
        if (AGApplication.IS_LOCAL_SUBSCRIBE_ENABLE
                && survey != null && (survey.isActive() || survey.isInterrupted())) {
            menu = R.menu.subscribe;
        }
        return menu;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    /**
     * установка отметки специального опроса
     *
     * @param isSpecial true - опрос специальный
     */
    public void setHearing(boolean isSpecial) {
        this.isHearing = isSpecial;
    }

    @Override
    public boolean onBackPressed() {
        getViewModel().onBack();
        return super.onBackPressed();
    }

    @Override
    public boolean onUpPressed() {
        return onBackPressed();
    }

    public void setCallback(SurveySummaryFragment.Callback callback) {
        if (callback == null) {
            this.callback = SurveySummaryFragment.Callback.STUB;
        } else {
            this.callback = callback;
        }
    }

    @Override
    public void onBack() {
        getViewModel().onBack();
    }

    @Override
    public void onUp() {
        onBack();
    }

    @Override
    public void onLocationUpdated() {

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        getViewModel().restoreSavedState(savedInstanceState);
    }
}
