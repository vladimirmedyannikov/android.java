package ru.mos.polls.social.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;

import org.json.JSONObject;

import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.social.manager.SocialManager;
import ru.ok.android.sdk.OkListener;


public class SocialAuthActivity extends ToolbarAbstractActivity implements OkListener {
    final String TAG = SocialAuthActivity.class.getName();

    public static final int OK_MODE_OAUTH = 1;
    public static final int OK_MODE_SESSION_TOKEN = 2;

    private static final String STATE_SOCIAL_ID = "state_social_id";
    private static final String STATE_AUTH = "state_auth";

    public static final String EXTRA_SOCIAL_ID = "extra_social_id";
    public static final String EXTRA_SOCIAL_VALUE = "extra_social_value";
    public static final String EXTRA_OK_MODE = "extra_ok_mode";

    private final OkAuthWrapper okAuthWrapper = new OkAuthWrapper(this, this);

    private int socialId = -1;
    private int okMode = OK_MODE_SESSION_TOKEN;

    private boolean authRequested = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SOCIAL_ID, socialId);
        outState.putBoolean(STATE_AUTH, authRequested);
        /**
         * http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-h?answertab=active#tab-top
         */
        try {
            super.onSaveInstanceState(outState);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        /**
         * http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-h?answertab=active#tab-top
         */
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception ignored) {
        }
        socialId = savedInstanceState.getInt(STATE_SOCIAL_ID, -1);
        authRequested = savedInstanceState.getBoolean(STATE_AUTH);
        okMode = savedInstanceState.getInt(EXTRA_OK_MODE, OK_MODE_SESSION_TOKEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setHome(false);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (!authRequested) {
            requestAuth();
            authRequested = true;
        } else {
            final Intent data = new Intent();
            data.putExtra(EXTRA_SOCIAL_VALUE, socialId);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void requestAuth() {
        socialId = getIntent().getIntExtra(EXTRA_SOCIAL_VALUE, -1);
        SocialManager.clearAuth(this, socialId);
        final Intent intent;
        switch (socialId) {
            case SocialManager.SOCIAL_ID_TW:
                intent = new Intent(this, TwAuthActivity.class);
                startActivity(intent);
                break;
            case SocialManager.SOCIAL_ID_VK:
                intent = new Intent(this, VkAuthActivity.class);
                startActivity(intent);
                break;
            case SocialManager.SOCIAL_ID_FB:
                intent = new Intent(this, FbAuthActivity.class);
                startActivity(intent);
                break;
            case SocialManager.SOCIAL_ID_OK:
                okMode = getIntent().getIntExtra(EXTRA_OK_MODE, OK_MODE_SESSION_TOKEN);
                okAuthWrapper.requestAuth(okMode == OK_MODE_SESSION_TOKEN);
                break;
            case SocialManager.SOCIAL_ID_GP:
                intent = new Intent(this, GpAuthActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onSuccess(JSONObject json) {
        if (json != null) {
            String token = json.optString("access_token");
            String refresh = json.optString("session_secret_key");
            if (okMode == OK_MODE_OAUTH) {
                SocialManager.setAccessToken(this, SocialManager.SOCIAL_ID_OK, token);
                SocialManager.setRefreshToken(this, SocialManager.SOCIAL_ID_OK, refresh);
            } else {
                SocialManager.setOauthOkToken(this, token);
                SocialManager.setOauthOkSessionSecretToken(this, refresh);
            }
            SocialManager.setExpired(this, SocialManager.SOCIAL_ID_OK, -1L);
            final Intent data = new Intent();
            data.putExtra(EXTRA_SOCIAL_VALUE, socialId);
            setResult(RESULT_OK, data);
        }
        finish();
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "onError");
        setResult(RESULT_CANCELED);
        finish();
    }
}
