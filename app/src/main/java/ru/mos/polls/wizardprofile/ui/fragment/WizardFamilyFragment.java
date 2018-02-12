package ru.mos.polls.wizardprofile.ui.fragment;

import android.os.Bundle;

import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardFamilyBinding;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.wizardprofile.vm.WizardFamilyFragmentVM;

/**
 * Created by Trunks on 31.07.2017.
 */

public class WizardFamilyFragment extends NavigateFragment<WizardFamilyFragmentVM, FragmentWizardFamilyBinding> {
    public static final String ARG_AGUSER = "arg_aguser";


    public static WizardFamilyFragment newInstance(AgUser agUser) {
        WizardFamilyFragment f = new WizardFamilyFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_AGUSER, agUser);
        f.setArguments(args);
        return f;
    }

    @Override
    protected WizardFamilyFragmentVM onCreateViewModel(FragmentWizardFamilyBinding binding) {
        return new WizardFamilyFragmentVM(this, binding);
    }


    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_wizard_family;
    }

    @Override
    public void doRequestAction() {
        getViewModel().wizardAction();
    }
}
