package ru.mos.polls.newprofile.ui.fragment;

import android.os.Bundle;

import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentCustomFlatBinding;
import ru.mos.polls.newprofile.base.ui.MenuBindingFragment;
import ru.mos.polls.newprofile.vm.CustomFlatFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 03.08.2017.
 */

public class CustomFlatFragment extends MenuBindingFragment<CustomFlatFragmentVM, FragmentCustomFlatBinding> {
    public static final String EXTRA_FLAT = "extra_flat";
    public static final String EXTRA_STREET = "extra_street";
    public static final String EXTRA_HOUSE = "extra_house";
    public static final int REQUEST_FLAT = 101;

    public static CustomFlatFragment newInstanceForWizard(Flat flat, boolean hideWarning, String street, String house) {
        return newInstance(flat, hideWarning, street, house, true);
    }

    public static CustomFlatFragment newInstance(Flat flat, boolean hideWarning, String street, String house, boolean forWizard) {
        CustomFlatFragment f = new CustomFlatFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_STREET, street);
        args.putString(EXTRA_HOUSE, house);
        args.putSerializable(EXTRA_FLAT, flat);
        args.putBoolean(NewFlatFragment.ARG_HIDE_WARNING_FOR_ADD_FLATS, hideWarning);
        args.putBoolean(WizardProfileFragment.ARG_FOR_WIZARD, forWizard);
        f.setArguments(args);
        return f;
    }

    @Override
    protected CustomFlatFragmentVM onCreateViewModel(FragmentCustomFlatBinding binding) {
        return new CustomFlatFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_custom_flat;
    }

    @Override
    public int getMenuResource() {
        return R.menu.new_flat;
    }
}
