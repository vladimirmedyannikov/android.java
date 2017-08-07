package ru.mos.polls.friend.ui;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.mos.polls.AGApplication;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 12.07.17 12:21.
 */

public class FriendGuiUtils {
    public static String getTitle(Friend friend) {
        String result = "";
        if (friend != null) {
            if (!TextUtils.isEmpty(friend.getName())) {
                result = friend.getName();
            }
            if (!TextUtils.isEmpty(friend.getSurname())) {
                if (!TextUtils.isEmpty(result)) {
                    result += " " + friend.getSurname();
                } else {
                    result = friend.getSurname();
                }
            }
            if (TextUtils.isEmpty(result)) {
                result = friend.getPhone();
            }
        }
        return result;
    }

    public static void loadAvatar(ImageView v, String url) {
        ImageLoader imageLoader = AGApplication.getImageLoader();
        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    v.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }

    public static String formatPhone(String number) {
        if (number != null) {
            number = number.replace("+", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace(" ", "")
                    .replace("#", "")
                    .replace("-", "")
                    .replace("*", "");
            switch (number.length()) {
                case 10:
                    number = "7" + number;
                    break;
                case 11:
                    if (number.charAt(0) != '7') {
                        number = "7" + number.substring(1);
                    }
                    break;
            }

        }
        return number;
    }
}
