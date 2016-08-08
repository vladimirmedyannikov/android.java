package ru.mos.polls;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

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
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.permission_not_available)
                    .setPositiveButton(R.string.app_continue, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{permission},
                                    requestCode);
                        }
                    });
            builder.show();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    requestCode);
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