package ru.mos.polls.wizardprofile.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import ru.mos.elk.profile.AgUser;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardProfilePagerAdapter extends FragmentStatePagerAdapter {
    AgUser agUser;
    List<Fragment> list;

    public WizardProfilePagerAdapter(FragmentManager fm, AgUser agUser, List<Fragment> list) {
        super(fm);
        this.agUser = agUser;
        this.list = list;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

}