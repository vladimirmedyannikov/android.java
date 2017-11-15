package ru.mos.polls.poll.vm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import java.util.List;

import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.poll.model.Filter;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.poll.ui.PollBaseFragment;
import ru.mos.polls.poll.ui.adapter.PollAdapter;

/**
 * Created by Trunks on 05.10.2017.
 */

public class PollOldFragmentVM extends PollBaseFragmentVM {
    public static final String ACTION_ADD_OLD_POLL = "ru.mos.polls.poll.vm.action_add_old_poll";
    public static final String ARG_POLL = "arg_poll";

    private BroadcastReceiver addOldPollReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Poll poll = (Poll) intent.getSerializableExtra(ARG_POLL);
            list.add(0, poll);
            adapter.addOldPoll(poll);
        }
    };

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
    public void onViewCreated() {
        super.onViewCreated();
        recyclerUIComponent.setEmptyText(R.string.poll_old_empty_title);
        LocalBroadcastManager.getInstance(getFragment().getContext()).registerReceiver(addOldPollReceiver, new IntentFilter(ACTION_ADD_OLD_POLL));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getFragment().getContext()).unregisterReceiver(addOldPollReceiver);
    }

    @Override
    protected void eventsBusPollAction(Object o) {
    }

    @Override
    protected void addFilters(List<String> filters) {
        filters.add(Filter.OLD.toString());
        filters.add(Filter.PASSED.toString());
    }
}
