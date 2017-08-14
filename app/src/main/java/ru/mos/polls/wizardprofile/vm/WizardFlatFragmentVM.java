package ru.mos.polls.wizardprofile.vm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardFamilyBinding;
import ru.mos.polls.databinding.FragmentWizardFlatBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.service.model.Personal;
import ru.mos.polls.newprofile.state.EditPersonalInfoState;
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
    int wizardFlatType;
    Flat flat;
    TextInputEditText socialStatus;
    AppCompatTextView wizardFlatTitle;
    TextInputLayout socialStatusWrapper;
    Personal personal;

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
        socialStatusWrapper = binding.socialStatusWrapper;
        wizardFlatTitle = binding.wizardFlatTitle;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setView();
        if (wizardFlatType == NewFlatFragmentVM.FLAT_TYPE_WORK) {
            setRxEventBusListener();
            setListener();
        }
    }

    public void setListener() {
        socialStatus.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(agUser, EditPersonalInfoFragmentVM.SOCIAL_STATUS), EditPersonalInfoFragmentVM.SOCIAL_STATUS);
        });
    }

    public void setRxEventBusListener() {
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.ProfileEvents) {
                        Events.ProfileEvents action = (Events.ProfileEvents) o;
                        switch (action.getAction()) {
                            case Events.ProfileEvents.UPDATE_USER_INFO:
                                AgUser changed = action.getAgUser();
                                agUser = changed;
                                agUser.save(getActivity().getBaseContext());
//                                setView();
                                setSocialStatusView();
                                break;
                        }
                    }
                });
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
                socialStatusWrapper.setVisibility(View.VISIBLE);
                personal = new Personal();
                setSocialStatusView();
                break;
        }
        newFlatFragment = NewFlatFragment.newInstanceForWizard(flat, flatType);
        FragmentTransaction ft = getFragment().getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container, newFlatFragment);
        ft.commit();
    }

    private void setSocialStatusView() {
        if (agUser.getAgSocialStatus() != 0) {
            setSocialStatusView(agUser.getAgSocialStatus());
        }
    }

    public void setSocialStatusView(int idSocialStatus) {
        List<AgSocialStatus> list = AgSocialStatus.fromPreferences(getActivity().getBaseContext());
        socialStatus.setText(list.get(idSocialStatus).getTitle());
    }

    @Override
    public void onResume() {
        super.onResume();
        newFlatFragment.getViewModel().setWizardCustomFlatListener(this);
    }


    public boolean checkField() {
        if (wizardFlatType == NewFlatFragmentVM.FLAT_TYPE_WORK && socialStatus.getText().length() < 0) {
            Toast.makeText(getActivity(), "Укажите род деятельности", Toast.LENGTH_SHORT).show();
            return false;
        } else {

        }
        return true;
    }

    public void wizardAction() {
        if (checkField()) {
            newFlatFragment.getViewModel().confirmAction();
        }
    }

    @Override
    public void onCustomFlatListener(String street, String building) {
        customFlatFragment = CustomFlatFragment.newInstanceForWizard(flat, true, street, building, wizardFlatType);
        FragmentTransaction ft = getFragment().getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container, customFlatFragment);
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
