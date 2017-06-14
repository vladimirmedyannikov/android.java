package ru.mos.polls.newprofile.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.mos.polls.newprofile.ui.fragment.AchievementTabFragment;
import ru.mos.polls.newprofile.ui.fragment.InfoTabFragment;
import ru.mos.polls.newprofile.ui.fragment.UserTabFragment;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;

    public ProfilePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserTabFragment.newInstance();
            case 1:
                return new AchievementTabFragment();
            case 2:
                return new InfoTabFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
