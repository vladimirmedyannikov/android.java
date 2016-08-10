package ru.mos.polls.social.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.social.manager.SocialManager;


public class FbAuthActivity extends BaseActivity {
    public static final String PERMISSION_FB_BASIC_USER = "basic_info";
    public static final String PERMISSION_FB_PUBLISH_ACTION = "publish_actions";
    private static final String ERROR_VALIDATING_ACCESS_TOKEN = "Error validating access token";

    final String TAG = FbAuthActivity.class.getName();
    private LoginManager manager = LoginManager.getInstance();
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager.setDefaultAudience(DefaultAudience.FRIENDS);
        manager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        manager.registerCallback(callbackManager, callback);
        manager.logInWithPublishPermissions(this, Arrays.asList(PERMISSION_FB_PUBLISH_ACTION));
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d("FB_CLBCK", loginResult.toString());
            SocialManager.setAccessToken(FbAuthActivity.this, SocialManager.SOCIAL_ID_FB, loginResult.getAccessToken().getToken());
            SocialManager.setRefreshToken(FbAuthActivity.this, SocialManager.SOCIAL_ID_FB, "");
            SocialManager.setExpired(FbAuthActivity.this, SocialManager.SOCIAL_ID_FB, loginResult.getAccessToken().getExpires().getTime());
            finish();
        }

        @Override
        public void onCancel() {
            Log.d("FB_CLBCK", "cancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("FB_CLBCK", error.toString());
        }
    };

    private String getHashKey() {
        String result = "";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ru.mos.polls",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                result = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void showWarningDialog(Exception exception) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FbAuthActivity.this);
        builder.setMessage(getString(R.string.warning_error_validating_access_token));
        builder.setPositiveButton(R.string.ag_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
}
