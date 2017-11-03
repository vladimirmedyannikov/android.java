package ru.mos.polls.profile.ui.adapter;

import android.view.View;

import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingHolder;
import ru.mos.polls.base.ui.adapter.BaseAdapter;
import ru.mos.polls.databinding.UserInfoItemBinding;
import ru.mos.polls.profile.model.UserInfo;
import ru.mos.polls.profile.vm.UserInfoVM;

/**
 * Created by Trunks on 19.06.2017.
 */

public class UserInfoAdapter extends BaseAdapter<UserInfoVM, BindingHolder<UserInfoItemBinding>, UserInfoItemBinding, UserInfo> {

    private View.OnClickListener onItemClickListener;

    public UserInfoAdapter(View.OnClickListener onItemClickListener, List<UserInfo> list) {
        this.onItemClickListener = onItemClickListener;
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

    @Override
    public void onBindViewHolder(BindingHolder<UserInfoItemBinding> holder, int position) {
        super.onBindViewHolder(holder, position);
        if (position != 0) {
            holder.itemView.setOnClickListener(onItemClickListener);
        }
    }
}
