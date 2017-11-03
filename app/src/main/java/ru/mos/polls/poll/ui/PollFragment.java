package ru.mos.polls.poll.ui;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentPollBinding;
import ru.mos.polls.poll.vm.PollFragmentVM;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollFragment extends BindingFragment<PollFragmentVM, FragmentPollBinding> {

    public static PollFragment newInstance() {
        return new PollFragment();
    }

    @Override
    protected PollFragmentVM onCreateViewModel(FragmentPollBinding binding) {
        return new PollFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_poll;
    }
}
