package ru.mos.polls.auth.ui;

import ru.mos.polls.R;
import ru.mos.polls.auth.vm.AuthFragmentVM;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentAuthServiceBinding;

/**
 * Created by Trunks on 21.12.2017.
 */

public class AuthFragment extends MenuBindingFragment<AuthFragmentVM, FragmentAuthServiceBinding> {
    @Override
    protected AuthFragmentVM onCreateViewModel(FragmentAuthServiceBinding binding) {
        return new AuthFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_auth_service;
    }

    @Override
    public int getMenuResource() {
        return R.menu.confirm;
    }
}
