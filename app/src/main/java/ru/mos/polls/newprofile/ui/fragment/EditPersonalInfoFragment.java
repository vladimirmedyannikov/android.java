package ru.mos.polls.newprofile.ui.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewEditPersonalInfoBinding;
import ru.mos.polls.newprofile.base.ui.MenuBindingFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoFragment extends MenuBindingFragment<EditPersonalInfoFragmentVM, LayoutNewEditPersonalInfoBinding> {

    public static final String ARG_PERSONAL_INFO = "arg_personal_info";
    public static final String ARG_AGUSER = "arg_aguser";

    public static EditPersonalInfoFragment newInstance(AgUser agUser, int personalType) {
        EditPersonalInfoFragment f = new EditPersonalInfoFragment();
        Bundle args = new Bundle(2);
        args.putInt(ARG_PERSONAL_INFO, personalType);
        args.putSerializable(ARG_AGUSER, agUser);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_new_edit_personal_info;
    }

    @Override
    public int getMenuResource() {
        return R.menu.confirm;
    }
}
