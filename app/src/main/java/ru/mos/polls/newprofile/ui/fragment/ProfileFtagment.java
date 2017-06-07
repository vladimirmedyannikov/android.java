package ru.mos.polls.newprofile.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.mos.polls.R;
import ru.mos.polls.newprofile.BaseFragment;

/**
 * Created by Trunks on 06.06.2017.
 */

public class ProfileFtagment extends BaseFragment {

    public static ProfileFtagment newInstance() {
        ProfileFtagment f = new ProfileFtagment();
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_new_profile;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
