package ru.mos.polls.base.ui;


import android.databinding.ViewDataBinding;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.change.Remove;
import me.ilich.juggler.gui.JugglerActivity;
import me.ilich.juggler.states.State;
import ru.mos.polls.BR;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.profile.vm.WizardRequestInterfaceListener;

public abstract class NavigateFragment<VM extends FragmentViewModel, B extends ViewDataBinding> extends BindingFragment<VM, B> implements WizardRequestInterfaceListener {

    public void navigateToActivityForResult(State state, int code) {
        navigateTo().state(Add.newActivityForResult(state, BaseActivity.class, code));
    }

    public void navigateTo(State state, Class<? extends JugglerActivity> activityClass) {
        navigateTo().state(Add.newActivity(state, activityClass));
    }

    public void navigateToCloseCurrActivity() {
        navigateTo().state(Remove.closeCurrentActivity());
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void doRequestAction() {

    }
}
