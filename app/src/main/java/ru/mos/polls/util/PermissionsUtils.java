package ru.mos.polls.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.polls.R;

/**
 * Сущности разрешений, имеют методы для получения и проверки их наличия.
 */
public enum PermissionsUtils {

    GPS(R.string.gps_rationale, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
    ALL_EXTERNAL_STORAGE(R.string.external_rationale, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
    WRITE_EXTERNAL_STORAGE(R.string.external_rationale, Manifest.permission.WRITE_EXTERNAL_STORAGE),
    SMS(R.string.read_sms_rationale, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
    SMS_SEND(R.string.read_write_sms_rationale, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS),
    /**
     * для отправки смс на {@link Build.VERSION_CODES#O}
     * требуется разрешение {@link android.Manifest.permission#READ_PHONE_STATE}
     */
    SMS_SEND_OREO(R.string.read_write_sms_rationale, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE),
    CAMERA_MEDIA(R.string.camera_rationale, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
    CONTACTS(R.string.contacts_read_rationale, Manifest.permission.READ_CONTACTS);

    private String[] permissions;

    private @StringRes
    int rationaleId;

    PermissionsUtils(@StringRes int rationaleId, String... permissions) {
        this.permissions = permissions;
        this.rationaleId = rationaleId;
    }

    /**
     * @return true если разрешение было получено, false если отсутствует
     */
    public boolean isGranted(Context context) {
        return EasyPermissions.hasPermissions(context, permissions);
    }

    /**
     * Метод для запроса разрешения
     * @param  fragment {@link Fragment} с переопределённым методом {@link Fragment#onRequestPermissionsResult(int, String[], int[]) onRequestPermissionsResult}
     * или с методом, аннотированным {@link pub.devrel.easypermissions.AfterPermissionGranted AfterPermissionGranted}
     */
    public void request(@NonNull Fragment fragment, int requestCode) {
        EasyPermissions.requestPermissions(fragment, fragment.getString(rationaleId), requestCode, permissions);
    }

    /**
     * Метод для запроса разрешения
     * @param  activity {@link Activity} с переопределённым методом {@link Activity#onRequestPermissionsResult(int, String[], int[]) onRequestPermissionsResult}
     * или с методом, аннотированным {@link pub.devrel.easypermissions.AfterPermissionGranted AfterPermissionGranted}
     */
    public void request(@NonNull Activity activity, int requestCode) {
        EasyPermissions.requestPermissions(activity, activity.getString(rationaleId), requestCode, permissions);
    }

    /**
     * для обработки чекбокса "больше не спрашивать"
     */
    public static void onPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        boolean isNeedSettingsShow = false;
        for (int i = 0; i < permissions.length; i++) {
            /**
             * если разрешение не дано, отрабатываем кейс
             */
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                switch (permissions[i]) {
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        isNeedSettingsShow = checkPermissionDeniedPermanently(activity, permissions[i], "Местоположение");
                        break;
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        isNeedSettingsShow = checkPermissionDeniedPermanently(activity, permissions[i], "Память");
                        break;
                    case Manifest.permission.SEND_SMS:
                    case Manifest.permission.READ_SMS:
                    case Manifest.permission.RECEIVE_SMS:
                        isNeedSettingsShow = checkPermissionDeniedPermanently(activity, permissions[i], "SMS");
                        break;
                    case Manifest.permission.CAMERA:
                        isNeedSettingsShow = checkPermissionDeniedPermanently(activity, permissions[i], "Камера");
                        break;
                    case Manifest.permission.READ_CONTACTS:
                        isNeedSettingsShow = checkPermissionDeniedPermanently(activity, permissions[i], "Контакты");
                        break;
                    case Manifest.permission.READ_PHONE_STATE:
                        isNeedSettingsShow = checkPermissionDeniedPermanently(activity, permissions[i], "Телефон");
                        break;
                }
                if (isNeedSettingsShow) return;
            }
        }
    }

    /**
     * проверка того, нажал ли пользователь "больше не спрашивать" при отклонении
     * @param permission разрешение из {@link android.Manifest.permission}
     * @param permissionDescription расшифровка разрешения для пользователя
     * @return true если открыли диалог с предложением перейти в настройки
     */
    private static boolean checkPermissionDeniedPermanently(Activity activity, final String permission, final String permissionDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        /**
         * если после отклонения разрешения функция {@link ActivityCompat#shouldShowRequestPermissionRationale(Activity, String)}
         * вернула false, то пользователь чекнул "больше не спрашивать"
         */
        boolean res = Build.VERSION.SDK_INT >= 23 && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        if (res) {
            String message = "Для корректной работы приложения, требуется получить разрешение \"%s\".\nПерейти в настройки приложения?";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                message += " Если тумблер уже находится в положении \"Вкл.\" то перевключите тумблер.";
            }
            message = String.format(message, permissionDescription);
            builder.setMessage(message)
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
        return res;
    }
}


