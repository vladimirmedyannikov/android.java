package ru.mos.polls.friend.ui.utils;

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
        imageLoader.displayImage(url, v, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                Log.wtf("LoadingImage", "failed \n" + imageUri + "\n" + failReason.toString());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                Log.wtf("LoadingImage", "complete \n" + imageUri);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
//                Log.wtf("LoadingImage", "cancelled \n" + imageUri);
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

    public static boolean isPhoneValid(String phone) {
        return !TextUtils.isEmpty(phone)
                && phone.length() == 11
                && phone.charAt(0) == '7'
                && TextUtils.isDigitsOnly(phone);

    }
}
