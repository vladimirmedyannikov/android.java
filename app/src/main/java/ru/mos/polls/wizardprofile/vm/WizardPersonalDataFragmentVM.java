package ru.mos.polls.wizardprofile.vm;

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
    public WizardPersonalDataFragmentVM(WizardPersonalDataFragment fragment, FragmentWizardPersonalDataBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWizardPersonalDataBinding binding) {

    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
//        personalInfoFragment = (EditPersonalInfoFragment) getFragment().getChildFragmentManager().findFragmentById(R.id.wizard_personal_data);
//        personalInfoFragment.getViewModel().personalType = EditPersonalInfoFragmentVM.PERSONAL_FIO;
//        personalInfoFragment.getViewModel().setView(EditPersonalInfoFragmentVM.PERSONAL_FIO);
        FragmentTransaction ft = getFragment().getChildFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        ft.replace(R.id.your_placeholder, EditPersonalInfoFragment.newInstance(new AgUser(getActivity()),EditPersonalInfoFragmentVM.PERSONAL_FIO));
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();
//        your_placeholder
    }
}
