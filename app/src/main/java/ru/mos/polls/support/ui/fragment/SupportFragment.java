package ru.mos.polls.support.ui.fragment;

import android.os.Bundle;

import me.ilich.juggler.Navigable;
import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.LayoutSupportBinding;
import ru.mos.polls.support.vm.SupportFragmentVM;

/**
 * Created by matek3022 on 13.09.17.
 */

public class SupportFragment extends BindingFragment<SupportFragmentVM, LayoutSupportBinding> {

    private static final String ARG_START_WITH_NEW_ACTIVITY = "ru.mos.polls.newsupport.ui.fragment.start_with_new_activity";

    public static JugglerFragment instance(boolean startWithNewActivity) {
        SupportFragment f = new SupportFragment();
        Bundle b = new Bundle(1);
        b.putBoolean(ARG_START_WITH_NEW_ACTIVITY, startWithNewActivity);
        f.setArguments(b);
        return f;
    }

    @Override
    protected SupportFragmentVM onCreateViewModel(LayoutSupportBinding binding) {
        return new SupportFragmentVM(this, binding);
    }

    public boolean isStartWithNewActivity() {
        return getArguments().getBoolean(ARG_START_WITH_NEW_ACTIVITY);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_support;
    }

    @Override
    public Navigable navigateTo() {
        return super.navigateTo();
    }
}