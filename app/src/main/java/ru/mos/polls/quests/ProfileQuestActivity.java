package ru.mos.polls.quests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import org.json.JSONObject;

import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.ProfileManager;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.profile.gui.fragment.AbstractProfileFragment;
import ru.mos.polls.profile.gui.fragment.FamilyFragment;
import ru.mos.polls.profile.gui.fragment.UserPersonalFragment;
import ru.mos.polls.profile.gui.fragment.WorkFragment;
import ru.mos.polls.profile.gui.fragment.location.FlatsFragment;
import ru.mos.polls.quests.controller.QuestStateController;
import ru.mos.polls.quests.quest.ProfileQuest;

/**
 * Экран для выполенения заданий по заполнению личных данных
 * адресов и т.д.
 *
 * @since 1.9
 */
public class ProfileQuestActivity extends ToolbarAbstractActivity implements AbstractProfileFragment.ChangeListener {
    private static final int REQUEST_ADD_FLAT = 1;
    private static final String EXTRA_REQUEST_CODE = "extra_request_code";
    private static final String EXTRA_TASK_ID = "extra_task_id";
    private static final String CHANGED_AG_USER = "changed_ag_user";
    private static final String FRAGMENT_TAG = "fragment_tag";

    public static void startUpdatePersonal(Context context) {
        startActivity(context, ProfileQuest.ID_UPDATE_PERSONAL);
    }

    public static void startUpdateLocation(Context context) {
        startActivity(context, ProfileQuest.ID_UPDATE_LOCATION);
    }

    public static void startUpdateExtraInfo(Context context) {
        startActivity(context, ProfileQuest.ID_UPDATE_EXTRA_INFO);
    }

    public static void startUpdateFamilyInfo(Context context) {
        startActivity(context, ProfileQuest.ID_UPDATE_FAMILY_INFO);
    }

    public static void startActivity(Context context, String taskId) {
        Intent start = new Intent(context, ProfileQuestActivity.class);
        start.putExtra(EXTRA_TASK_ID, taskId);
        context.startActivity(start);
    }

    public static void startActivityAddFlat(Fragment fragment) {
        Intent start = new Intent(fragment.getActivity(), ProfileQuestActivity.class);
        start.putExtra(EXTRA_TASK_ID, ProfileQuest.ID_UPDATE_LOCATION);
        start.putExtra(EXTRA_REQUEST_CODE, REQUEST_ADD_FLAT);
        fragment.startActivityForResult(start, REQUEST_ADD_FLAT);
    }

    public static boolean onResult(int requestCode, int resultCode) {
        return resultCode == RESULT_OK && requestCode == REQUEST_ADD_FLAT;
    }

    private AbstractProfileFragment fragment;
    private Button save;

    private AgUser changed, saved;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ag_task);
        init(savedInstanceState);
        setFragment(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CHANGED_AG_USER, changed);
        if (fragment != null) {
            outState.putString(FRAGMENT_TAG, fragment.getClass().getName());
        }
    }

    @Override
    protected void findViews() {
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doQuest();
            }
        });
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            changed = (AgUser) savedInstanceState.getSerializable(CHANGED_AG_USER);
        } else {
            changed = new AgUser(this);
        }
        saved = new AgUser(this);
        taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
    }

    private void setFragment(Bundle savedInstanceState) {
        int title = -1;
        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState != null) {
            fragment = (AbstractProfileFragment) fm.findFragmentByTag(savedInstanceState.getString(FRAGMENT_TAG));
        }
        if (fragment == null) {
            if (ProfileQuest.ID_UPDATE_PERSONAL.equalsIgnoreCase(taskId)) {
                fragment = UserPersonalFragment.newInstance();
                title = R.string.quest_title_personal;
            } else if (ProfileQuest.ID_UPDATE_LOCATION.equalsIgnoreCase(taskId)) {
                int requestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, -1);
                if (requestCode == REQUEST_ADD_FLAT) {
                    fragment = FlatsFragment.newInstanceWithoutWarning();
                } else {
                    fragment = FlatsFragment.newInstance();
                }
                title = R.string.quest_title_location;
            } else if (ProfileQuest.ID_UPDATE_EXTRA_INFO.equalsIgnoreCase(taskId)) {
                fragment = WorkFragment.newInstance();
                title = R.string.quest_title_extra_info;
            } else if (ProfileQuest.ID_UPDATE_FAMILY_INFO.equalsIgnoreCase(taskId)) {
                fragment = FamilyFragment.newInstance();
                title = R.string.quest_title_family_info;
            }
            if (title != -1) {
                TitleHelper.setTitle(this, title);
            }
            fm.beginTransaction().add(R.id.container, fragment, fragment.getClass().getName()).commit();
        }
        fragment.setChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fragment != null && fragment.isAdded() && fragment.isVisible()) {
            fragment.refreshUI(changed);
        }
    }

    private void doQuest() {
        startProgress();
        ProfileManager.SaveAgUserListener saveAgUserListener = new ProfileManager.SaveAgUserListener() {
            @Override
            public void onSaved(JSONObject resultJson) {
                changed.save(ProfileQuestActivity.this);
                processResults(resultJson);
                stopProgress();
                QuestStateController.getInstance().add(taskId);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(ProfileQuestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                stopProgress();
            }
        };
        boolean isNedChildCount = fragment instanceof FamilyFragment;
        ProfileManager.setProfile(this, saved.getRequestBody(changed, isNedChildCount, false), saveAgUserListener);
    }

    private void processResults(JSONObject resultJson) {
        QuestMessage message = new QuestMessage(resultJson);
        if (!message.isEmpty()) {
            message.show(this, true);
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onChange(int fragmentId) {
        fragment.updateAgUser(changed);
        save.setEnabled(fragment.isFilledAndChanged(saved, changed));
    }
}
