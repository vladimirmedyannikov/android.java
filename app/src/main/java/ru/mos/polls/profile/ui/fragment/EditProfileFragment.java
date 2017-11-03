package ru.mos.polls.profile.ui.fragment;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.gui.JugglerActivity;
import me.ilich.juggler.states.State;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentNewEditProfileBinding;
import ru.mos.polls.base.ui.BaseActivity;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.profile.vm.EditProfileFragmentVM;

/**
 * Created by wlTrunks on 14.06.2017.
 */

public class EditProfileFragment extends BindingFragment<EditProfileFragmentVM, FragmentNewEditProfileBinding> {
    public static EditProfileFragment newInstance() {
        EditProfileFragment f = new EditProfileFragment();
        return f;
    }

    public EditProfileFragment() {
    }

    @Override
    protected EditProfileFragmentVM onCreateViewModel(FragmentNewEditProfileBinding binding) {
        return new EditProfileFragmentVM(this, getBinding());
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_new_edit_profile;
    }


    public void navigateToActivityForResult(State state, int code) {
        navigateTo().state(Add.newActivityForResult(state, BaseActivity.class, code));
    }

    public void navigateTo(State state, Class<? extends JugglerActivity> activityClass) {
        navigateTo().state(Add.newActivity(state, activityClass));
    }
}
