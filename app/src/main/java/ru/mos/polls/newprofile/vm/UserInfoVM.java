package ru.mos.polls.newprofile.vm;

import android.databinding.BaseObservable;

import ru.mos.polls.databinding.UserInfoItemBinding;
import ru.mos.polls.newprofile.model.UserInfo;

/**
 * Created by Trunks on 19.06.2017.
 */

public class UserInfoVM extends BaseObservable {
    private UserInfo userData;
    private UserInfoItemBinding binding;

    public UserInfoVM(UserInfo userData, UserInfoItemBinding binding) {
        this.userData = userData;
        this.binding = binding;
    }

    public String getValue() {
        return userData.getValue();
    }

    public String getTitle() {
        return userData.getTitle();
    }
}
