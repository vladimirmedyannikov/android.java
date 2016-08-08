package ru.mos.polls.social.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.Scopes;

import java.io.IOException;

import ru.mos.polls.R;


public class GpHelper {
    public static final String GP_PACKAGE = "com.google.android.apps.plus";
    public static final String GP_URL = "https://play.google.com/store/apps/details?id=com.google.android.apps.plus";

    public static boolean isGooglePlusInstalled(Context context) {
        try {
            context.getPackageManager().getApplicationInfo(GP_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void setErrorDialog(final Context context) {
        setErrorDialog(context, false);
    }

    public static void setErrorDialog(final Context context, final boolean activityWillBeFinished) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.not_installed_google_plus_app);
        builder.setPositiveButton(R.string.survey_done_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(GP_URL));
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (context instanceof Activity && activityWillBeFinished) {
                    ((Activity) context).finish();
                }
            }
        });
        builder.show();
    }

    public static String getAccessToken(Context context, String accountName) throws Exception {
        GetGpTokenTask getGpTokenTask = new GetGpTokenTask(context, accountName);
        return getGpTokenTask.getAccessToken();
    }

    public static void getAccessToken(Context context, String accountName, GetGpTokenTask.OnTokenListener onTokenListener) {
        GetGpTokenTask getGpTokenTask = new GetGpTokenTask(context, accountName);
        getGpTokenTask.getAccessToken(onTokenListener);
    }

    public static class GetGpTokenTask extends Thread {
        private static final String OAUTH2 = "oauth2:";

        private Context context;
        private String accountName;
        private String accessToken;
        private String permissions;
        private OnTokenListener onTokenListener;

        public GetGpTokenTask(Context context, String accountName) {
            this.context = context;
            this.accountName = accountName;
            permissions = OAUTH2 + Scopes.PLUS_LOGIN + " " + Scopes.PROFILE; //set default permissions
        }

        @Override
        public void run() {
            accessToken = null;
            try {
                accessToken = GoogleAuthUtil.getToken(context, accountName, permissions);
                if (onTokenListener != null) {
                    onTokenListener.onTokenRetrieve(accessToken);
                }
            } catch (IOException ignored) {
            } catch (GoogleAuthException ignored) {
            }
        }

        public void setPermissions(String permissions) {
            this.permissions = permissions;
        }

        public void setOnTokenListener(OnTokenListener onTokenListener) {
            this.onTokenListener = onTokenListener;
        }

        /*
         * Асинхронный метод получения токена
         */
        public void getAccessToken(OnTokenListener onTokenListener) {
            this.onTokenListener = onTokenListener;
            start();
        }

        /*
         * Метод получения токена в том же потоке,
         * в котором вызывается метод (должен быть не UI поток)
         */
        public String getAccessToken() throws Exception {
            accessToken = GoogleAuthUtil.getToken(context, accountName, permissions);
            return accessToken;
        }

        public interface OnTokenListener {
            void onTokenRetrieve(String accessToken);
        }
    }
}
