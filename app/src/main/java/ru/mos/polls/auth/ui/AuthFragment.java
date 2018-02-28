package ru.mos.polls.auth.ui;

import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.auth.vm.AuthFragmentVM;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentAuthServiceBinding;

public class AuthFragment extends MenuBindingFragment<AuthFragmentVM, FragmentAuthServiceBinding> {
    public static String ARG_PHONE = "arg_phone";

    public static AuthFragment newInstance(String phone) {
        AuthFragment f = new AuthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE, phone);
        f.setArguments(args);
        return f;
    }

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
