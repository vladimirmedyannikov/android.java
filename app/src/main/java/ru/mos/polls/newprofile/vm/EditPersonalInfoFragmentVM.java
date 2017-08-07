package ru.mos.polls.newprofile.vm;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentNewEditPersonalInfoBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.vm.MenuFragmentVM;
import ru.mos.polls.newprofile.model.BirthdayKids;
import ru.mos.polls.newprofile.service.ProfileSet;
import ru.mos.polls.newprofile.service.model.Personal;
import ru.mos.polls.newprofile.ui.adapter.BirthdayKidsAdapter;
import ru.mos.polls.newprofile.ui.adapter.SocialStatusAdapter;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.AgTextUtil;
import ru.mos.polls.util.FileUtils;
import ru.mos.polls.util.InputFilterMinMax;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoFragmentVM extends MenuFragmentVM<EditPersonalInfoFragment, FragmentNewEditPersonalInfoBinding> implements OnSocialStatusItemClick {
    public static final int PERSONAL_EMAIL = 33344;
    public static final int PERSONAL_FIO = 44333;
    public static final int COUNT_KIDS = 43678;
    public static final int BIRTHDAY_KIDS = 13453;
    public static final int SOCIAL_STATUS = 12333;
    public int personalType;
    AgUser agUser;
    TextInputEditText email, lastname, firstname, middlename, childsCount;
    View emailContainer, fioContainer, childsCountContainer;
    RecyclerView recyclerView;
    List<BirthdayKids> birthdayKidsList;
    boolean isDataChanged;
    boolean forWizard;
    Personal personal;

    public EditPersonalInfoFragmentVM(EditPersonalInfoFragment fragment, FragmentNewEditPersonalInfoBinding binding) {
        super(fragment, binding);
    }


    @Override
    protected void initialize(FragmentNewEditPersonalInfoBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            personalType = extras.getInt(EditPersonalInfoFragment.ARG_PERSONAL_INFO);
            agUser = (AgUser) extras.get(EditPersonalInfoFragment.ARG_AGUSER);
            forWizard = extras.getBoolean(WizardProfileFragment.ARG_FOR_WIZARD);
        } else {
            agUser = new AgUser(getActivity());
        }
        emailContainer = binding.emailContainer.emailContainer;
        fioContainer = binding.fioContainer.fioContainer;
        childsCountContainer = binding.childsCountContainer.childsCountContainer;
        email = binding.emailContainer.email;
        lastname = binding.fioContainer.lastname;
        firstname = binding.fioContainer.firstname;
        middlename = binding.fioContainer.middlename;
        childsCount = binding.childsCountContainer.childsCount;
        recyclerView = binding.root;
    }

    public int getPersonalType() {
        return personalType;
    }

    @Override
    public void onResume() {
        super.onResume();
        setView(personalType);
    }

    public void setView(int personalType) {
        switch (personalType) {
            case PERSONAL_EMAIL:
                emailContainer.setVisibility(View.VISIBLE);
                email.setText(agUser.getEmail());
                break;
            case PERSONAL_FIO:
                fioContainer.setVisibility(View.VISIBLE);
                lastname.setText(agUser.getSurname());
                firstname.setText(agUser.getFirstName());
                middlename.setText(agUser.getMiddleName());
                break;
            case COUNT_KIDS:
                childsCountContainer.setVisibility(View.VISIBLE);
                childsCount.setText(String.valueOf(agUser.getChildCount()));
                childsCount.setFilters(new InputFilter[]{new InputFilterMinMax(0, 15)});
                break;
            case SOCIAL_STATUS:
                SocialStatusAdapter adapter = new SocialStatusAdapter(AgSocialStatus.fromPreferences(getFragment().getContext()), this);
                setRecyclerViewAdapter(adapter);
                break;
            case BIRTHDAY_KIDS:
                setKidsBirthdayDateView();
                break;
        }
    }

    public void setRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        setRecyclerList(recyclerView);
        recyclerView.setAdapter(adapter);
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public void setKidsBirthdayDateView() {
        List<Long> kidsYearList = agUser.getChildBirthdays();
        birthdayKidsList = new ArrayList<>();
        String[] hints = getFragment().getResources().getStringArray(R.array.child_birthdays_hints);
        String[] title = getFragment().getResources().getStringArray(R.array.child_birthdays_hint);

        int kidsCount = agUser.getChildCount();
        int childSize = kidsYearList.size();
        if (kidsCount >= childSize) {
            childSize = kidsCount - childSize;
            for (int i = 0; i < childSize; ++i) {
                kidsYearList.add(Long.valueOf(0));
            }
        } else {
            for (int i = childSize - 1; i > kidsCount - 1; --i) {
                kidsYearList.remove(i);
            }
        }
        for (int i = 0; i < kidsYearList.size(); i++) {
            String hint = String.format(getFragment().getString(R.string.child_birthdays_hints_format), hints[i]);
            birthdayKidsList.add(new BirthdayKids(kidsYearList.get(i), hint, title[i]));
        }
        BirthdayKidsAdapter adapter = new BirthdayKidsAdapter(birthdayKidsList, getFragment().getChildFragmentManager());
        setRecyclerViewAdapter(adapter);
    }


    @Override
    public void onOptionsItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_confirm:
                confirmAction();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu() {
        super.onCreateOptionsMenu();
        if (forWizard || personalType == SOCIAL_STATUS) {
            hideAllMenuIcon();
        }
    }

    public void hideAllMenuIcon() {
        getFragment().hideMenuItem(R.id.action_confirm);
    }

    public void confirmAction() { //каждые данные сохранятьются отдельно
        boolean validationOk = false;
        if (personal == null) {
            personal = new Personal();
        }
        switch (personalType) {
            case PERSONAL_EMAIL:
                validationOk = checkEmailValid();
                personal.setEmail(agUser.getEmail());
                break;
            case PERSONAL_FIO:
                validationOk = checkFIOValid();
                personal.setSurname(agUser.getSurname());
                personal.setFirstname(agUser.getFirstName());
                personal.setMiddlename(agUser.getMiddleName());
                break;
            case COUNT_KIDS:
                agUser.setChildCount(Integer.valueOf(childsCount.getText().toString()));
                validationOk = true;
                personal.setChildrens_count(agUser.getChildCount());
                break;
            case BIRTHDAY_KIDS:
                agUser.setChildBirthdays(getBirthdayKidsLong());
                validationOk = true;
                personal.setChildrens_birthdays(agUser.childBirthdaysAsList());
                break;
            case SOCIAL_STATUS:
                personal.setSocial_status(String.valueOf(agUser.getAgSocialStatus()));
                validationOk = true;
                break;
        }
        if (validationOk) saveUser(personal);
    }

    public boolean checkFIOValid() {
        if (AgTextUtil.validateRus(lastname.getText().toString())
                && AgTextUtil.validateRus(firstname.getText().toString())
                && AgTextUtil.validateRus(middlename.getText().toString())) {
            agUser.setSurname(lastname.getText().toString());
            agUser.setFirstName(firstname.getText().toString());
            agUser.setMiddleName(middlename.getText().toString());
            return true;
        } else {
            Toast.makeText(getActivity(), "ФИО заполнены не на кириллице", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void saveUser(Personal personal) {
        sendData(new ProfileSet.Request(personal));
    }

    public void sendData(ProfileSet.Request request) {
        /**
         * отправляем личные данные профиля
         */
        HandlerApiResponseSubscriber<ProfileSet.Response.Result> handler
                = new HandlerApiResponseSubscriber<ProfileSet.Response.Result>(getActivity(), progressable) {
            @Override
            protected void onResult(ProfileSet.Response.Result result) {
                if (!forWizard) {
                    AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.UPDATE_USER_INFO, agUser));
                    EditProfileFragmentVM.sendBroadcastReLoadBadges(getActivity());
                    getActivity().finish();
                } else {
                    int wizardType = 0;
                    switch (personalType) {
                        case PERSONAL_EMAIL:
                            wizardType = Events.WizardEvents.WIZARD_EMAIL;
                            break;
                        case PERSONAL_FIO:
                            wizardType = Events.WizardEvents.WIZARD_PERSONAL;
                            break;
                        case COUNT_KIDS:
                            wizardType = Events.WizardEvents.WIZARD_FAMALY;
                            break;
                        case BIRTHDAY_KIDS:
                            wizardType = Events.WizardEvents.WIZARD_KIDS;
                            break;
                    }
                    agUser.save(getActivity());
                    int percent = 0;
                    if (result != null) percent = result.getPercentFillProfile();
                    AGApplication.bus().send(new Events.WizardEvents(wizardType, percent));
                }
            }
        };
        Observable<ProfileSet.Response> responseObservabl =
                AGApplication.api.setProfile(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservabl.subscribeWith(handler));
    }

    public boolean checkEmailValid() {
        if (AgTextUtil.isEmailValid(email.getText().toString())) {
            agUser.setEmail(email.getText().toString());
            return true;
        } else {
            Toast.makeText(getActivity(), "E-mail не соотвествует формату", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onSocialStatusClick(AgSocialStatus agSocialStatus) {
        agUser.setAgSocialStatus(agSocialStatus.getId());
        confirmAction();
    }

    public boolean isDataChanged() {
        switch (personalType) {
            case PERSONAL_EMAIL:
                isDataChanged = !agUser.getEmail().equalsIgnoreCase(email.getText().toString());
                break;
            case PERSONAL_FIO:
                isDataChanged = (!agUser.getSurname().equalsIgnoreCase(lastname.getText().toString())
                        || !agUser.getFirstName().equalsIgnoreCase(firstname.getText().toString())
                        || !agUser.getMiddleName().equalsIgnoreCase(middlename.getText().toString()));
                break;
            case COUNT_KIDS:
                int value = TextUtils.isEmpty(childsCount.getText()) ? 0 : Integer.valueOf(childsCount.getText().toString());
                isDataChanged = !(agUser.getChildCount() == value);
                break;
            case BIRTHDAY_KIDS:
                isDataChanged = !FileUtils.equalList(getBirthdayKidsLong(), agUser.getChildBirthdays());
                break;
        }
        return isDataChanged;
    }

    public List<Long> getBirthdayKidsLong() {
        List<Long> list = new ArrayList<>();
        for (BirthdayKids birthdayKids : birthdayKidsList) {
            list.add(birthdayKids.getBirthdayYear());
        }
        return list;
    }
}
