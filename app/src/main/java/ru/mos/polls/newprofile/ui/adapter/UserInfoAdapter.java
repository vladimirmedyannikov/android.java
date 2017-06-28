package ru.mos.polls.newprofile.ui.adapter;

import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.databinding.UserInfoItemBinding;
import ru.mos.polls.databinding.UserStatsItemBinding;
import ru.mos.polls.newprofile.base.ui.BindingHolder;
import ru.mos.polls.newprofile.base.ui.adapter.BaseAdapter;
import ru.mos.polls.newprofile.model.UserInfo;
import ru.mos.polls.newprofile.vm.UserInfoVM;

/**
 * Created by Trunks on 19.06.2017.
 */

public class UserInfoAdapter extends BaseAdapter<UserInfoVM, BindingHolder<UserInfoItemBinding>, UserInfoItemBinding, UserInfo> {

    public UserInfoAdapter(List<UserInfo> list) {
        this.list = list;
    }

    @Override
    public BindingHolder<UserInfoItemBinding> getVH(UserInfoItemBinding binding) {
        return new BindingHolder<>(binding);
    }

    @Override
    public UserInfoVM getVM(UserInfo obj, UserInfoItemBinding binding) {
        return new UserInfoVM(obj, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.user_info_item;
    }
}
