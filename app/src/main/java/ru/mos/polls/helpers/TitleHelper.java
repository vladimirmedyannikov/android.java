package ru.mos.polls.helpers;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class TitleHelper {

    public static void setTitle(Activity activity, int title) {
        String titleString = activity.getString(title);
        setTitle(activity, titleString);
    }

    public static void setTitle(Activity activity, String title) {
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && actionBar != null) {
            actionBar.setTitle(title);
        } else {
            activity.setTitle(title);
        }
    }

}
