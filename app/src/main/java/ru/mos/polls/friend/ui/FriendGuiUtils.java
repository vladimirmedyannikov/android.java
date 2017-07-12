package ru.mos.polls.friend.ui;

import android.text.TextUtils;

import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 12.07.17 12:21.
 */

public class FriendGuiUtils {
    public static String getTitle(Friend friend) {
        String result = "";
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
        return result;
    }
}
