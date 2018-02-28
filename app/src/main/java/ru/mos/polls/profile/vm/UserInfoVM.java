package ru.mos.polls.profile.vm;


import ru.mos.polls.base.vm.BaseVM;
import ru.mos.polls.databinding.UserInfoItemBinding;
import ru.mos.polls.profile.model.UserInfo;

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
