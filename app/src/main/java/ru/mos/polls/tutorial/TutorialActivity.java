package ru.mos.polls.tutorial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ru.mos.polls.R;

public class TutorialActivity extends AppCompatActivity {
    public static void start(Context context) {
        Intent activity = getTutorialActivityIntent(context);
        context.startActivity(activity);
    }


    public static Intent getTutorialActivityIntent(Context context) {
        return new Intent(context, TutorialActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TutorialFragment.newInstance(Tutorial.DEFAULTS))
                .commit();
    }
}
