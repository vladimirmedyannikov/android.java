package ru.mos.polls.survey.ui;

import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentDetailsExpertsBinding;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.vm.DetailsExpertsFragmentVM;

public class DetailsExpertsFragment extends NavigateFragment<DetailsExpertsFragmentVM, FragmentDetailsExpertsBinding>{
    private static final String EXTRA_EXPERT = "extra_expert";
    private static final String EXTRA_IS_HEARING = "extra_is_hearing";
    private static final String EXTRA_POLL_ID = "extra_poll_id";
    private static final String EXTRA_QUESTION_ID = "extra_question_id";

    public static DetailsExpertsFragment instance(DetailsExpert detailsExpert, long pollId, long questionId, boolean isHearing) {
        DetailsExpertsFragment res = new DetailsExpertsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_EXPERT, detailsExpert);
        bundle.putLong(EXTRA_POLL_ID, pollId);
        bundle.putLong(EXTRA_QUESTION_ID, questionId);
        bundle.putBoolean(EXTRA_IS_HEARING, isHearing);
        res.setArguments(bundle);
        return res;
    }

    @Override
    protected DetailsExpertsFragmentVM onCreateViewModel(FragmentDetailsExpertsBinding binding) {
        return new DetailsExpertsFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_details_experts;
    }

    public DetailsExpert getExtraDetailsExpert() {
        if (getArguments() == null) return null;
        if (getArguments().getSerializable(EXTRA_EXPERT) == null) return null;
        return (DetailsExpert) getArguments().getSerializable(EXTRA_EXPERT);
    }

    public long getExtraPollId() {
        if (getArguments() == null) return 0;
        return getArguments().getLong(EXTRA_POLL_ID, 0);
    }

    public long getExtraQuestionId() {
        if (getArguments() == null) return 0;
        return getArguments().getLong(EXTRA_QUESTION_ID, 0);
    }

    public boolean isExtraHearing() {
        if (getArguments() == null) return false;
        return getArguments().getBoolean(EXTRA_IS_HEARING, false);
    }
}
