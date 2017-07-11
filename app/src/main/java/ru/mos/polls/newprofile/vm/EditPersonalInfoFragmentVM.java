package ru.mos.polls.newprofile.vm;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.Social;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoFragmentVM extends MenuFragmentVM<EditPersonalInfoFragment, LayoutNewEditPersonalInfoBinding> implements OnSocialStatusItemClick {
    public static final int PERSONAL_EMAIL = 33344;
    public static final int PERSONAL_FIO = 44333;
    public static final int COUNT_KIDS = 43678;
    public static final int BIRTHDAY_KIDS = 13453;
    public static final int SOCIAL_STATUS = 12333;
    public static final int SOCIAL_BINDINGS = 14035;
    public int personalType;
    AgUser agUser;
    TextInputEditText email, lastname, firstname, middlename, childsCount;
    View emailContainer, fioContainer, childsCountContainer;
    RecyclerView recyclerView;
    List<BirthdayKids> birthdayKidsList;


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
                break;
            case SOCIAL_STATUS:
                SocialStatusAdapter adapter = new SocialStatusAdapter(AgSocialStatus.fromPreferences(getFragment().getContext()), this);
                setRecyclerViewAdapter(adapter);
                break;
            case BIRTHDAY_KIDS:
                setKidsBirthdayDateView();
                break;
            case SOCIAL_BINDINGS:
                setSocialBindView();
                break;
        }
    }

    public void setSocialBindView() {
        List<Social> list = Social.getSavedSocials(getActivity().getBaseContext());
        SocialBindAdapter adapter = new SocialBindAdapter(list);
        setRecyclerViewAdapter(adapter);
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
        switch (personalType) {
            case PERSONAL_EMAIL:
                agUser.setEmail(email.getText().toString());
                break;
            case PERSONAL_FIO:
                agUser.setSurname(lastname.getText().toString());
                agUser.setFirstName(firstname.getText().toString());
                agUser.setMiddleName(middlename.getText().toString());
                break;
            case COUNT_KIDS:
                agUser.setChildCount(Integer.valueOf(childsCount.getText().toString()));
                break;
            case BIRTHDAY_KIDS:
                List<Long> list = new ArrayList<>();
                for (BirthdayKids birthdayKids : birthdayKidsList) {
                    list.add(birthdayKids.getBirthdayYear());
                }
                agUser.setChildBirthdays(list);
                break;
        }
        AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.UPDATE_USER_INFO, agUser));
        getActivity().finish();
    }

    @Override
    public void onSocialStatusClick(AgSocialStatus agSocialStatus) {
        agUser.setAgSocialStatus(agSocialStatus.getId());
        confirmaAction(SOCIAL_STATUS);
    }
}
