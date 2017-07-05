package ru.mos.polls.newprofile.vm;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewEditPersonalInfoBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.vm.MenuFragmentVM;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoFragmentVM extends MenuFragmentVM<EditPersonalInfoFragment, LayoutNewEditPersonalInfoBinding> {
    public static final int PERSONAL_EMAIL = 33344;
    public static final int PERSONAL_FIO = 44333;
    public int personalType;
    AgUser agUser;
    TextInputEditText email;

    public EditPersonalInfoFragmentVM(EditPersonalInfoFragment fragment, LayoutNewEditPersonalInfoBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutNewEditPersonalInfoBinding binding) {
        Bundle extras = getFragment().getArguments();
        personalType = extras.getInt(EditPersonalInfoFragment.ARG_PERSONAL_INFO);
        agUser = (AgUser) extras.get(EditPersonalInfoFragment.ARG_AGUSER);
        email = binding.email;
    }

    @Override
    public void onViewCreated() {
        email.setText(agUser.getEmail());
    }

    @Override
    public void confirmAction(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_confirm:
                agUser.setEmail(email.getText().toString());
                AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.UPDATE_USER_INFO, agUser));
                getActivity().finish();
                break;
        }
    }
}
