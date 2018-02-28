package ru.mos.polls.poll.ui;

import android.os.Bundle;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.poll.vm.PollActiveFragmentVM;
import ru.mos.polls.poll.vm.PollBaseFragmentVM;
import ru.mos.polls.poll.vm.PollOldFragmentVM;

public class PollBaseFragment extends BindingFragment<PollBaseFragmentVM, FragmentTabPollBinding> {

    public static String ARG_POLL_TYPE = "arg_poll_type";

    public static PollBaseFragment newInstance(int type) {
        PollBaseFragment f = new PollBaseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POLL_TYPE, type);
        f.setArguments(args);
        return f;
    }

    @Override
    protected PollBaseFragmentVM onCreateViewModel(FragmentTabPollBinding binding) {
        Bundle extras = getArguments();
        int pollType = 0;
        if (extras != null) {
            pollType = extras.getInt(PollBaseFragment.ARG_POLL_TYPE);
        }
        if (pollType == PollBaseFragmentVM.ARG_ACTIVE_POLL) {
            return new PollActiveFragmentVM(this, binding);
        }
        return new PollOldFragmentVM(this, binding);
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
