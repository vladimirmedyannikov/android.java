package ru.mos.polls.friend.vm;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.FriendAddItemBinding;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 13:04.
 */

public class FriendAddItemVW extends RecyclerBaseViewModel<Void, FriendAddItemBinding> {
    public static final int TYPE = 1;

    public FriendAddItemVW(Void model, FriendAddItemBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public FriendAddItemVW(Void model) {
        super(model);
    }

    @Override
    public int getViewType() {
        return TYPE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.friend_add_item;
    }
}
