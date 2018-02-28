package ru.mos.polls.friend.vm.profile;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.FriendProfileHeaderItemBinding;
import ru.mos.polls.friend.service.FriendProfile;
import ru.mos.polls.friend.ui.adapter.FriendProfileAdapter;


public class FriendProfileHeaderVM extends RecyclerBaseViewModel<FriendProfile, FriendProfileHeaderItemBinding> {

    public FriendProfileHeaderVM(FriendProfile model, FriendProfileHeaderItemBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public FriendProfileHeaderVM(FriendProfile model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.friend_profile_header_item;
    }

    @Override
    public int getViewType() {
        return FriendProfileAdapter.Type.ITEM_HEADER;
    }

    @Override
    public void onBind(FriendProfileHeaderItemBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.getRoot().setOnClickListener(v -> {

        });
    }

    @Override
    public int getVariableId() {
        return BR.viewModel;
    }

}
