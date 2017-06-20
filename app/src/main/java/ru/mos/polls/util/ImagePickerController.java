package ru.mos.polls.util;

import android.Manifest;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import ru.mos.polls.R;

/**
 * Created by Trunks on 20.06.2017.
 */

public class ImagePickerController {
    public static final int MEDIA_PERMISSION_REQUEST_CODE = 987;
    public static final String[] MEDIA_PERMS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int REQUEST_GALLERY = 1004;
    public static final int REQUEST_CAMERA = 1005;

    public static void showDialog(final Fragment f) {
        final Context context = f.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                R.layout.dialog_item, R.id.item);
        arrayAdapter.addAll("Камера", "Галерея");
        builder.setAdapter(
                arrayAdapter,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            break;
                        case 1:
                            break;
                    }
                });
        builder.show();
    }
}
