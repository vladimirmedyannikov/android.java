package ru.mos.polls.wizardprofile.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.MakeAvatarFragment;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardProfilePagerAdapter extends FragmentPagerAdapter {
    AgUser agUser;

    public WizardProfilePagerAdapter(FragmentManager fm, AgUser agUser) {
        super(fm);
        this.agUser = agUser;
    }

    public WizardProfilePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MakeAvatarFragment();
            case 1:
                return EditPersonalInfoFragment.newInstance(agUser, EditPersonalInfoFragmentVM.PERSONAL_EMAIL);

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}