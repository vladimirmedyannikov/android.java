package ru.mos.polls.friend.vm;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutFriendsBinding;
import ru.mos.polls.friend.ui.FriendsAdapter;
import ru.mos.polls.friend.ui.FriendsFragment;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 13:18.
 */

public class FriendsFragmentVM extends FragmentViewModel<FriendsFragment, LayoutFriendsBinding> {
    private List<Friend> friends = new ArrayList<>();

    public FriendsFragmentVM(FriendsFragment fragment, LayoutFriendsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutFriendsBinding binding) {
        getFragment().getActivity().setTitle(R.string.mainmenu_friends);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.Adapter adapter = new FriendsAdapter(getActivity());
        binding.list.setAdapter(adapter);
    }
}
