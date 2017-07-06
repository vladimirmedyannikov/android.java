package ru.mos.polls.friend.vm;

import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutFriendsBinding;
import ru.mos.polls.friend.ui.FriendsFragment;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 13:18.
 */

public class FriendsFragmentVM extends FragmentViewModel<FriendsFragment, LayoutFriendsBinding> {

    public FriendsFragmentVM(FriendsFragment fragment, LayoutFriendsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutFriendsBinding binding) {
        getFragment().getActivity().setTitle(R.string.mainmenu_friends);
    }
}
