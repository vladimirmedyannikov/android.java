package ru.mos.polls.newpoll.vm.item;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemActivePollBinding;
import ru.mos.polls.newpoll.ui.adapter.PollAdapter;
import ru.mos.polls.poll.model.Poll;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollItemActiveVM extends RecyclerBaseViewModel<Poll, ItemActivePollBinding> {

    public PollItemActiveVM(Poll model, ItemActivePollBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public PollItemActiveVM(Poll model) {
        super(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_active_poll;
    }

    @Override
    public int getViewType() {
        return PollAdapter.Type.ITEM_ACTIVE;
    }

    @Override
    public void onBind(ItemActivePollBinding viewDataBinding) {
        super.onBind(viewDataBinding);
    }
}
