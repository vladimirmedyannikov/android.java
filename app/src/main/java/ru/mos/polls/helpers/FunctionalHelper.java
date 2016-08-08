package ru.mos.polls.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import ru.mos.polls.R;


public abstract class FunctionalHelper {
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    public static void showDisableDialog(Fragment fragment) {
        showDisableDialog(fragment.getActivity());
    }

    public static void showDisableDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.error_for_not_available_hearing);
        builder.setNegativeButton(R.string.ag_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setPositiveButton(R.string.ag_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startGooglePlay(activity);
                activity.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
        builder.show();

    }

    public static void startBrowser(Context context, int link) {
        startBrowser(context, context.getString(link));
    }

    public static void startBrowser(Context context, String link) {
        Intent startBrowser = new Intent();
        startBrowser.setAction(Intent.ACTION_VIEW);
        startBrowser.setData(Uri.parse(link));
        context.startActivity(startBrowser);
    }


    public static void startGooglePlay(Context context) {
        startGooglePlay(context, context.getPackageName());
    }

    public static void startGooglePlay(Context context, String packageName) {
        String url = String.format(context.getString(R.string.app_google_play), packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public static void showInstalledAppDetailsForAg(Context context) {
        showInstalledAppDetails(context, context.getPackageName());
    }

    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }
}
