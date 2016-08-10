package ru.mos.polls.social.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

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
        VKSdk.login(this, sMyScope);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> vkCallback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                SocialManager.setAccessToken(VkAuthActivity.this, SocialManager.SOCIAL_ID_VK, res.accessToken);
                SocialManager.setRefreshToken(VkAuthActivity.this, SocialManager.SOCIAL_ID_VK, "");
                SocialManager.setExpired(VkAuthActivity.this, SocialManager.SOCIAL_ID_VK, System.currentTimeMillis() + res.expiresIn * 1000L);
                SocialManager.setUserIdVk(VkAuthActivity.this, res.userId);
                finish();
            }

            @Override
            public void onError(VKError error) {
                finish();
            }
        };
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
