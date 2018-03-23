package ru.mos.polls.profile.ui.fragment;

import android.os.Bundle;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentNewEditProfileBinding;
import ru.mos.polls.profile.vm.EditProfileFragmentVM;

public class EditProfileFragment extends NavigateFragment<EditProfileFragmentVM, FragmentNewEditProfileBinding> {
    public static EditProfileFragment newInstance(int coordScreen) {
        EditProfileFragment f = new EditProfileFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(EditProfileFragmentVM.ARG_COORD, coordScreen);
        f.setArguments(bundle);
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

    /**
     *
      * @return координату на которой были на экране {@link ru.mos.polls.profile.vm.InfoTabFragmentVM}
     */
    public int getCoordScroll() {
        if (getArguments() != null) {
            return getArguments().getInt(EditProfileFragmentVM.ARG_COORD, 0);
        } else {
            return 0;
        }
    }
}
