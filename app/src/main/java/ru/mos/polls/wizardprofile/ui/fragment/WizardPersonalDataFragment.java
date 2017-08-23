package ru.mos.polls.wizardprofile.ui.fragment;

import android.os.Bundle;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardPersonalDataBinding;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.wizardprofile.vm.WizardPersonalDataFragmentVM;

/**
 * Created by Trunks on 31.07.2017.
 */

public class WizardPersonalDataFragment extends NavigateFragment<WizardPersonalDataFragmentVM, FragmentWizardPersonalDataBinding> {
    public static final String ARG_AGUSER = "arg_aguser";

    public static WizardPersonalDataFragment newInstance(AgUser agUser) {
        WizardPersonalDataFragment f = new WizardPersonalDataFragment();
        Bundle args = new Bundle(1);
        args.putSerializable(ARG_AGUSER, agUser);
        f.setArguments(args);
        return f;
    }

    @Override
    protected WizardPersonalDataFragmentVM onCreateViewModel(FragmentWizardPersonalDataBinding binding) {
        return new WizardPersonalDataFragmentVM(this, binding);
    }


    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_wizard_personal_data;
    }

    @Override
    public void doRequestAction() {
        getViewModel().wizardAction();
    }
}
