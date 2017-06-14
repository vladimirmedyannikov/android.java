package ru.mos.polls.newprofile.ui.vm;

import android.widget.Toast;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.databinding.LayoutUserTabProfileBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.ui.fragment.UserTabClickListener;
import ru.mos.polls.newprofile.ui.fragment.UserTabFragment;

/**
 * Created by Trunks on 08.06.2017.
 */

public class UserTabFragmentVM extends FragmentViewModel<UserTabFragment, LayoutUserTabProfileBinding> implements UserTabClickListener {

    private AgUser changed, saved;

    public UserTabFragmentVM(UserTabFragment fragment, LayoutUserTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutUserTabProfileBinding binding) {
        saved = new AgUser(getActivity());
        binding.setAgUser(saved);
        binding.setClickListener(this);
    }

    @Override
    public void makePhoto() {
        Toast.makeText(getFragment().getContext(), "make photo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void editUserInfo() {
        AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.EDIT_USER_INFO));
    }
}
