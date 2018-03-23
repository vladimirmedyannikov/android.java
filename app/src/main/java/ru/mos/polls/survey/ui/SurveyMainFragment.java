package ru.mos.polls.survey.ui;


import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentMainSurveyBinding;
import ru.mos.polls.survey.vm.SurveyMainFragmentVM;

public class SurveyMainFragment extends NavigateFragment<SurveyMainFragmentVM, FragmentMainSurveyBinding> {
    public static final String EXTRA_SURVEY_ID = "extra_survey_id";
    public static final String EXTRA_IS_HEARING = "extra_is_hearing";
    public static final String EXTRA_RESULT_SURVEY_STATE = "extra_result_survey_state";
    public static final String IS_ACTIVITY_RESTARTED = "extra_is_activity_resturt";

    public static final int REQUEST_POLLS = 0;

    public static SurveyMainFragment instance(long pollId, boolean isHearing) {
        SurveyMainFragment fragment = new SurveyMainFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_SURVEY_ID, pollId);
        bundle.putBoolean(EXTRA_IS_HEARING, isHearing);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected SurveyMainFragmentVM onCreateViewModel(FragmentMainSurveyBinding binding) {
        return new SurveyMainFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_main_survey;
    }

    @Override
    public boolean onBackPressed() {
        getViewModel().onBackPressed();
        return super.onBackPressed();
    }
}
