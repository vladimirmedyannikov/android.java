package ru.mos.polls.newprofile.ui.fragment;

import ru.mos.polls.databinding.FragmentPguAuthBinding;
import ru.mos.polls.newprofile.base.ui.MenuBindingFragment;
import ru.mos.polls.newprofile.vm.PguAuthFragmentVM;

/**
 * Created by Trunks on 03.08.2017.
 */

public class PguAuthFragment extends MenuBindingFragment<PguAuthFragmentVM,FragmentPguAuthBinding> {
    @Override
    protected PguAuthFragmentVM onCreateViewModel(FragmentPguAuthBinding binding) {
        return null;
    }

    @Override
    public int getVariable() {
        return 0;
    }

    @Override
    public int getLayoutResources() {
        return 0;
    }

    @Override
    public int getMenuResource() {
        return 0;
    }
}
