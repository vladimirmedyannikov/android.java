package ru.mos.polls.wizardprofile.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.MakeAvatarFragment;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardProfilePagerAdapter extends FragmentStatePagerAdapter {
    AgUser agUser;
    ArrayMap<String, Fragment> list;

    public WizardProfilePagerAdapter(FragmentManager fm, AgUser agUser, ArrayMap<String, Fragment> list) {
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
       return list.valueAt(position);
//        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

}