package ru.mos.polls.newprofile.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewEditProfileBinding;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.newprofile.ui.vm.EditProfileFragmentVM;

/**
 * Created by wlTrunks on 14.06.2017.
 */

public class EditProfileFragment extends BindingFragment<EditProfileFragmentVM, LayoutNewEditProfileBinding> {
    public static EditProfileFragment newInstance() {
        EditProfileFragment f = new EditProfileFragment();
        return f;
    }

    public EditProfileFragment() {
    }

    @Override
    protected EditProfileFragmentVM onCreateViewModel(LayoutNewEditProfileBinding binding) {
        return new EditProfileFragmentVM(this, getBinding());
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_new_edit_profile;
    }


}
