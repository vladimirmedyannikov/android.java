package ru.mos.polls.newprofile.ui.vm;

import android.databinding.ViewDataBinding;

import ru.mos.polls.databinding.LayoutNewEditProfileBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.ui.fragment.EditProfileFragment;

/**
 * Created by wlTrunks on 14.06.2017.
 */

public class EditProfileFragmentVM extends FragmentViewModel<EditProfileFragment, LayoutNewEditProfileBinding>{

    @Override
    protected void initialize(LayoutNewEditProfileBinding binding) {

    }

    public EditProfileFragmentVM(EditProfileFragment fragment, LayoutNewEditProfileBinding binding) {
        super(fragment, binding);
    }
}
