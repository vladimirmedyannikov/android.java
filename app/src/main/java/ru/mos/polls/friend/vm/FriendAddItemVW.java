package ru.mos.polls.friend.vm;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.FriendAddItemBinding;
import ru.mos.polls.friend.ui.FriendsAdapter;
import ru.mos.polls.util.GuiUtils;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.07.17 13:04.
 */

public class FriendAddItemVW extends RecyclerBaseViewModel<Object, FriendAddItemBinding> {
    public static final int TYPE = 1;

    public FriendAddItemVW() {
        super(null);
    }

    @Override
    public int getViewType() {
        return FriendsAdapter.Type.ITEM_ADD_FRIEND_BUTTON;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.friend_add_item;
    }

    @Override
    public void onBind(FriendAddItemBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.root.setOnClickListener(v -> {
            GuiUtils.displayOkMessage(v.getContext(), "В разработке", null);
        });
    }
}
