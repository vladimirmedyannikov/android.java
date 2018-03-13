package ru.mos.polls.settings.ui.fragment;

import me.ilich.juggler.change.Add;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BaseActivity;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.LayoutSettingsBinding;
import ru.mos.polls.settings.vm.SettingsFragmentVM;
import ru.mos.polls.subscribes.state.SubscribeState;


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

    public void navigateToSubscribe() {
        navigateTo().state(Add.newActivity(new SubscribeState(), BaseActivity.class));
    }

}