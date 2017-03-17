package ru.mos.polls;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.Dialogs;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.ProfileManager;
import ru.mos.elk.push.GCMHelper;
import ru.mos.polls.broadcast.SmsBroadcastReceiver;
import ru.mos.polls.support.gui.AgSupportActivity;
import ru.mos.polls.tutorial.TutorialActivity;
import ru.mos.polls.tutorial.TutorialFragment;
import ru.mos.polls.util.GuiUtils;

/**
 * @since 2.3
 */

public class AgPhoneConfirmActivity extends BaseActivity {
    public static final int CONFIRM_CODE_NOT_VALID = 401;
    public static final String EXTRA_PHONE = "extra_phone";

    public static void start(Context context, String phone) {
        Intent activity = new Intent(context, AgPhoneConfirmActivity.class);
        activity.putExtra(EXTRA_PHONE, phone);
        context.startActivity(activity);
    }

    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.btnAction)
    Button action;
    @BindView(R.id.help)
    TextView help;
    @BindView(R.id.tvError)
    TextView tvError;
    private GoogleStatistics.Auth statistics = new GoogleStatistics.Auth();
    private String phone;

    private BroadcastReceiver smsAuthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(SmsBroadcastReceiver.EXTRA_CODE);
            etCode.setText(code);
            onAction();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_confirm);
        ButterKnife.bind(this);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(smsAuthReceiver, new IntentFilter(SmsBroadcastReceiver.ACTION_CODE_CONFIRMATION_RECEIVED));

        phone = getIntent().getStringExtra(EXTRA_PHONE);
        tvPhone.setText(formatPhone());
    }

    private String formatPhone() {
        return "+7 (" +
                phone.substring(0, 3) +
                ") " +
                phone.substring(3, 6) +
                "-" +
                phone.substring(6, 8) +
                "-" +
                phone.substring(8);
    }

    @OnClick(R.id.feedback)
    public void onFeedback() {
        AgSupportActivity.startActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(smsAuthReceiver);
    }

    @OnClick(R.id.btnAction)
    public void onAction() {
        final ProgressDialog dialog = Dialogs.showProgressDialog(this, ru.mos.elk.R.string.elk_wait_authorization);
        GuiUtils.hideKeyboard(etCode);
        tvError.setVisibility(View.GONE);

        ProfileManager.AgUserListener agUserListener = new ProfileManager.AgUserListener() {
            @Override
            public void onLoaded(AgUser agUser) {
                Statistics.auth(phone, true);
                GoogleStatistics.Auth.auth(phone, true);
                statistics.check(true);
                dialog.dismiss();
                ru.mos.elk.Statistics.logon();
                onAuthCompleted();
            }

            @Override
            public void onError(VolleyError error) {
                dialog.dismiss();
                Statistics.auth(phone, false);
                statistics.check(false);
                statistics.errorOccurs(error.getMessage());
                String errorMessage = error.getMessage();
                if (error.getErrorCode() == CONFIRM_CODE_NOT_VALID) {
                    errorMessage = getString(R.string.auth_error_confirm_code_not_correct);
                }
                tvError.setText(errorMessage);
                tvError.setVisibility(View.VISIBLE);
                tvError.requestFocus();
            }
        };
        ProfileManager.getProfile(this, getQueryParams(), agUserListener);
    }

    private void onAuthCompleted() {
        if (!TutorialFragment.Manager.wasShow(this)) {
            TutorialActivity.start(this);
        } else {
            Intent activity = new Intent(this, MainActivity.class);
            startActivity(activity);
        }
        finish();
    }

    private JSONObject getQueryParams() {
        JSONObject params = new JSONObject();
        JSONObject auth = new JSONObject();
        try {
            auth.put("login", "7" + phone);
            auth.put("password", etCode.getText().toString());
            auth.put(GCMHelper.GUID, getSharedPreferences(GCMHelper.PREFERENCES, MODE_PRIVATE).getString(GCMHelper.GUID, null));
            params.put(Session.AUTH, auth);
            SharedPreferences gcmPrefs = getSharedPreferences(GCMHelper.PREFERENCES, MODE_PRIVATE);
            if (!gcmPrefs.getBoolean(GCMHelper.PROPERTY_ON_SERVER, false)) {
                JSONObject deviceInfo = new JSONObject();
                deviceInfo.put("guid", gcmPrefs.getString(GCMHelper.GUID, null));
                deviceInfo.put("object_id", gcmPrefs.getString(GCMHelper.PROPERTY_REG_ID, null));
                deviceInfo.put("user_agent", "Android");
                deviceInfo.put("app_version", GCMHelper.getAppVersionName(this));
                params.put("device_info", deviceInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params;
    }

    @OnClick(R.id.help)
    public void onHelp() {
        AgAuthActivity.start(this);
        finish();
    }

    @OnTextChanged(R.id.etCode)
    public void onCodeTextChanged() {
        action.setEnabled(etCode.getText().length() > 0);
    }

}
