package ru.mos.polls.newprofile.ui.vm;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ilich.juggler.change.Add;
import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.AGApplication;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewProfileBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.ui.adapter.ProfilePagerAdapter;
import ru.mos.polls.newprofile.ui.fragment.ProfileFragment;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class ProfileFragmentVM extends FragmentViewModel<ProfileFragment, LayoutNewProfileBinding> {
    ViewPager pager;
    ProfilePagerAdapter mAdapter;
    TabLayout slidingTabs;

    public ProfileFragmentVM(ProfileFragment fragment, LayoutNewProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutNewProfileBinding binding) {
        pager = binding.pager;
        slidingTabs = binding.slidingTabs;
        mAdapter = new ProfilePagerAdapter(getFragment().getActivity().getSupportFragmentManager());
        pager.setAdapter(mAdapter);
        slidingTabs.setupWithViewPager(pager);
        int[] tabIcons = {
                R.drawable.ic_user,
                R.drawable.icon02,
                R.drawable.icon03
        };
        for (int i = 0; i < tabIcons.length; i++) {
            slidingTabs.getTabAt(i).setIcon(tabIcons[i]);
        }
//        AGApplication.bus().toObserverable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(o -> {
//                    if (o instanceof Events.ProfileEvents) {
//                        Events.ProfileEvents action = (Events.ProfileEvents) o;
//                        switch (action.getAction()) {
//                            case Events.ProfileEvents.EDIT_USER_INFO:
//                                selectTab(2);
////                                navigateTo().state(Add.newActivity(new AnalyticsState(), MainActivity.class));
//                                JugglerFragment jg = getFragment();
//
//
//                                break;
//                        }
//                    }
//                });
    }

    public void selectTab(int tabNumber) {
        slidingTabs.setScrollPosition(tabNumber, 0f, true);
        pager.setCurrentItem(tabNumber);
    }
}
