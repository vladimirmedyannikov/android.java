package ru.mos.polls.wizardprofile.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardPersonalDataBinding;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.wizardprofile.vm.WizardPersonalDataFragmentVM;

/**
 * Created by Trunks on 31.07.2017.
 */

public class WizardPersonalDataFragment extends BindingFragment<WizardPersonalDataFragmentVM, FragmentWizardPersonalDataBinding> {
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
}
