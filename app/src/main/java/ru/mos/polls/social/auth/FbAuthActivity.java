package ru.mos.polls.social.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;

import com.facebook.Session;
import com.facebook.SessionState;

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

    private final Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (exception != null && exception.getMessage() != null) {
                if (exception.getMessage().contains(ERROR_VALIDATING_ACCESS_TOKEN)) {
                    showWarningDialog(exception);
                } else {
                    finish();
                }
                return;
            }

            if (session.isOpened()) {
                SocialManager.setAccessToken(FbAuthActivity.this, SocialManager.SOCIAL_ID_FB, session.getAccessToken());
                SocialManager.setRefreshToken(FbAuthActivity.this, SocialManager.SOCIAL_ID_FB, "");
                SocialManager.setExpired(FbAuthActivity.this, SocialManager.SOCIAL_ID_FB, session.getExpirationDate().getTime());
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
        } else {
            session.closeAndClearTokenInformation();
            session = new Session(this);
        }
        Session.setActiveSession(session);
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setPermissions(Arrays.asList(PERMISSION_FB_BASIC_USER)).setCallback(new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    if (session != null && session.isOpened() && !session.isClosed()) {
                        Session.NewPermissionsRequest newPermissionsRequest = new Session
                                .NewPermissionsRequest(FbAuthActivity.this, Arrays.asList(PERMISSION_FB_PUBLISH_ACTION)).setCallback(this);
                        try {
                            session.requestNewPublishPermissions(newPermissionsRequest);
                        } catch (Exception ignored) {
                        }

                    }
                }
            }));
        }
    }

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
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session.saveSession(Session.getActiveSession(), outState);
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
