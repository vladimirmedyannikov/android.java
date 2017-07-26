package ru.mos.polls.newprofile.ui.fragment;

import android.os.Bundle;
import android.view.View;

import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewFlatBinding;
import ru.mos.polls.newprofile.base.ui.MenuBindingFragment;
import ru.mos.polls.newprofile.vm.NewFlatFragmentVM;

/**
 * Created by Trunks on 04.07.2017.
 */

public class NewFlatFragment extends MenuBindingFragment<NewFlatFragmentVM, LayoutNewFlatBinding> {

    public static final String ARG_FLAT = "arg_flat";
    public static final String ARG_FLAT_TYPE = "arg_flat_type";
    public static final String ARG_HIDE_WARNING_FOR_ADD_FLATS = "arg_hide_warning_for_add_flats";

    public static NewFlatFragment newInstance(Flat flat, int flatType) {
        return newInstance(flat, flatType, false);
    }

    public static NewFlatFragment newInstanceHideWarning(Flat flat, int flatType) {
        return newInstance(flat, flatType, true);
    }

    public static NewFlatFragment newInstance(Flat flat, int flatType, boolean hideWarning) {
        NewFlatFragment f = new NewFlatFragment();
        Bundle args = new Bundle(3);
        args.putInt(ARG_FLAT_TYPE, flatType);
        args.putSerializable(ARG_FLAT, flat);
        args.putBoolean(ARG_HIDE_WARNING_FOR_ADD_FLATS, hideWarning);
        f.setArguments(args);
        return f;
    }

    public NewFlatFragment() {
    }

    @Override
    protected NewFlatFragmentVM onCreateViewModel(LayoutNewFlatBinding binding) {
        return new NewFlatFragmentVM(this, getBinding());
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_new_flat;
    }

    @Override
    public int getMenuResource() {
        return R.menu.new_flat;
    }
}
