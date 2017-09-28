package ru.mos.polls.newsupport.ui.fragment;

import me.ilich.juggler.Navigable;
import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.LayoutSupportBinding;
import ru.mos.polls.newsupport.vm.SupportFragmentVM;

/**
 * Created by matek3022 on 13.09.17.
 */

public class SupportFragment extends BindingFragment<SupportFragmentVM, LayoutSupportBinding> {

    public static JugglerFragment instance() {
        return new SupportFragment();
    }

    @Override
    protected SupportFragmentVM onCreateViewModel(LayoutSupportBinding binding) {
        return new SupportFragmentVM(this, binding);
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