package ru.mos.polls.wizardprofile.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardProfileBinding;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.wizardprofile.vm.WizardProfileFragmentVM;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardProfileFragment extends BindingFragment<WizardProfileFragmentVM, FragmentWizardProfileBinding> {
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
