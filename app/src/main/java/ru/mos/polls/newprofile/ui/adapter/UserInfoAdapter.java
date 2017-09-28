package ru.mos.polls.newprofile.ui.adapter;

import android.content.Context;

import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingHolder;
import ru.mos.polls.base.ui.adapter.BaseAdapter;
import ru.mos.polls.databinding.UserInfoItemBinding;
import ru.mos.polls.newprofile.model.UserInfo;
import ru.mos.polls.newprofile.vm.UserInfoVM;
import ru.mos.polls.util.GuiUtils;

/**
 * Created by Trunks on 19.06.2017.
 */

public class UserInfoAdapter extends BaseAdapter<UserInfoVM, BindingHolder<UserInfoItemBinding>, UserInfoItemBinding, UserInfo> {

    private Context context;

    public UserInfoAdapter(Context context, List<UserInfo> list) {
        this.context = context;
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
            holder.itemView.setOnClickListener(v -> {
                GuiUtils.displayYesOrNotDialog(context,
                        "Вы хотите перейти в режим редактирования данных?",
                        (dialog, which) -> {
                            // TODO: 28.09.17 вызывать окно редактирования плюс ещё надо отельно слушать нажатие на соц сеть, см в InfoTabFragmentVM
                        }, null);
            });
        }
    }
}
