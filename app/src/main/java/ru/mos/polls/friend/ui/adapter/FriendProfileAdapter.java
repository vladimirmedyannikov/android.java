package ru.mos.polls.friend.ui.adapter;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;


public class FriendProfileAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public interface Type {
        int ITEM_HEADER = 0;
        int ITEM_STATISTICS = 1;
    }

}
