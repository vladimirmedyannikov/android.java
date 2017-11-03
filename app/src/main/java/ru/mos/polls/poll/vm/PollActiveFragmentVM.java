package ru.mos.polls.poll.vm;

import android.support.v7.widget.SwitchCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.poll.model.Filter;
import ru.mos.polls.poll.ui.PollBaseFragment;
import ru.mos.polls.poll.ui.adapter.PollAdapter;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.subscribes.controller.SubscribesAPIController;
import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;

/**
 * Created by Trunks on 05.10.2017.
 */

public class PollActiveFragmentVM extends PollBaseFragmentVM {

    private SwitchCompat subscribeQuestionsEmail;
    private SubscribesAPIController subscribesController;

    public PollActiveFragmentVM(PollBaseFragment fragment, FragmentTabPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentTabPollBinding binding) {
        pollType = PollAdapter.Type.ITEM_ACTIVE;
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
                case Events.PollEvents.FINISHED_POLL:
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
                    case Events.PollEvents.FINISHED_POLL:
                        poll.setStatus(Poll.Status.PASSED.status);
                        poll.setPassedDate(System.currentTimeMillis() / 1000);
                        adapter.removeItem(poll);
                        list.remove(poll);
                        AGApplication.bus().send(new Events.PollEvents(Events.PollEvents.ADD_OLD_POLL, poll));
                        updateView();
                        break;
                }
            }
        }
    }
}
