package ru.mos.polls.newprofile.vm;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewEditPersonalInfoBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.vm.MenuFragmentVM;
import ru.mos.polls.newprofile.model.BirthdayKids;
import ru.mos.polls.newprofile.ui.adapter.BirthdayKidsAdapter;
import ru.mos.polls.newprofile.ui.adapter.SocialBindAdapter;
import ru.mos.polls.newprofile.ui.adapter.SocialStatusAdapter;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.social.model.Social;
import ru.mos.polls.util.AgTextUtil;
import ru.mos.polls.util.FileUtils;
import ru.mos.polls.util.InputFilterMinMax;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoFragmentVM extends MenuFragmentVM<EditPersonalInfoFragment, LayoutNewEditPersonalInfoBinding> implements OnSocialStatusItemClick {
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

    public EditPersonalInfoFragmentVM(EditPersonalInfoFragment fragment, LayoutNewEditPersonalInfoBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutNewEditPersonalInfoBinding binding) {
        Bundle extras = getFragment().getArguments();
        personalType = extras.getInt(EditPersonalInfoFragment.ARG_PERSONAL_INFO);
        agUser = (AgUser) extras.get(EditPersonalInfoFragment.ARG_AGUSER);
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

    @Override
    public void onViewCreated() {
        setView(personalType);
    }

    public int getPersonalType() {
        return personalType;
    }

    @Override
    public void onResume() {
        super.onResume();
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
                childsCount.setFilters(new InputFilter[]{new InputFilterMinMax("0", "15")});
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
                confirmaAction(personalType);
                break;
        }
    }

    public void confirmaAction(int personalType) {
        boolean validationOk = false;
        switch (personalType) {
            case PERSONAL_EMAIL:
                validationOk = checkEmailValid();
                break;
            case PERSONAL_FIO:
                checkFIOValid();
                break;
            case COUNT_KIDS:
                agUser.setChildCount(Integer.valueOf(childsCount.getText().toString()));
                validationOk = true;
                break;
            case BIRTHDAY_KIDS:
                agUser.setChildBirthdays(getBirthdayKidsLong());
                validationOk = true;
                break;
        }
        if (validationOk) saveUser();
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

    public void saveUser() {
        AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.UPDATE_USER_INFO, agUser));
        getActivity().finish();
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
        confirmaAction(SOCIAL_STATUS);
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
                isDataChanged = !(agUser.getChildCount() == Integer.valueOf(childsCount.getText().toString()));
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
