package ru.mos.polls.survey.hearing.gui.fragment;

import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentExpositionBinding;
import ru.mos.polls.survey.hearing.model.Exposition;
import ru.mos.polls.survey.hearing.vm.ExpositionFragmentVM;

public class ExpositionFragment extends NavigateFragment<ExpositionFragmentVM, FragmentExpositionBinding>{

    public static final String EXTRA_MODEL = "extra_model";
    public static final String EXTRA_TITLE = "extra_title";

    public static ExpositionFragment getInstance(Exposition exposition, String surveyTitle) {
        ExpositionFragment res = new ExpositionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_MODEL, exposition);
        bundle.putString(EXTRA_TITLE, surveyTitle);
        res.setArguments(bundle);
        return res;
    }

    @Override
    protected ExpositionFragmentVM onCreateViewModel(FragmentExpositionBinding binding) {
        return new ExpositionFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_exposition;
    }

    public Exposition getExtraExposition() {
        if (getArguments() == null) return null;
        return (Exposition) getArguments().getSerializable(EXTRA_MODEL);
    }

    public String getExtraTitle() {
        if (getArguments() == null) return "";
        return getArguments().getString(EXTRA_TITLE, "");
    }
}
