package ru.mos.polls.newprofile;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.mos.polls.R;

/**
 * Created by Trunks on 06.06.2017.
 */

public class ProfileFtagment extends BaseNavigationFragment  {

    public static ProfileFtagment newInstance() {
        ProfileFtagment f = new ProfileFtagment();

        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.new_profile_layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
