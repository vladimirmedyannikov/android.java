package ru.mos.polls.settings.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.LayoutSettingsBinding;
import ru.mos.polls.settings.vm.SettingsFragmentVM;

/**
 * Created by matek3022 on 25.09.17.
 */

public class SettingsFragment extends NavigateFragment<SettingsFragmentVM, LayoutSettingsBinding> {

    public static SettingsFragment instance() {
        return new SettingsFragment();
    }

    @Override
    protected SettingsFragmentVM onCreateViewModel(LayoutSettingsBinding binding) {
        return new SettingsFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_settings;
    }

}