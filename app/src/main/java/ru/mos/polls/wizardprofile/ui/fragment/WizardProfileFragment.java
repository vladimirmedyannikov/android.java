package ru.mos.polls.wizardprofile.ui.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardProfileBinding;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.wizardprofile.vm.WizardProfileFragmentVM;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardProfileFragment extends BindingFragment<WizardProfileFragmentVM, FragmentWizardProfileBinding> {

    public static final String ARG_FOR_WIZARD = "for_wizard";
    public static final String ARG_WIZARD_IDS = "profile_personal_quest";
    public static final String ARG_WIZARD_PERCENT = "profile_percent";

    public static WizardProfileFragment newInstance(List<String> list, int percent) {
        WizardProfileFragment f = new WizardProfileFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_WIZARD_IDS, (ArrayList<String>) list);
        args.putInt(ARG_WIZARD_PERCENT, percent);
        f.setArguments(args);
        return f;
    }

    @Override
    protected WizardProfileFragmentVM onCreateViewModel(FragmentWizardProfileBinding binding) {
        return new WizardProfileFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_wizard_profile;
    }
}
