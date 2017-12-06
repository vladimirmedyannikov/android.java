package ru.mos.polls.infosurvey.ui;

import android.os.Bundle;


import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentInfoSurveyBinding;
import ru.mos.polls.infosurvey.vm.InfoSurveyFragmentVM;

/**
 * Created by Trunks on 06.12.2017.
 */

public class InfoSurveyFragment extends NavigateFragment<InfoSurveyFragmentVM, FragmentInfoSurveyBinding> {
    public static final String ARG_POLL_ID = "poll_id";
    public static InfoSurveyFragment newInstance(int idPoll) {
        InfoSurveyFragment f = new InfoSurveyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POLL_ID, idPoll);
        f.setArguments(args);
        return f;
    }

    @Override
    protected InfoSurveyFragmentVM onCreateViewModel(FragmentInfoSurveyBinding binding) {
        return new InfoSurveyFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_info_survey;
    }
}
