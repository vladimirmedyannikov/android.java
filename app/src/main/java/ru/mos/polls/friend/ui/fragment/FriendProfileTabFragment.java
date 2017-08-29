package ru.mos.polls.friend.ui.fragment;


import android.os.Bundle;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentFriendTabBinding;
import ru.mos.polls.friend.vm.FriendProfileTabFragmentVM;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * Created by Trunks on 06.06.2017.
 */

public class FriendProfileTabFragment extends BindingFragment<FriendProfileTabFragmentVM, FragmentFriendTabBinding> {
    public final static String ARG_FRIEND = "arg_friend";

    public static FriendProfileTabFragment newInstance(Friend friend) {
        FriendProfileTabFragment f = new FriendProfileTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIEND, friend);
        f.setArguments(args);
        return f;
    }

    public FriendProfileTabFragment() {
    }

    @Override
    protected FriendProfileTabFragmentVM onCreateViewModel(FragmentFriendTabBinding binding) {
        return new FriendProfileTabFragmentVM(this, getBinding());
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_friend_tab;
    }
}
