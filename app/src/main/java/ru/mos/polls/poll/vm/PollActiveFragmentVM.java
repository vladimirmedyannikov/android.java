package ru.mos.polls.poll.vm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.poll.model.Filter;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.poll.ui.PollBaseFragment;
import ru.mos.polls.poll.ui.adapter.PollAdapter;
import ru.mos.polls.subscribes.controller.SubscribesAPIController;
import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;

/**
 * Created by Trunks on 05.10.2017.
 */

public class PollActiveFragmentVM extends PollBaseFragmentVM {

    public static final String ACTION_POLL_IS_PASSED = "ru.mos.polls.poll.vm.poll_is_passed";
    public static final String ARG_POLL_ID = "arg_poll_id";

    private SwitchCompat subscribeQuestionsEmail;
    private SubscribesAPIController subscribesController;
    public List<Integer> finishedPollList;

    private BroadcastReceiver passedPollReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long pollId = intent.getLongExtra(ARG_POLL_ID, 0);
            Poll poll = adapter.getPoll(pollId);
            processFinishedPoll(poll);
        }
    };

    public PollActiveFragmentVM(PollBaseFragment fragment, FragmentTabPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentTabPollBinding binding) {
        pollType = PollAdapter.Type.ITEM_ACTIVE;
        finishedPollList = new ArrayList<>();
        super.initialize(binding);
        subscribeQuestionsEmail = binding.emailNew;
        subscribesController = new SubscribesAPIController();
        Statistics.enterPollsActive();
        GoogleStatistics.Survey.enterPollsActive();
        setListeners();
        updateView();
        loadSubscribe();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        LocalBroadcastManager.getInstance(getFragment().getContext()).registerReceiver(passedPollReceiver, new IntentFilter(ACTION_POLL_IS_PASSED));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getFragment().getContext()).unregisterReceiver(passedPollReceiver);
    }

    @Override
    public void updateView() {
        subscriptionsContainer.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }

    public void loadSubscribe() {
        subscribesController.loadAllSubscribes((BaseActivity) getActivity(), new SubscribesAPIController.StateListener() {
            @Override
            public void onSubscriptionsState(List<Subscription> typeChanells) {
                for (Subscription subscription : typeChanells) {
                    if (subscription.getType().equals(Subscription.TYPE_AG_NEW)) {
                        for (Channel channel : subscription.getChannels()) {
                            if (channel.getName().equals(Channel.CHANNEL_EMAIL)) {
                                subscribeQuestionsEmail.setChecked(channel.isEnabled());
                            }
                        }
                    }
                }

            }

            @Override
            public void onError() {
            }
        });
    }

    public void setListeners() {
        subscribeQuestionsEmail.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subscribeEmail(isChecked);
        });
    }

    public void subscribeEmail(boolean isChecked) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        Subscription subscription = new Subscription(Subscription.TYPE_AG_NEW);
        subscription.getChannels().add(new Channel(Channel.CHANNEL_EMAIL, isChecked));
        subscriptions.add(subscription);
        subscribesController.saveAllSubscribes((BaseActivity) getActivity(), subscriptions);
    }

    @Override
    protected void eventsBusPollAction(Object o) {
        if (o instanceof Events.PollEvents) {
            Events.PollEvents action = (Events.PollEvents) o;
            switch (action.getEventType()) {
//                case Events.PollEvents.FINISHED_POLL:
                case Events.PollEvents.INTERRUPTED_POLL:
                    processPoll(action.getPollId(), action.getEventType());
                    break;
            }
        }
    }

    @Override
    protected void addFilters(List<String> filters) {
        filters.add(Filter.AVAILABLE.toString());
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
//                    case Events.PollEvents.FINISHED_POLL:
//                        processFinishedPoll(poll);
//                        break;
                }
            }
        }
    }

    private void processFinishedPoll(Poll poll) {
        if (!finishedPollList.contains(poll.getId())) {
            finishedPollList.add(poll.getId());
            poll.setStatus(Poll.Status.PASSED.status);
            poll.setPassedDate(System.currentTimeMillis() / 1000);
            adapter.removeItem(poll);
            list.remove(poll);
            Intent oldPollIntent = new Intent(PollOldFragmentVM.ACTION_ADD_OLD_POLL);
            oldPollIntent.putExtra(PollOldFragmentVM.ARG_POLL, poll);
            LocalBroadcastManager.getInstance(getFragment().getContext()).sendBroadcast(oldPollIntent);
            updateView();
        }
    }
}
