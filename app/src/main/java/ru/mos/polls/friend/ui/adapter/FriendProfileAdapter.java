package ru.mos.polls.friend.ui.adapter;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;

/**
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 14.08.17 21:35.
 */

public class FriendProfileAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public interface Type {
        int ITEM_HEADER = 0;
        int ITEM_STATISTICS = 1;
    }

}
