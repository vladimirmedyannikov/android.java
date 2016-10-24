package ru.mos.polls.profile.gui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.Window;

import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.profile.gui.fragment.BindingSocialFragment;
import ru.mos.polls.quests.controller.QuestStateController;
import ru.mos.polls.quests.quest.ProfileQuest;

public class UpdateSocialActivity extends ToolbarAbstractActivity {
    private static final String EXTRA_IS_TASK = "is_task";

    public static void startActivity(Context context) {
        startActivity(context, false);
    }

    public static void startActivityForQuest(Context context) {
        startActivity(context, true);
    }

    public static void startActivity(Context context, boolean isTask) {
        Intent start = new Intent(context, UpdateSocialActivity.class);
        start.putExtra(EXTRA_IS_TASK, isTask);
        context.startActivity(start);
    }

    private BindingSocialFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_update_social);
        setSupportProgressBarIndeterminateVisibility(false);
        boolean isTask = getIntent().getBooleanExtra(MainActivity.IS_TASK, false);
        setFragment(isTask);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BindingSocialFragment fr = (BindingSocialFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        fr.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (fragment.isQuestExecuted()) {
            QuestStateController.getInstance().updateSocialUnavaible();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.quest_task_done);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        } else super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    private void setFragment(boolean isTask) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragment = BindingSocialFragment.newInstance(isTask);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

}
