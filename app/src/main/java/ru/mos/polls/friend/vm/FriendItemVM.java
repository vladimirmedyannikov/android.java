package ru.mos.polls.friend.vm;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.FriendItemBinding;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 10:09.
 */

public class FriendItemVM extends RecyclerBaseViewModel<Friend, FriendItemBinding> {
    public static final int TYPE = 0;

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
        return TYPE;
    }


}
