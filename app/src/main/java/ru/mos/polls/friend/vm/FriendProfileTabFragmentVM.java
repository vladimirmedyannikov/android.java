package ru.mos.polls.friend.vm;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.rxjava.RxEventDisposableSubscriber;
import ru.mos.polls.databinding.FragmentFriendTabBinding;
import ru.mos.polls.friend.model.Friend;
import ru.mos.polls.friend.ui.fragment.FriendProfileTabFragment;
import ru.mos.polls.friend.ui.fragment.FriendStatisticFragment;
import ru.mos.polls.profile.ui.adapter.PagerAdapter;
import ru.mos.polls.profile.ui.fragment.AchievementTabFragment;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class FriendProfileTabFragmentVM extends UIComponentFragmentViewModel<FriendProfileTabFragment, FragmentFriendTabBinding> {
    private ViewPager pager;
    private TabLayout slidingTabs;
    private Friend friend;
    private PagerAdapter adapter;
    private View invisibleProfileContainer;
    private CircleImageView avatar;
    private boolean isInvisible = false;

    public FriendProfileTabFragmentVM(FriendProfileTabFragment fragment, FragmentFriendTabBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().with(new ProgressableUIComponent()).build();
    }

    private void goneSlidingTabs(boolean on) {
        if (!isInvisible) {
            Fade fade = new Fade();
            fade.setDuration(500);

            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(500);

            TransitionSet transitionSet = new TransitionSet();
            transitionSet.addTransition(fade);
            transitionSet.addTransition(changeBounds);
            transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);

            TransitionManager.beginDelayedTransition(getBinding().root, transitionSet);
            getBinding().slidingTabs.setVisibility(on ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void initialize(FragmentFriendTabBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            friend = (Friend) extras.getSerializable(FriendProfileTabFragment.ARG_FRIEND);
        }
        List<PagerAdapter.Page> pages = getPages();
        pager = binding.pager;
        adapter = new PagerAdapter(getFragment().getChildFragmentManager(), pages);
        pager.setAdapter(adapter);
        slidingTabs = binding.slidingTabs;
        invisibleProfileContainer = binding.friendInfoLayout;
        avatar = binding.friendAvatar.avatar;
//        goneSlidingTabs(true);
        slidingTabs.setupWithViewPager(pager);
        for (int index = 0; index < pages.size(); ++index) {
            slidingTabs
                    .getTabAt(index)
                    .setIcon(pages.get(index).getIconResId());
        }
        getFragment().getActivity().setTitle(R.string.profile_title);
        subscribeEventsBus();
    }

    private void subscribeEventsBus() {
        disposables.add(AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxEventDisposableSubscriber() {
                    @Override
                    public void onNext(Object o) {
                        if (o instanceof Events.FriendEvents) {
                            Events.FriendEvents action = (Events.FriendEvents) o;
                            switch (action.getId()) {
                                case Events.FriendEvents.FRIEND_INVISIBLE:
                                    isInvisible = true;
                                    slidingTabs.setVisibility(View.GONE);
                                    pager.setVisibility(View.GONE);
                                    avatar.setImageResource(R.drawable.ic_avatar_default);
                                    invisibleProfileContainer.setVisibility(View.VISIBLE);
                                    break;
                                case Events.FriendEvents.FRIEND_ACHIEVEMENT_DOWNLOAD_RESULT_ZERO:
                                    if (adapter != null) {
                                        adapter.deletePage(1);
                                    }
                                    goneSlidingTabs(true);
                                    break;
                                case Events.FriendEvents.FRIEND_ACHIEVEMENT_DOWNLOAD_RESULT_NOT_ZERO:
                                    goneSlidingTabs(false);
                                    break;
                            }
                        }
                    }
                }));
    }

    protected void selectTab(int tabNumber) {
        slidingTabs.setScrollPosition(tabNumber, 0f, true);
        pager.setCurrentItem(tabNumber);
    }

    protected List<PagerAdapter.Page> getPages() {
        List<PagerAdapter.Page> result = new ArrayList<>();
        if (friend != null) {
            result.add(new PagerAdapter.Page(R.drawable.ic_user, FriendStatisticFragment.newInstance(friend)));
            result.add(new PagerAdapter.Page(R.drawable.ic_trophy, AchievementTabFragment.newInstance(friend.getId())));
        }
        return result;
    }
}
