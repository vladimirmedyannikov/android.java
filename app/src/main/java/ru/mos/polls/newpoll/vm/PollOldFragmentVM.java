package ru.mos.polls.newpoll.vm;

import java.util.List;

import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.newpoll.ui.PollBaseFragment;
import ru.mos.polls.poll.controller.PollApiController;

/**
 * Created by Trunks on 05.10.2017.
 */

public class PollOldFragmentVM extends PollBaseFragmentVM {
    public PollOldFragmentVM(PollBaseFragment fragment, FragmentTabPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentTabPollBinding binding) {
        super.initialize(binding);
        GoogleStatistics.Survey.enterPollsUnactive();
    }

    @Override
    protected void eventsBusPollAction(Object o) {
        if (o instanceof Events.PollEvents) {
            Events.PollEvents action = (Events.PollEvents) o;
            switch (action.getEventType()) {
                case Events.PollEvents.FINISHED_POLL:
                    list.add(0, action.getPoll());
                    adapter.addOldPoll(action.getPoll());
                    break;
            }
        }
    }

    @Override
    protected void addFilters(List<String> filters) {
        filters.add(PollApiController.Filter.OLD.toString());
        filters.add(PollApiController.Filter.PASSED.toString());
    }
}
