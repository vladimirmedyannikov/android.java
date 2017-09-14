package ru.mos.polls.newpoll.vm.item;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemActivePollBinding;
import ru.mos.polls.newpoll.ui.adapter.PollAdapter;
import ru.mos.polls.poll.model.Poll;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollItemOldVM extends RecyclerBaseViewModel<Poll, ItemActivePollBinding> {
    public PollItemOldVM(Poll model) {
        super(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_passed_poll;
    }

    @Override
    public int getViewType() {
        return PollAdapter.Type.ITEM_OLD;
    }
}
