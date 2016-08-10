package ru.mos.polls.social.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.social.manager.SocialManager;


public class TwAuthActivity extends BaseActivity {


    private TwitterAuthClient twitterAuthClient;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        twitterAuthClient = new TwitterAuthClient();
        twitterAuthClient.authorize(TwAuthActivity.this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("TW_SDK", result.toString());
                TwitterSession res = result.data;
                SocialManager.setAccessToken(TwAuthActivity.this, SocialManager.SOCIAL_ID_TW, res.getAuthToken().token);
                SocialManager.setRefreshToken(TwAuthActivity.this, SocialManager.SOCIAL_ID_TW, res.getAuthToken().secret);
                SocialManager.setExpired(TwAuthActivity.this, SocialManager.SOCIAL_ID_TW, -1L);
                finish();
            }

            @Override
            public void failure(TwitterException e) {
                Log.d("TW_SDK", e.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, TwAuthActivity.class);
        context.startActivity(intent);
    }
}
