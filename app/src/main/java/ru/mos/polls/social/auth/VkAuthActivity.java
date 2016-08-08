package ru.mos.polls.social.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

import ru.mos.polls.R;
import ru.mos.polls.social.manager.SocialManager;


public class VkAuthActivity extends ActionBarActivity {
    private static final String sTokenKey = "VK_ACCESS_TOKEN";
    private static final String[] sMyScope = new String[]{
            VKScope.OFFLINE,
            VKScope.WALL
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, getString(R.string.vk_app_id), VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
        VKSdk.authorize(sMyScope);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VKSdk.VK_SDK_REQUEST_CODE && (data == null || "com.vk.auth-token".equalsIgnoreCase(data.getAction()))) {
            finish();
        }
    }

    private void saveUserData(VKAccessToken accessToken) {
        SocialManager.setAccessToken(VkAuthActivity.this, SocialManager.SOCIAL_ID_VK, accessToken.accessToken);
        SocialManager.setRefreshToken(VkAuthActivity.this, SocialManager.SOCIAL_ID_VK, "");
        SocialManager.setUserIdVk(this, accessToken.userId);
        final long expireDate = System.currentTimeMillis() + accessToken.expiresIn * 1000L;
        SocialManager.setExpired(VkAuthActivity.this, SocialManager.SOCIAL_ID_VK, expireDate);
        finish();
    }

    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            finish();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            saveUserData(newToken);
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            saveUserData(token);
        }
    };

}
