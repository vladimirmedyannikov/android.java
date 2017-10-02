package ru.mos.polls.newinnovation.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentInnovationsListBinding;
import ru.mos.polls.newinnovation.vm.InnovationFragmentVM;

/**
 * Created by Trunks on 02.10.2017.
 */

public class InnovationFragment extends BindingFragment<InnovationFragmentVM, FragmentInnovationsListBinding> {
    public static InnovationFragment newInstance() {
        return new InnovationFragment();
    }

    @Override
    protected InnovationFragmentVM onCreateViewModel(FragmentInnovationsListBinding binding) {
        return new InnovationFragmentVM(this, binding);
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
