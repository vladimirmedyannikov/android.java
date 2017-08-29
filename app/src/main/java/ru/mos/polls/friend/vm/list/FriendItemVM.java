package ru.mos.polls.friend.vm.list;

import ru.mos.polls.AGApplication;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FriendItemBinding;
import ru.mos.polls.friend.ui.adapter.FriendsAdapter;
import ru.mos.polls.friend.ui.utils.FriendGuiUtils;
import ru.mos.polls.rxhttp.rxapi.config.AgApiBuilder;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 10:09.
 */

public class FriendItemVM extends RecyclerBaseViewModel<Friend, FriendItemBinding> {

    public FriendItemVM(Friend model, FriendItemBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public FriendItemVM(Friend model) {
        super(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.friend_item;
    }

    @Override
    public int getViewType() {
        return FriendsAdapter.Type.ITEM_FRIEND;
    }

    @Override
    public void onBind(FriendItemBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        FriendGuiUtils.loadAvatar(viewDataBinding.avatar, AgApiBuilder.resourceURL(model.getAvatar()));
        viewDataBinding.getRoot().setOnClickListener(v -> {
            AGApplication.bus().send(new Events.FriendEvents(model));
        });
    }

    @Override
    public int getVariableId() {
        return BR.viewModel;
    }

}
