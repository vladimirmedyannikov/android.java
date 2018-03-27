package ru.mos.polls.survey.hearing.gui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.popup.PopupController;
import ru.mos.polls.survey.hearing.gui.fragment.PguBindFragment;

@Deprecated
public class PguAuthActivity extends ToolbarAbstractActivity {
    private static final String EXTRA_IS_TASK = "extra_is_task";

    public static final int REQUEST_AUTH_RESULT = 100;
    public static final String EXTRA_AUTH_RESULT = "extra_auth_result";

    public static void startActivity(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), PguAuthActivity.class);
        fragment.startActivityForResult(intent, REQUEST_AUTH_RESULT);
    }

    public static void startActivityForQuest(Activity activity) {
        startActivity(activity, true);
    }

    public static void startActivity(Activity activity) {
        startActivity(activity, false);
    }

    public static void startActivity(Activity activity, boolean isTask) {
        Intent intent = new Intent(activity, PguAuthActivity.class);
        intent.putExtra(EXTRA_IS_TASK, isTask);
        activity.startActivityForResult(intent, REQUEST_AUTH_RESULT);
    }

    public static boolean isAuth(int resultCode, int requestCode, Intent data) {
        boolean result = false;
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_AUTH_RESULT) {
            result = isAuth(data);
        }
        return result;
    }

    public static boolean isAuth(Intent data) {
        boolean result = false;
        if (data != null) {
            result = data.getBooleanExtra(EXTRA_AUTH_RESULT, false);
        }
        return result;
    }

    protected PguBindFragment pguBindFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewId());
        ButterKnife.bind(this);
        TitleHelper.setTitle(this, R.string.title_pgu_auth);
    }

    protected int getViewId() {
        return R.layout.activity_pgu_bind;
    }

    @Override
    protected void findViews() {
        processPguState();
        pguBindFragment = (PguBindFragment) getSupportFragmentManager().findFragmentById(R.id.pguBindFragment);
        if (pguBindFragment != null) {
            pguBindFragment.setPguBindListener(new PguBindFragment.PguBindingListener() {
                @Override
                public void onPrepare() {
                    startProgress();
                }

                @Override
                public void onSuccess(QuestMessage questMessage) {
                    stopProgress();
                    setResult(true);
                    if (questMessage != null && !questMessage.isEmpty()) {
                        questMessage.show(PguAuthActivity.this, true);
                    } else {

                        finish();
                    }
                }

                @Override
                public void onError() {
                    stopProgress();
                }
            });
        }
    }

    @OnClick(R.id.help)
    void help(View v) {
        AbstractActivity.hideSoftInput(PguAuthActivity.this, v);
        PopupController.pgu(PguAuthActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        hideSoftInput(this, findViewById(R.id.help));
    }

    private void processPguState() {
        TextView state = ButterKnife.findById(this, R.id.state);
        int stateValue = R.string.state_pgu_disconnected;
        int stateColor = R.color.greyHint;
        int drawable = 0;
        if (AgUser.isPguConnected(this)) {
            stateValue = R.string.state_pgu_connected;
            stateColor = R.color.greenText;
            drawable = R.drawable.abc_ic_cab_done;
        }
        state.setText(stateValue);
        state.setTextColor(getResources().getColor(stateColor));
        state.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }

    private void setResult(boolean isAuth) {
        Intent result = new Intent();
        result.putExtra(EXTRA_AUTH_RESULT, isAuth);
        setResult(RESULT_OK, result);
    }
}
