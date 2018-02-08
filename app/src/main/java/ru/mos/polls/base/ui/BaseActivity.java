package ru.mos.polls.base.ui;


import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by wlTrunks on 15.06.2017.
 */

public class BaseActivity extends ru.mos.polls.base.activity.BaseActivity {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
