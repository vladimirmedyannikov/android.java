package ru.mos.polls.friend.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutFriendProfileBinding;
import ru.mos.polls.friend.vm.FriendStatisticFragmentVM;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 14.08.17 21:27.
 */

public class FriendStatisticFragment extends BindingFragment<FriendStatisticFragmentVM, LayoutFriendProfileBinding> {
    public static final String ARG_FRIEND = "arg_friend";

    public static Fragment newInstance(Friend friend) {
        Fragment result = new FriendStatisticFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIEND, friend);
        result.setArguments(args);
        return result;
    }

    @Override
    protected FriendStatisticFragmentVM onCreateViewModel(LayoutFriendProfileBinding binding) {
        return new FriendStatisticFragmentVM(this, binding);
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
