package ru.mos.polls.friend.ui.fragment;

import android.support.v4.app.Fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutFriendsBinding;
import ru.mos.polls.friend.vm.FriendsFragmentVM;
import ru.mos.polls.newprofile.base.ui.BindingFragment;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 13:15.
 */

public class FriendsFragment extends BindingFragment<FriendsFragmentVM, LayoutFriendsBinding> {
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
