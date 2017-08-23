package ru.mos.polls.newprofile.vm;


import ru.mos.polls.databinding.UserInfoItemBinding;
import ru.mos.polls.base.vm.BaseVM;
import ru.mos.polls.newprofile.model.UserInfo;

/**
 * Created by Trunks on 19.06.2017.
 */

public class UserInfoVM extends BaseVM<UserInfo, UserInfoItemBinding> {

    public UserInfoVM(UserInfo userData, UserInfoItemBinding binding) {
        super(userData, binding);
    }

    public String getValue() {
        return model.getValue();
    }

    public String getTitle() {
        return model.getTitle();
    }

}
