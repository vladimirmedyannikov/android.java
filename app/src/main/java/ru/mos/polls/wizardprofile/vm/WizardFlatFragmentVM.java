package ru.mos.polls.wizardprofile.vm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardFlatBinding;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.service.model.Personal;
import ru.mos.polls.newprofile.state.EditPersonalInfoState;
import ru.mos.polls.newprofile.ui.fragment.CustomFlatFragment;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.newprofile.vm.NewFlatFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.WizardFlatFragment;

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
    View container;
    Personal personal;
    boolean isCustomFlat;
    boolean isWorkFlatEmpty;
    int flatType;

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
        container = binding.container;
        socialStatus = binding.socialStatus;
        socialStatusWrapper = binding.socialStatusWrapper;
        wizardFlatTitle = binding.wizardFlatTitle;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setView();
        setRxEventBusListener();
        if (wizardFlatType == NewFlatFragmentVM.FLAT_TYPE_WORK) {
            setListener();
        }
    }

    public void setListener() {
        socialStatus.setOnClickListener(v -> {
            getFragment().navigateToActivityForResult(new EditPersonalInfoState(agUser, EditPersonalInfoFragmentVM.WIZARD_SOCIAL_STATUS), EditPersonalInfoFragmentVM.WIZARD_SOCIAL_STATUS);
        });
    }

    public void setRxEventBusListener() {
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.ProfileEvents) {
                        Events.ProfileEvents action = (Events.ProfileEvents) o;
                        switch (action.getEventType()) {
                            case Events.ProfileEvents.UPDATE_USER_INFO:
                                AgUser changed = action.getAgUser();
                                agUser = changed;
                                agUser.save(getActivity().getBaseContext());
                                setSocialStatusView();
                                break;
                        }
                    }
                    if (o instanceof Events.WizardEvents) {
                        Events.WizardEvents action = (Events.WizardEvents) o;
                        switch (action.getEventType()) {
                            case Events.WizardEvents.WIZARD_CHANGE_FLAT_FR:
                                rotateFragment();
                                break;
                        }
                    }
                });
    }

    private void rotateFragment() {
        if (isCustomFlat) {
            changeToNewFlatFragment();
        }
    }

    public void setView() {
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
                isWorkFlatEmpty = flat.isEmpty();
                container.setVisibility(flat.isEmpty() ? View.VISIBLE : View.GONE);
                wizardFlatTitle.setVisibility(flat.isEmpty() ? View.VISIBLE : View.GONE);
                setSocialStatusView();
                socialStatus.setVisibility(agUser.getAgSocialStatus() == 0 ? View.VISIBLE : View.GONE);
                socialStatusWrapper.setVisibility(agUser.getAgSocialStatus() == 0 ? View.VISIBLE : View.GONE);
                personal = new Personal();
                break;
        }
        newFlatFragment = NewFlatFragment.newInstanceForWizard(flat, flatType);
        changeToNewFlatFragment();
    }

    public void changeToNewFlatFragment() {
        changeFragment(newFlatFragment);
        isCustomFlat = false;
    }

    public void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragment().getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private void setSocialStatusView() {
        setSocialStatusView(agUser.getAgSocialStatus());
    }

    public void setSocialStatusView(int idSocialStatus) {
        List<AgSocialStatus> list = AgSocialStatus.fromPreferences(getActivity().getBaseContext());
        socialStatus.setText(idSocialStatus != 0 ? list.get(idSocialStatus).getTitle() : "");
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
        if (flatType == NewFlatFragmentVM.FLAT_TYPE_WORK && !isWorkFlatEmpty) {
            AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_SOCIAL_STATUS, AgUser.getPercentFillProfile(getActivity())));
            return;
        }
        if (checkField() && !isCustomFlat) {
            newFlatFragment.getViewModel().confirmAction();
        } else {
            if (checkField())
                customFlatFragment.getViewModel().confirmAction();
        }
    }

    @Override
    public void onCustomFlatListener(String street, String building) {
        customFlatFragment = CustomFlatFragment.newInstanceForWizard(flat, true, street, building, wizardFlatType);
        newFlatFragment.getViewModel().changeFlat();
        changeToCustomFlatFragment();
    }

    public void changeToCustomFlatFragment() {
        changeFragment(customFlatFragment);
        isCustomFlat = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
