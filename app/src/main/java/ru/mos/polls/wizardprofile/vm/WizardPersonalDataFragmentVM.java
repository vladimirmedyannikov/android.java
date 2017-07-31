package ru.mos.polls.wizardprofile.vm;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardPersonalDataBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.WizardPersonalDataFragment;

/**
 * Created by Trunks on 31.07.2017.
 */

public class WizardPersonalDataFragmentVM extends FragmentViewModel<WizardPersonalDataFragment, FragmentWizardPersonalDataBinding> {
    EditPersonalInfoFragment personalInfoFragment;
    AgUser agUser;

    public WizardPersonalDataFragmentVM(WizardPersonalDataFragment fragment, FragmentWizardPersonalDataBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWizardPersonalDataBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            agUser = (AgUser) extras.get(WizardPersonalDataFragment.ARG_AGUSER);
        } else {
            agUser = new AgUser(getActivity());
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        FragmentTransaction ft = getFragment().getChildFragmentManager().beginTransaction();
        personalInfoFragment = EditPersonalInfoFragment.newInstance(new AgUser(getActivity()), EditPersonalInfoFragmentVM.PERSONAL_FIO);
        ft.replace(R.id.container, personalInfoFragment);
        ft.commit();
    }
}
