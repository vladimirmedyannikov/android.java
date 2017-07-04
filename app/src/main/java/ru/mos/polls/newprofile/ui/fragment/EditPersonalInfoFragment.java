package ru.mos.polls.newprofile.ui.fragment;

import android.os.Bundle;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewEditPersonalInfoBinding;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoFragment extends BindingFragment<EditPersonalInfoFragmentVM, LayoutNewEditPersonalInfoBinding> {

    public static final String ARG_PERSONAL_INFO = "arg_personal_info";

    public static EditPersonalInfoFragment newInstance(int type) {
        EditPersonalInfoFragment f = new EditPersonalInfoFragment();
        Bundle args = new Bundle(1);
        args.putSerializable(ARG_PERSONAL_INFO, type);
        f.setArguments(args);
        return f;
    }

    public EditPersonalInfoFragment() {
    }

    @Override
    protected EditPersonalInfoFragmentVM onCreateViewModel(LayoutNewEditPersonalInfoBinding binding) {
        return new EditPersonalInfoFragmentVM(this, getBinding());
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_new_edit_personal_info;
    }
}
