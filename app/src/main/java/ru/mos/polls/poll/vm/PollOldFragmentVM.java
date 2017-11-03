package ru.mos.polls.poll.vm;

import android.view.View;

import java.util.List;

import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.poll.model.Filter;
import ru.mos.polls.poll.ui.PollBaseFragment;
import ru.mos.polls.poll.ui.adapter.PollAdapter;

/**
 * Created by Trunks on 05.10.2017.
 */

public class PollOldFragmentVM extends PollBaseFragmentVM {
    public PollOldFragmentVM(PollBaseFragment fragment, FragmentTabPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentTabPollBinding binding) {
        pollType = PollAdapter.Type.ITEM_OLD;
        super.initialize(binding);
        subscriptionsContainer.setVisibility(View.GONE);
        GoogleStatistics.Survey.enterPollsUnactive();
    }

    @Override
    protected void eventsBusPollAction(Object o) {
        if (o instanceof Events.PollEvents) {
            Events.PollEvents action = (Events.PollEvents) o;
            switch (action.getEventType()) {
                case Events.PollEvents.ADD_OLD_POLL:
                    list.add(0, action.getPoll());
                    adapter.addOldPoll(action.getPoll());
                    break;
            }
        }
    }

    @Override
    protected void addFilters(List<String> filters) {
        filters.add(Filter.OLD.toString());
        filters.add(Filter.PASSED.toString());
    }
}
