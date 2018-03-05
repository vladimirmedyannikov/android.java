package ru.mos.polls.profile.vm;


import android.view.View;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.UserInfoItemBinding;
import ru.mos.polls.profile.model.UserInfo;

public class UserInfoVM extends RecyclerBaseViewModel<UserInfo, UserInfoItemBinding> {

    private View.OnClickListener onItemClickListener;

    public UserInfoVM(UserInfo userData, View.OnClickListener onItemClickListener) {
        super(userData);
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBind(UserInfoItemBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(this);
        viewDataBinding.executePendingBindings();
        if (onItemClickListener != null) {
            viewDataBinding.root.setOnClickListener(onItemClickListener);
        }
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.user_info_item;
    }

    public String getValue() {
        return model.getValue();
    }

    public String getTitle() {
        return model.getTitle();
    }

}
