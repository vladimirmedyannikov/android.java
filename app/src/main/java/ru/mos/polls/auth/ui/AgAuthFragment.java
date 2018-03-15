package ru.mos.polls.auth.ui;


import ru.mos.polls.R;
import ru.mos.polls.auth.vm.AgAuthFragmentVM;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentAgAuthBinding;

public class AgAuthFragment extends MenuBindingFragment<AgAuthFragmentVM, FragmentAgAuthBinding>{

    public static AgAuthFragment instance() {
        return new AgAuthFragment();
    }

    @Override
    protected AgAuthFragmentVM onCreateViewModel(FragmentAgAuthBinding binding) {
        return new AgAuthFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_ag_auth;
    }

    @Override
    public int getMenuResource() {
        return R.menu.confirm;
    }
}
