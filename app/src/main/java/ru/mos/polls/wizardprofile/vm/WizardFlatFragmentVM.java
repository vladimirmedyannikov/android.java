package ru.mos.polls.wizardprofile.vm;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardFamilyBinding;
import ru.mos.polls.databinding.FragmentWizardFlatBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.service.model.Personal;
import ru.mos.polls.newprofile.ui.adapter.MaritalStatusAdapter;
import ru.mos.polls.newprofile.ui.fragment.CustomFlatFragment;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.newprofile.vm.NewFlatFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.WizardFamilyFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardFlatFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardPersonalDataFragment;

/**
 * Created by Trunks on 31.07.2017.
 */

public class WizardFlatFragmentVM extends FragmentViewModel<WizardFlatFragment, FragmentWizardFlatBinding> implements WizardCustomFlatListener {
    NewFlatFragment newFlatFragment;
    CustomFlatFragment customFlatFragment;
    AgUser agUser;
    Personal personal;
    int wizardFlatType;
    Flat flat;
    TextInputEditText socialStatus;
    AppCompatTextView wizardFlatTitle;

    public WizardFlatFragmentVM(WizardFlatFragment fragment, FragmentWizardFlatBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWizardFlatBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            agUser = (AgUser) extras.get(WizardFlatFragment.ARG_AGUSER);
            wizardFlatType = extras.getInt(WizardFlatFragment.ARG_WIZARD_FLAT_TYPE);
        } else {
            agUser = new AgUser(getActivity());
        }
        personal = new Personal();
        socialStatus = binding.socialStatus;
        wizardFlatTitle = binding.wizardFlatTitle;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setView();

    }

    public void setView() {
        int flatType = 0;
        switch (wizardFlatType) {
            case NewFlatFragmentVM.FLAT_TYPE_REGISTRATION:
                flat = agUser.getRegistration();
                flatType = NewFlatFragmentVM.FLAT_TYPE_REGISTRATION;
                wizardFlatTitle.setText(getActivity().getString(R.string.flat_title_registration));
                break;
            case NewFlatFragmentVM.FLAT_TYPE_RESIDENCE:
                flat = agUser.getResidence();
                flatType = NewFlatFragmentVM.FLAT_TYPE_RESIDENCE;
                wizardFlatTitle.setText(getActivity().getString(R.string.flat_title_residence));
                break;
            case NewFlatFragmentVM.FLAT_TYPE_WORK:
                flat = agUser.getWork();
                flatType = NewFlatFragmentVM.FLAT_TYPE_WORK;
                wizardFlatTitle.setText(getActivity().getString(R.string.flat_title_work));
                socialStatus.setVisibility(View.VISIBLE);
                break;
        }
        newFlatFragment = NewFlatFragment.newInstanceForWizard(flat, flatType);
        FragmentTransaction ft = getFragment().getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container, newFlatFragment);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        newFlatFragment.getViewModel().setWizardCustomFlatListener(this);
    }


    public boolean checkField() {
//        if (selectedMartial == 0) {
//            Toast.makeText(getActivity(), "Укажите семейное положение", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    public void wizardAction() {
        if (checkField()) {
            newFlatFragment.getViewModel().confirmAction();
        }
    }

    @Override
    public void onCustomFlatListener(String street, String building) {
        customFlatFragment = CustomFlatFragment.newInstanceForWizard(flat, true, "asdas", "asdas");
        FragmentTransaction ft = getFragment().getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container, customFlatFragment);
        ft.commit();
    }
}
