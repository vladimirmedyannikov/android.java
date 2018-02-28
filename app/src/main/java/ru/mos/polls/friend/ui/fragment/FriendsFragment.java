package ru.mos.polls.friend.ui.fragment;

import android.support.v4.app.Fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.LayoutFriendsBinding;
import ru.mos.polls.friend.vm.FriendsFragmentVM;


public class FriendsFragment extends NavigateFragment<FriendsFragmentVM, LayoutFriendsBinding> {
    public static Fragment instance() {
        return new FriendsFragment();
    }

    @Override
    protected FriendsFragmentVM onCreateViewModel(LayoutFriendsBinding binding) {
        return new FriendsFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_friends;
    }
}
