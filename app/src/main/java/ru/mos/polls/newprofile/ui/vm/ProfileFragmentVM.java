package ru.mos.polls.newprofile.ui.vm;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import com.astuetz.PagerSlidingTabStrip;

import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutNewProfileBinding;
import ru.mos.polls.newprofile.base.FragmentViewModel;
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
        pager.setCurrentItem(0);
        slidingTabs.getTabAt(0).setIcon(R.drawable.icon01);
    }
}
