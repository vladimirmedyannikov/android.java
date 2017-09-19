package ru.mos.polls.support.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;


public class AgSupportActivity extends ToolbarAbstractActivity {
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AgSupportActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.container, SupportFragment.newInstance())
                    .commit();
        }
    }
}
