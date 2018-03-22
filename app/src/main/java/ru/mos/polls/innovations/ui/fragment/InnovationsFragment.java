package ru.mos.polls.innovations.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentInnovationsListBinding;
import ru.mos.polls.innovations.vm.InnovationsFragmentVM;


public class InnovationsFragment extends BindingFragment<InnovationsFragmentVM, FragmentInnovationsListBinding> {
    public static InnovationsFragment newInstance() {
        return new InnovationsFragment();
    }

    @Override
    protected InnovationsFragmentVM onCreateViewModel(FragmentInnovationsListBinding binding) {
        return new InnovationsFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_innovations_list;
    }
}
