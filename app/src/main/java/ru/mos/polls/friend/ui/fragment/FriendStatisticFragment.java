package ru.mos.polls.friend.ui.fragment;

import android.os.Bundle;

import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.LayoutFriendProfileBinding;
import ru.mos.polls.friend.model.Friend;
import ru.mos.polls.friend.vm.FriendStatisticFragmentVM;

/**
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 14.08.17 21:27.
 */

public class FriendStatisticFragment extends BindingFragment<FriendStatisticFragmentVM, LayoutFriendProfileBinding> {
    public static final String ARG_FRIEND = "arg_friend";

    public static JugglerFragment newInstance(Friend friend) {
        JugglerFragment result = new FriendStatisticFragment();
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
