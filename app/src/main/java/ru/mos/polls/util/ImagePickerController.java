package ru.mos.polls.util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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
    public static final int SELECT_PHOTO = 100;
    public static final int TAKE_PHOTO = 101;
    public static Uri cameraPictureUrl;

    public static void showDialog(final Fragment fragment) {
        final Context context = fragment.getContext();
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
                            getPicture(fragment, TAKE_PHOTO);
                            break;
                        case 1:
                            getPicture(fragment, SELECT_PHOTO);
                            break;
                    }
                });
        builder.show();
    }

    public static void getPicture(final Fragment fragment, int sourceType) {
        int chooseCode = 0;
        Intent pictureChooseIntent = null;
        switch (sourceType) {
            case TAKE_PHOTO:
                cameraPictureUrl = createImageUri(fragment);
                pictureChooseIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pictureChooseIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl);
                chooseCode = REQUEST_CAMERA;
                break;
            case SELECT_PHOTO:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    pictureChooseIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    pictureChooseIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                } else {
                    pictureChooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pictureChooseIntent.setType("image/*");
                chooseCode = REQUEST_GALLERY;
                break;
        }
        fragment.startActivityForResult(pictureChooseIntent, chooseCode);
    }

    public static Uri createImageUri(Fragment fragment) {
        ContentResolver contentResolver = fragment.getActivity().getContentResolver();
        ContentValues cv = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        cv.put(MediaStore.Images.Media.TITLE, timeStamp);
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
    }

    public static void beginCrop(Fragment fragment, int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK && (requestCode == ImagePickerController.REQUEST_CAMERA || requestCode == ImagePickerController.REQUEST_GALLERY)) {
            Uri result = getUri(requestCode, data);
            CropImage.activity(result).start(fragment.getContext(), fragment);
        }
    }

    public static Uri getCropedUri(Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        return result.getUri();
    }

    public static Uri getUri(int requestCode, Intent data) {
        Uri result = null;
        switch (requestCode) {
            case REQUEST_CAMERA:
                result = cameraPictureUrl;
                break;
            case REQUEST_GALLERY:
                result = data.getData();
                break;
        }
        return result;
    }


    public static Observable<Bitmap> uriToBitmap(final Context context, final Uri uri) {
        return Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                emitter.onNext(bitmap);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}

