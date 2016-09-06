package ru.mos.elk.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.Dialogs;
import ru.mos.elk.R;
import ru.mos.elk.Statistics;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.ProfileManager;
import ru.mos.elk.push.GCMHelper;

public class AuthActivity extends BaseActivity {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    public static final String SKIP_ACTIVITY = "ru.mos.elk.auth.SKIP_ACTIVITY";
    public static final String PASSED_ACTIVITY = "ru.mos.elk.auth.PASSED_ACTIVITY";
    public static final String JUST_AUTHORIZE = "ru.mos.elk.auth.JUST_AUTHORIZE";

    protected static final int REQUEST_REGISTER = 1;
    protected static final int REQUEST_RESTORE = 2;

    protected static final int ERROR_CODE_423 = 423;

    protected EditText etLogin;
    protected EditText etPassword;
    protected TextView tvError;
    protected View btnLogin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        etLogin = ButterKnife.findById(this, R.id.etLogin);
        etPassword = ButterKnife.findById(this, R.id.etPassword);
        tvError = ButterKnife.findById(this, R.id.tvError);
        configLogin();
//        configureRegister();
        configureRestore();
        configureEdits();
        configSkip();
    }

    private void configLogin() {
        btnLogin = ButterKnife.findById(this, R.id.btnAction);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickLogin(v);
            }
        });
    }

    public void onClickLogin(View view) {
        final ProgressDialog dialog = Dialogs.showProgressDialog(this, R.string.elk_wait_authorization);
        etLogin.clearFocus();
        etPassword.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
        tvError.setVisibility(View.GONE);

        ProfileManager.AgUserListener agUserListener = new ProfileManager.AgUserListener() {
            @Override
            public void onLoaded(AgUser agUser) {
                dialog.dismiss();
                onLogon();
            }

            @Override
            public void onError(VolleyError error) {
                dialog.dismiss();
                if (error != null) {
                    if (error.getErrorCode() != ERROR_CODE_423)
                    onLoginFault(error.getMessage());
                    showErrorDialog(error.getErrorCode());
                }
            }
        };
        ProfileManager.getProfile(this, getQueryParams(), agUserListener);
    }

    private JSONObject getQueryParams() {
        JSONObject params = new JSONObject();
        JSONObject auth = new JSONObject();
        try {
            auth.put(LOGIN, makeLogin(etLogin.getText().toString()));
            auth.put(PASSWORD, etPassword.getText().toString());
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

    protected String makeLogin(String loginString) {
        return "7" + loginString;
    }

//    protected void onLogon(JSONObject jsonUserData) {
//        Statistics.logon();
//        Bundle extras = getIntent().getExtras();
//        if(!extras.getBoolean(JUST_AUTHORIZE, false)){
//            Intent intent = new Intent(this, (Class<?>) extras.getSerializable(PASSED_ACTIVITY));
//            intent.putExtras(extras);
//            startActivity(intent);
//        }
//        finish();
//
//        Context cntxt = getApplicationContext();
//		AgProfileManager.clearStoredData(cntxt);
//        AgProfileManager.storeCommon(cntxt, jsonUserData.optJSONObject("common"));
//		AgProfileManager.storePersonal(cntxt, jsonUserData.optJSONObject("personal"));
//
//        API.registerPush(cntxt);
//        API.refreshData(cntxt);
//	}

    protected void onLogon() {
        Statistics.logon();
        Bundle extras = getIntent().getExtras();
        if (!extras.getBoolean(JUST_AUTHORIZE, false)) {
            Intent intent = new Intent(this, (Class<?>) extras.getSerializable(PASSED_ACTIVITY));
            intent.putExtras(extras);
            startActivity(intent);
        }
        finish();
    }

    protected void onLoginFault(String errorMessage) {
        ScrollView sv =  ButterKnife.findById(this, R.id.scrollView);
        sv.pageScroll(View.FOCUS_DOWN);
        tvError.setText(errorMessage);
        tvError.setVisibility(View.VISIBLE);
        tvError.requestFocus();
    }

    protected void showErrorDialog(int error){

    }

    private void configSkip() {
        View view = ButterKnife.findById(this, R.id.btnSkip);
        final Class<?> skipActivityCls = (Class) getIntent().getSerializableExtra(SKIP_ACTIVITY);
        if (skipActivityCls != null) {
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AuthActivity.this, skipActivityCls));
                    finish();
                }
            });
        } else {
            view.setVisibility(View.GONE);
            TextView tvInfo = ButterKnife.findById(this, R.id.tvInfo);
            tvInfo.setText(R.string.elk_info_auth_section);
        }
    }

    protected void configureEdits() {
        SharedPreferences prefs = getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE);
        String phone = prefs.getString(AgUser.PHONE, null);
        if (phone != null && phone.length() > 1)
            etLogin.setText(phone.substring(1));
        TextWatcher watcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                btnLogin.setEnabled(etLogin.getText().length() == 10 && etPassword.getText().length() > 0);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        };
        etLogin.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((etLogin.getText().length() == 10 && etPassword.getText().length() > 0) &&
                        (actionId == R.id.actionLogin || actionId == EditorInfo.IME_ACTION_DONE)) {
                    onClickLogin(null);
                    return true;
                }

                return false;
            }
        });

        View.OnFocusChangeListener onFocusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    tvError.setVisibility(View.GONE);
            }
        };
        etLogin.setOnFocusChangeListener(onFocusListener);
        etPassword.setOnFocusChangeListener(onFocusListener);
    }

    protected void configureRestore() {
        Button restore = ButterKnife.findById(this, R.id.btnRestorePassword);
        restore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, RestoreActivity.class);
                intent.putExtra("phone", etLogin.getText());
                startActivityForResult(intent, REQUEST_RESTORE);
            }
        });
    }

//    protected void configureRegister() {
//        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
//                startActivityForResult(intent, REQUEST_REGISTER);
//            }
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_REGISTER:
            case REQUEST_RESTORE:
                if (resultCode == RESULT_OK)
                    etLogin.setText(data.getStringExtra("phone"));
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
