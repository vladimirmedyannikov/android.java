package ru.mos.polls;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Инкапсуляция работы с {@link RuntimePermission}
 *
 * @since 1.0
 */
public class RuntimePermissionController {
    public static final int REQUEST_CODE_SMS_SEND = 1;
    public static final int REQUEST_CODE_SMS_RECEIVE = 2;

    private Activity activity;

    public RuntimePermissionController(Activity activity) {
        this.activity = activity;
    }

    public boolean hasSmsSend() {
        return has(Manifest.permission.SEND_SMS);
    }

    public boolean hasSmsReceive() {
        return has(Manifest.permission.RECEIVE_SMS);
    }

    /**
     * Проверка налиция разрешения {@link RuntimePermission}
     *
     * @param permission наименование разерешения {@link Manifest.permission}
     * @return true - разрешение есть
     */
    public boolean has(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestSmsSend() {
        requestRuntimePermission(Manifest.permission.SEND_SMS, REQUEST_CODE_SMS_SEND);
    }

    public void requestSmsReceive() {
        requestRuntimePermission(Manifest.permission.RECEIVE_SMS, REQUEST_CODE_SMS_RECEIVE);
    }

    /**
     * Запрос на получение разрешений {@link RuntimePermission}
     *
     * @param permission  наименование разерешения {@link Manifest.permission}
     * @param requestCode код запроса, необходимый, чтобы идентифицировать результат включения или отключения конкретного разрешения
     *                    используется в методе {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     */
    public void requestRuntimePermission(final String permission, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) && ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.permission_not_available)
                    .setPositiveButton(R.string.app_continue, (dialog, which) -> ActivityCompat.requestPermissions(activity,
                            new String[]{permission},
                            requestCode));
            builder.show();
            return;
        }
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED && !EasyPermissions.permissionPermanentlyDenied(activity, permission)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    requestCode);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (Build.VERSION.SDK_INT >= 23 && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            builder.setMessage(R.string.permission_not_available_goto_settings)
                    .setPositiveButton("Да", (dialog, which) -> {
                        Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + activity.getPackageName()));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        activity.startActivity(i);
                    })
                    .setNegativeButton("Нет", null);
            builder.show();
        }
    }

    /**
     * Проверка наличия разрешения {@link RuntimePermission}<br/>
     * Метод вызывается в {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     *
     * @param requestCode  код запроса
     * @param grantResults установено/не установлено разрешение {@link RuntimePermission}
     * @return true - разрешение на чтение смс есть
     */
    public boolean smsReceivePermissionGranted(int requestCode, int[] grantResults) {
        boolean result = false;
        if (requestCode == REQUEST_CODE_SMS_SEND) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

}