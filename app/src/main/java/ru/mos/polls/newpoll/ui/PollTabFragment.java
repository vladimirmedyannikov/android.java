package ru.mos.polls.newpoll.ui;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.newpoll.vm.PollTabFragmentVM;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollTabFragment extends BindingFragment<PollTabFragmentVM, FragmentTabPollBinding> {
    @Override
    protected PollTabFragmentVM onCreateViewModel(FragmentTabPollBinding binding) {
        return new PollTabFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_tab_poll;
    }
}
