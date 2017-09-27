package ru.mos.polls.newpoll.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.newpoll.vm.item.PollItemActiveVM;
import ru.mos.polls.newpoll.vm.item.PollItemOldVM;
import ru.mos.polls.poll.model.Poll;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public PollAdapter() {
    }

    public interface Type {
        int ITEM_ACTIVE = 0;
        int ITEM_OLD = 1;
    }

    public void add(List<Poll> polls, int type) {
        List<RecyclerBaseViewModel> content = new ArrayList<>();
        RecyclerBaseViewModel pollVM = null;
        for (Poll poll : polls) {
            switch (type) {
                case Type.ITEM_ACTIVE:
                    pollVM = new PollItemActiveVM(poll);
                    break;
                case Type.ITEM_OLD:
                    pollVM = new PollItemOldVM(poll);
                    break;
            }
            content.add(pollVM);
        }
        addData(content);
    }

    public void removeItem(Poll poll) {
        for (RecyclerBaseViewModel recyclerBaseViewModel : list) {
            if (poll.getId() == ((Poll)recyclerBaseViewModel.getModel()).getId()) {
                System.out.println("POOOOLLL removeItem " + poll.getId());
                list.remove(recyclerBaseViewModel);
            }
        }
        notifyDataSetChanged();
    }
}
