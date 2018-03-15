package ru.mos.polls.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.polls.R;

/**
 * Сущности разрешений, имеют методы для получения и проверки их наличия.
 */
public enum PermissionsUtils {

    GPS(R.string.gps_rationale, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
    ALL_EXTERNAL_STORAGE(R.string.external_rationale, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
    WRITE_EXTERNAL_STORAGE(R.string.external_rationale, Manifest.permission.WRITE_EXTERNAL_STORAGE),
    SMS(R.string.read_write_sms_rationale, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
    SMS_RECEIVE(R.string.read_sms_rationale, Manifest.permission.RECEIVE_SMS),
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
}


