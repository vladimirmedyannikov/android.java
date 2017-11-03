package ru.mos.polls.profile.ui.fragment;

import android.os.Bundle;

import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentNewFlatBinding;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.profile.vm.NewFlatFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class NewFlatFragment extends MenuBindingFragment<NewFlatFragmentVM, FragmentNewFlatBinding> {

    public static final String ARG_FLAT = "arg_flat";
    public static final String ARG_FLAT_TYPE = "arg_flat_type";
    public static final String ARG_HIDE_WARNING_FOR_ADD_FLATS = "arg_hide_warning_for_add_flats";

    public static NewFlatFragment newInstance(Flat flat, int flatType) {
        return newInstance(flat, flatType, false);
    }

    public static NewFlatFragment newInstanceForWizard(Flat flat, int flatType) {
        return newInstance(flat, flatType, true);
    }

    public static NewFlatFragment newInstance(Flat flat, int flatType, boolean hideWarning) {
        NewFlatFragment f = new NewFlatFragment();
        Bundle args = new Bundle(3);
        args.putInt(ARG_FLAT_TYPE, flatType);
        args.putSerializable(ARG_FLAT, flat);
        args.putBoolean(WizardProfileFragment.ARG_FOR_WIZARD, hideWarning);
        f.setArguments(args);
        return f;
    }

    public NewFlatFragment() {
    }

    @Override
    protected NewFlatFragmentVM onCreateViewModel(FragmentNewFlatBinding binding) {
        return new NewFlatFragmentVM(this, getBinding());
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_new_flat;
    }

    @Override
    public int getMenuResource() {
        return R.menu.new_flat;
    }
}
