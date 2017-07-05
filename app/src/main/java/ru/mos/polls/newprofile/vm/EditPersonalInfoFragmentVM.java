package ru.mos.polls.newprofile.vm;

import android.widget.Toast;

import ru.mos.polls.databinding.LayoutNewEditPersonalInfoBinding;
import ru.mos.polls.newprofile.base.vm.MenuFragmentVM;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoFragmentVM extends MenuFragmentVM<EditPersonalInfoFragment, LayoutNewEditPersonalInfoBinding> {
    public static final int PERSONAL_EMAIL = 33344;
    public static final int PERSONAL_FIO = 44333;
    public int personalType;

    public EditPersonalInfoFragmentVM(EditPersonalInfoFragment fragment, LayoutNewEditPersonalInfoBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutNewEditPersonalInfoBinding binding) {
        personalType = getFragment().getArguments().getInt(EditPersonalInfoFragment.ARG_PERSONAL_INFO);
    }

    @Override
    public void confirmAction() {
        Toast.makeText(getFragment().getContext(),"action",Toast.LENGTH_SHORT).show();
    }
}
