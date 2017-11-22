package ru.mos.polls.wizardprofile.vm;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.rxjava.RxEventDisposableSubscriber;
import ru.mos.polls.databinding.FragmentWizardFamilyBinding;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.profile.service.model.Personal;
import ru.mos.polls.profile.ui.adapter.MaritalStatusAdapter;
import ru.mos.polls.profile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.profile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.WizardFamilyFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardPersonalDataFragment;

/**
 * Created by Trunks on 31.07.2017.
 */

public class WizardFamilyFragmentVM extends FragmentViewModel<WizardFamilyFragment, FragmentWizardFamilyBinding> {
    EditPersonalInfoFragment personalInfoFragment;
    AgUser agUser;
    AppCompatSpinner editMartialStatus;
    ArrayAdapter martialStatusAdapter;
    int selectedMartial;
    Personal personal;

    public WizardFamilyFragmentVM(WizardFamilyFragment fragment, FragmentWizardFamilyBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWizardFamilyBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            agUser = (AgUser) extras.get(WizardPersonalDataFragment.ARG_AGUSER);
        } else {
            agUser = new AgUser(getActivity());
        }
        editMartialStatus = binding.editMartialStatus;
        personal = new Personal();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setRxEventsBusListener();
        FragmentTransaction ft = getFragment().getChildFragmentManager().beginTransaction();
        personalInfoFragment = EditPersonalInfoFragment.newInstanceForWizard(agUser, EditPersonalInfoFragmentVM.COUNT_KIDS);
        ft.replace(R.id.container, personalInfoFragment);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        setMartialStatusView(agUser.getGender());
    }

    /**
     * слушатель для проставления семейного положения взависимости от пола
     */

    public void setRxEventsBusListener() {
        disposables.add(AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxEventDisposableSubscriber() {
                    @Override
                    public void onNext(Object o) {
                        if (o instanceof Events.WizardEvents) {
                            Events.WizardEvents events = (Events.WizardEvents) o;
                            switch (events.getEventType()) {
                                case Events.WizardEvents.WIZARD_UPDATE_GENDER:
                                    agUser = new AgUser(getActivity());
                                    setMartialStatusView(agUser.getGender());
                                    break;
                            }
                        }
                    }
                }));
    }

    public void setMartialStatusView(AgUser.Gender gender) {
        martialStatusAdapter = (MaritalStatusAdapter) getMartialStatusAdapter(gender);
        editMartialStatus.setAdapter(martialStatusAdapter);
        selectedMartial = martialStatusAdapter.getPosition(agUser.getMaritalStatus());
        editMartialStatus.setSelection(selectedMartial, false);
        editMartialStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != selectedMartial) {
                    AgUser.MaritalStatus maritalStatus = (AgUser.MaritalStatus) martialStatusAdapter.getItem(position);
                    selectedMartial = position;
                    personal.setMarital_status(maritalStatus == AgUser.MaritalStatus.NULL ? "" : maritalStatus.getValue());
                    agUser.setMaritalStatus(maritalStatus);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public ArrayAdapter getMartialStatusAdapter(AgUser.Gender gender) {
        List<AgUser.MaritalStatus> list = new ArrayList<>(Arrays.asList(AgUser.MaritalStatus.getMaritalStatusItems()));
        MaritalStatusAdapter newAd = new MaritalStatusAdapter(getActivity(), R.layout.layout_spinner_item, list, gender);
        newAd.setDropDownViewResource(R.layout.layout_spinner_item);
        return newAd;
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
            personalInfoFragment.getViewModel().setPersonal(personal);
            personalInfoFragment.getViewModel().setAgUser(agUser);
            personalInfoFragment.getViewModel().confirmAction();
        }
    }
}
