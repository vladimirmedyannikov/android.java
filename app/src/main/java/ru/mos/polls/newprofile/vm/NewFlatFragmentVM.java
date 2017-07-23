package ru.mos.polls.newprofile.vm;

import android.os.Bundle;

import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.databinding.LayoutNewFlatBinding;
import ru.mos.polls.newprofile.base.vm.MenuFragmentVM;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;

/**
 * Created by Trunks on 23.07.2017.
 */

public class NewFlatFragmentVM extends MenuFragmentVM<NewFlatFragment, LayoutNewFlatBinding> {
    public static final int FLAT_TYPE_REGISTRATION = 12234;
    public static final int FLAT_TYPE_RESIDENCE = 11223;
    public static final int FLAT_TYPE_WORK = 11132;
    int flatType;
    Flat flat;

    public NewFlatFragmentVM(NewFlatFragment fragment, LayoutNewFlatBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutNewFlatBinding binding) {
        Bundle extras = getFragment().getArguments();
        flatType = extras.getInt(NewFlatFragment.ARG_FLAT_TYPE);
        flat = (Flat) extras.get(NewFlatFragment.ARG_FLAT);
    }
}
