package ru.mos.polls.innovation.gui.fragment;

import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.helpers.TitleHelper;


public class ActiveInnovationsFragment extends AbstractInnovationsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleHelper.setTitle(getActivity(), R.string.title_novelty);
    }

    @Override
    protected boolean isPagingEnable() {
        return true;
    }
}
