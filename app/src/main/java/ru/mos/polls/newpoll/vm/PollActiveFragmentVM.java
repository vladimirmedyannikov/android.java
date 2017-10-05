package ru.mos.polls.newpoll.vm;

import java.util.List;

import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.newpoll.ui.PollBaseFragment;
import ru.mos.polls.poll.controller.PollApiController;
import ru.mos.polls.poll.model.Poll;

/**
 * Created by Trunks on 05.10.2017.
 */

public class PollActiveFragmentVM extends PollBaseFragmentVM {
    public PollActiveFragmentVM(PollBaseFragment fragment, FragmentTabPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentTabPollBinding binding) {
        super.initialize(binding);
        Statistics.enterPollsActive();
        GoogleStatistics.Survey.enterPollsActive();
    }

    @Override
    protected void eventsBusPollAction(Object o) {
        if (o instanceof Events.PollEvents) {
            Events.PollEvents action = (Events.PollEvents) o;
            switch (action.getEventType()) {
                case Events.PollEvents.FINISHED_POLL:
                case Events.PollEvents.INTERRUPTED_POLL:
                    processPoll(action.getPollId(), action.getEventType());
                    break;
            }
        }
    }

    @Override
    protected void addFilters(List<String> filters) {
        filters.add(PollApiController.Filter.AVAILABLE.toString());
    }

    protected void processPoll(long pollId, int typeEvent) {
        Poll poll = adapter.getPoll(pollId);
        if (poll != null) {
            if (poll.getId() == pollId) {
                switch (typeEvent) {
                    case Events.PollEvents.INTERRUPTED_POLL:
                        poll.setStatus(Poll.Status.INTERRUPTED.status);
                        adapter.notifyDataSetChanged();
                        break;
                    case Events.PollEvents.FINISHED_POLL:
                        poll.setStatus(Poll.Status.PASSED.status);
                        poll.setPassedDate(System.currentTimeMillis());
                        adapter.removeItem(poll);
                        list.remove(poll);
                        AGApplication.bus().send(new Events.PollEvents(Events.PollEvents.ADD_OLD_POLL, poll));
                        break;
                }
            }
        }
    }
}
