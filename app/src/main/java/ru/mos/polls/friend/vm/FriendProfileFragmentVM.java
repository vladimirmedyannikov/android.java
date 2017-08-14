package ru.mos.polls.friend.vm;

import android.support.v7.widget.LinearLayoutManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.LayoutFriendProfileBinding;
import ru.mos.polls.friend.ui.adapter.FriendProfileAdapter;
import ru.mos.polls.friend.ui.fragment.FriendProfileFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendProfile;

/**
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 14.08.17 21:30.
 */

public class FriendProfileFragmentVM extends UIComponentFragmentViewModel<FriendProfileFragment, LayoutFriendProfileBinding> {
    private FriendProfileAdapter adapter;

    public FriendProfileFragmentVM(FriendProfileFragment fragment, LayoutFriendProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutFriendProfileBinding binding) {
        getFragment().getActivity().setTitle(R.string.profile_title);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FriendProfileAdapter();
        binding.list.setAdapter(adapter);
        loadFriendProfile();
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new PullableUIComponent(() -> {
                    progressable = getPullableProgressable();
                }))
                .with(new ProgressableUIComponent())
                .build();
    }

    private void loadFriendProfile() {
        HandlerApiResponseSubscriber<FriendProfile.Response.Result> handler
                = new HandlerApiResponseSubscriber<FriendProfile.Response.Result>(getFragment().getContext(), progressable) {

            @Override
            protected void onResult(FriendProfile.Response.Result result) {

                adapter.notifyDataSetChanged();
            }
        };
        int id = getFragment().getArguments().getInt(FriendProfileFragment.ARG_ID);
        AGApplication
                .api
                .friendProfile(new FriendProfile.Request(id))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }
}