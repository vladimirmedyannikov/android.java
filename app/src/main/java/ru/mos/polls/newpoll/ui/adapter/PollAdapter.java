package ru.mos.polls.newpoll.ui.adapter;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public interface Type {
        int ITEM_ACTIVE = 0;
        int ITEM_OLD = 1;
    }

}
