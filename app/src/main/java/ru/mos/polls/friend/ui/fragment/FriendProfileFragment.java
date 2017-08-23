package ru.mos.polls.friend.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutFriendProfileBinding;
import ru.mos.polls.friend.vm.FriendProfileFragmentVM;
import ru.mos.polls.base.ui.BindingFragment;

/**
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 14.08.17 21:27.
 */

public class FriendProfileFragment extends BindingFragment<FriendProfileFragmentVM, LayoutFriendProfileBinding> {
    public static final String ARG_ID = "arg_id";

    public static Fragment instance(int id) {
        Fragment result = new FriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        result.setArguments(args);
        return result;
    }

    @Override
    protected FriendProfileFragmentVM onCreateViewModel(LayoutFriendProfileBinding binding) {
        return new FriendProfileFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_friend_profile;
    }
}
