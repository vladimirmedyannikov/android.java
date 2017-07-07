package ru.mos.polls.newprofile.vm;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewEditPersonalInfoBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.vm.MenuFragmentVM;
import ru.mos.polls.newprofile.ui.adapter.SocialStatusAdapter;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.social.model.Social;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoFragmentVM extends MenuFragmentVM<EditPersonalInfoFragment, LayoutNewEditPersonalInfoBinding> implements OnSocialStatusItemClick {
    public static final int PERSONAL_EMAIL = 33344;
    public static final int PERSONAL_FIO = 44333;
    public static final int PERSONAL_CHILDS = 43678;
    public static final int SOCIAL_STATUS = 12333;
    public int personalType;
    AgUser agUser;
    TextInputEditText email, lastname, firstname, middlename, childsCount;
    View emailContainer, fioContainer, childsCountContainer;
    RecyclerView recyclerView;

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
            case PERSONAL_CHILDS:
                childsCountContainer.setVisibility(View.VISIBLE);
                childsCount.setText(String.valueOf(agUser.getChildCount()));
                break;
            case SOCIAL_STATUS:
                SocialStatusAdapter adapter = new SocialStatusAdapter(AgSocialStatus.fromPreferences(getFragment().getContext()), this);
                setRecyclerList(recyclerView);
                recyclerView.setAdapter(adapter);
                break;
        }
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
            case PERSONAL_CHILDS:
                agUser.setChildCount(Integer.valueOf(childsCount.getText().toString()));
        }
        AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.UPDATE_USER_INFO, agUser));
        getActivity().finish();
    }

    @Override
    public void onSocialStatusClick(AgSocialStatus agSocialStatus) {
        agUser.setAgSocialStatus(agSocialStatus.getId());
        Toast.makeText(getFragment().getContext(), agSocialStatus.getTitle(), Toast.LENGTH_SHORT).show();
        confirmaAction(SOCIAL_STATUS);
    }
}
