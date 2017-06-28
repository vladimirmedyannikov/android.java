package ru.mos.polls.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Trunks on 22.06.2017.
 */

public class FileUtils {
    public static final String TAG = "FileUtils";

    public static void copyFile(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    public static File createNewFile(Context context, String filename) {
        File file = new File(context.getCacheDir() + filename);
        return file;
    }

    public static Bitmap getBitmap(final Context context, final Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static File getFileFromUri(final Context context, final Uri uri, String filename) {
        File oldFile = new File(uri.getPath());
        File newFile = createNewFile(context, filename);
        copyFile(oldFile, newFile);
        return newFile;
    }
}
