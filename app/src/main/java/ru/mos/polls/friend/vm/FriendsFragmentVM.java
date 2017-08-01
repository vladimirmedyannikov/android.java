package ru.mos.polls.friend.vm;

import android.support.v7.widget.LinearLayoutManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutFriendsBinding;
import ru.mos.polls.friend.ui.FriendsAdapter;
import ru.mos.polls.friend.ui.FriendsFragment;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendMy;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 13:18.
 */

public class FriendsFragmentVM extends FragmentViewModel<FriendsFragment, LayoutFriendsBinding> {
    private FriendsAdapter adapter;

    public FriendsFragmentVM(FriendsFragment fragment, LayoutFriendsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutFriendsBinding binding) {
        getFragment().getActivity().setTitle(R.string.mainmenu_friends);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FriendsAdapter();
        adapter.add(new FriendAddItemVW());
        binding.list.setAdapter(adapter);
        loadMyFriends();
    }

    private void loadMyFriends() {
        HandlerApiResponseSubscriber<FriendMy.Response.Result> handler
                = new HandlerApiResponseSubscriber<FriendMy.Response.Result>(getFragment().getContext()) {

            @Override
            protected void onResult(FriendMy.Response.Result result) {
                /**
                 * todo
                 * для теста используем пока заглушку
                 */
                adapter.add(/*result.getFriends()*/FriendsAdapter.getStub(getFragment().getContext()));
                adapter.notifyDataSetChanged();
            }
        };

        AGApplication
                .api
                .friendMy(new FriendMy.Request())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }
}
