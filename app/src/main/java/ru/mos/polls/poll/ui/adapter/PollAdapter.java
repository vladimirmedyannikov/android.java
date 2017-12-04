package ru.mos.polls.poll.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.poll.vm.item.PollItemActiveVM;
import ru.mos.polls.poll.vm.item.PollItemOldVM;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public PollAdapter() {
    }

    public interface Type {
        int ITEM_ACTIVE = 0;
        int ITEM_OLD = 1;
        /**
         * флаг того, что функция добавления сама определяет тип
         */
        int ITEM_DEFAULT = -1;
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
                case Type.ITEM_DEFAULT:
                    if (poll.isActive() || poll.isInterrupted()) {
                        pollVM = new PollItemActiveVM(poll);
                    } else {
                        pollVM = new PollItemOldVM(poll);
                    }
                    break;
            }
            content.add(pollVM);
        }
        addData(content);
    }

    public void addOldPoll(Poll poll) {
        list.add(0, new PollItemOldVM(poll));
        notifyDataSetChanged();
    }

    public void removeItem(Poll poll) {
        for (RecyclerBaseViewModel recyclerBaseViewModel : list) {
            if (poll.getId() == ((Poll) recyclerBaseViewModel.getModel()).getId()) {
                list.remove(recyclerBaseViewModel);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public Poll getPoll(long pollId) {
        for (RecyclerBaseViewModel recyclerBaseViewModel : list) {
            if (pollId == ((Poll) recyclerBaseViewModel.getModel()).getId()) {
                return (Poll) recyclerBaseViewModel.getModel();
            }
        }
        return null;
    }
}
