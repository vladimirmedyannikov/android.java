package ru.mos.polls.sourcesvoting.ui;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentSourcesVotingBinding;
import ru.mos.polls.sourcesvoting.vm.SourcesVotingFragmentVM;

/**
 * Created by Trunks on 13.10.2017.
 */

public class SourcesVotingFragment extends BindingFragment<SourcesVotingFragmentVM, FragmentSourcesVotingBinding> {

    public static SourcesVotingFragment newInstance() {
        SourcesVotingFragment f = new SourcesVotingFragment();
        return f;
    }

    @Override
    protected SourcesVotingFragmentVM onCreateViewModel(FragmentSourcesVotingBinding binding) {
        return new SourcesVotingFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_sources_voting;
    }
}
