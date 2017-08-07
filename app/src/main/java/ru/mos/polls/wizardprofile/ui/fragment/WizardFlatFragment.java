package ru.mos.polls.wizardprofile.ui.fragment;

import android.os.Bundle;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardFamilyBinding;
import ru.mos.polls.databinding.FragmentWizardFlatBinding;
import ru.mos.polls.newprofile.base.ui.NavigateFragment;
import ru.mos.polls.wizardprofile.vm.WizardFamilyFragmentVM;
import ru.mos.polls.wizardprofile.vm.WizardFlatFragmentVM;

/**
 * Created by Trunks on 31.07.2017.
 */

public class WizardFlatFragment extends NavigateFragment<WizardFlatFragmentVM, FragmentWizardFlatBinding> {
    public static final String ARG_AGUSER = "arg_aguser";
    public static final String ARG_WIZARD_FLAT_TYPE = "wizard_flat_type";


    public static WizardFlatFragment newInstance(AgUser agUser, int wizardFlatType) {
        WizardFlatFragment f = new WizardFlatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_AGUSER, agUser);
        args.putInt(ARG_WIZARD_FLAT_TYPE, wizardFlatType);
        f.setArguments(args);
        return f;
    }

    @Override
    protected WizardFlatFragmentVM onCreateViewModel(FragmentWizardFlatBinding binding) {
        return new WizardFlatFragmentVM(this, binding);
    }


    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_wizard_flat;
    }

    @Override
    public void doRequestAction() {
        getViewModel().wizardAction();
    }
}
