package ru.mos.polls.newprofile.ui.vm;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;


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
        slidingTabs.setupWithViewPager(pager);
        int[] tabIcons = {
                R.drawable.icon01,
                R.drawable.icon02,
                R.drawable.icon03
        };
        for (int i = 0; i < tabIcons.length; i++) {
            slidingTabs.getTabAt(i).setIcon(tabIcons[i]);
        }
    }
}
