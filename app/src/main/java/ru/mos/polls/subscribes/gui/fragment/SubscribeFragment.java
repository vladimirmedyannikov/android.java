package ru.mos.polls.subscribes.gui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentSubscribeBinding;
import ru.mos.polls.subscribes.vm.SubscribeFragmentVM;


public class SubscribeFragment extends BindingFragment<SubscribeFragmentVM, FragmentSubscribeBinding> {

    public static SubscribeFragment instance() {
        return new SubscribeFragment();
    }

    @Override
    protected SubscribeFragmentVM onCreateViewModel(FragmentSubscribeBinding binding) {
        return new SubscribeFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_subscribe;
    }
}
