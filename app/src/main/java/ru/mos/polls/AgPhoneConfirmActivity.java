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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import me.ilich.juggler.change.Add;
import ru.mos.polls.auth.state.AgAuthState;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.broadcast.SmsBroadcastReceiver;
import ru.mos.polls.profile.ProfileManagerRX;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.push.GCMHelper;
import ru.mos.polls.support.state.SupportState;
import ru.mos.polls.tutorial.TutorialActivity;
import ru.mos.polls.tutorial.TutorialFragment;
import ru.mos.polls.util.Dialogs;
import ru.mos.polls.util.GuiUtils;

/**
 * @since 2.3
 */

public class AgPhoneConfirmActivity extends BaseActivity {
    public static final int CONFIRM_CODE_NOT_VALID = 401;
    public static final String EXTRA_PHONE = "extra_phone";
    protected Toolbar toolbar;

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
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_confirm);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Подтверждение");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(smsAuthReceiver, new IntentFilter(SmsBroadcastReceiver.ACTION_CODE_CONFIRMATION_RECEIVED));

        phone = getIntent().getStringExtra(EXTRA_PHONE);
        tvPhone.setText(formatPhone());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                doRequestAction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doRequestAction() {
        if (checkCodeText()) {
            onAction();
        } else {
            Toast.makeText(this, "Введите проверочный код", Toast.LENGTH_SHORT).show();
        }
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

    public boolean checkCodeText() {
        return etCode.getText().length() > 0;
    }

    @OnClick(R.id.feedback)
    public void onFeedback() {
        navigateTo().state(Add.newActivity(new SupportState(true), ru.mos.polls.base.activity.BaseActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(smsAuthReceiver);
    }

    @OnClick(R.id.btnAction)
    public void onAction() {
        final ProgressDialog dialog = Dialogs.showProgressDialog(this, R.string.elk_wait_authorization);
        GuiUtils.hideKeyboard(etCode);
        tvError.setVisibility(View.GONE);

        ProfileManagerRX.AgUserListener listener = new ProfileManagerRX.AgUserListener() {
            @Override
            public void onLoaded(AgUser agUser) {
                Statistics.auth(phone, true);
                GoogleStatistics.Auth.auth(phone, true);
                statistics.check(true);
                dialog.dismiss();
                Statistics.logon();
                onAuthCompleted();
            }

            @Override
            public void onError(String message, int code) {
                dialog.dismiss();
                Statistics.auth(phone, false);
                statistics.check(false);
                statistics.errorOccurs(message);
                String errorMessage = message;
                if (code == CONFIRM_CODE_NOT_VALID) {
                    errorMessage = getString(R.string.auth_error_confirm_code_not_correct);
                }
                tvError.setText(errorMessage);
                tvError.setVisibility(View.VISIBLE);
                tvError.requestFocus();
            }
        };
        ProfileManagerRX.login(disposables, AgPhoneConfirmActivity.this, ProfileManagerRX.getRequest(AgPhoneConfirmActivity.this, phone, etCode.getText().toString()), listener);
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
        navigateTo().state(Add.newActivity(new AgAuthState(), ru.mos.polls.base.activity.BaseActivity.class));
        finish();
    }

    @OnTextChanged(R.id.etCode)
    public void onCodeTextChanged() {
        action.setEnabled(etCode.getText().length() > 0);
    }

    @OnEditorAction(R.id.etCode)
    boolean actionLoginListener(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            doRequestAction();
        }
        return true;
    }

}
