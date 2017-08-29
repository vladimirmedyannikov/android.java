package ru.mos.polls.friend.vm;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.databinding.FragmentFriendTabBinding;
import ru.mos.polls.friend.ui.fragment.FriendStatisticFragment;
import ru.mos.polls.friend.ui.fragment.FriendProfileTabFragment;
import ru.mos.polls.friend.ui.utils.FriendGuiUtils;
import ru.mos.polls.newprofile.ui.adapter.PagerAdapter;
import ru.mos.polls.newprofile.ui.fragment.AchievementTabFragment;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class FriendProfileTabFragmentVM extends FragmentViewModel<FriendProfileTabFragment, FragmentFriendTabBinding> {
    private ViewPager pager;
    private TabLayout slidingTabs;
    private Friend friend;

    public FriendProfileTabFragmentVM(FriendProfileTabFragment fragment, FragmentFriendTabBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentFriendTabBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            friend = (Friend) extras.getSerializable(FriendProfileTabFragment.ARG_FRIEND);
        }
        List<PagerAdapter.Page> pages = getPages();
        pager = binding.pager;
        PagerAdapter adapter = new PagerAdapter(getFragment().getChildFragmentManager(), pages);
        pager.setAdapter(adapter);

        slidingTabs = binding.slidingTabs;
        slidingTabs.setupWithViewPager(pager);
        for (int index = 0; index < pages.size(); ++index) {
            slidingTabs
                    .getTabAt(index)
                    .setIcon(pages.get(index).getIconResId());
        }
        slidingTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getFragment().getActivity().setTitle(R.string.profile_title);
                        break;
                    case 1:
                        getFragment().getActivity().setTitle(FriendGuiUtils.getTitle(friend));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    protected void selectTab(int tabNumber) {
        slidingTabs.setScrollPosition(tabNumber, 0f, true);
        pager.setCurrentItem(tabNumber);
    }

    protected List<PagerAdapter.Page> getPages() {
        List<PagerAdapter.Page> result = new ArrayList<>();
        result.add(new PagerAdapter.Page(R.drawable.ic_user, FriendStatisticFragment.newInstance(friend)));
        result.add(new PagerAdapter.Page(R.drawable.ic_trophy, AchievementTabFragment.newInstance(friend.getId())));
        return result;
    }
}
