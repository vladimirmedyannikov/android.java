package ru.mos.polls.poll.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.poll.adapter.ActivePollAdapter;
import ru.mos.polls.poll.controller.PollApiController;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.subscribes.controller.SubscribesAPIController;
import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;
import ru.mos.polls.survey.SurveyActivity;


public class ActivePollsFragment extends AbstractPollsFragment {
    public static ActivePollsFragment newInstance() {
        return new ActivePollsFragment();
    }

    private View subscriptionsContainer;
    private SwitchCompat subscribeQuestionsEmail;

    private SubscribesAPIController subscribesController = new SubscribesAPIController();


    public ActivePollsFragment() {
    }

    public PollRemoveListener pollRemoveListener;

    public void setPollRemoveListener(PollRemoveListener pollRemoveListener) {
        this.pollRemoveListener = pollRemoveListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * отметка отложенного голососвания, если голосование былопрервано
         * удаляем голосование, если его прошли
         */
        if (SurveyActivity.onResult(requestCode, resultCode, data)) {
            long pollId = data.getLongExtra(SurveyActivity.EXTRA_SURVEY_ID, -1);
            Poll.Status status = (Poll.Status) data.getSerializableExtra(SurveyActivity.EXTRA_RESULT_SURVEY_STATE);
            if (pollId != -1 && status != null) {
                for (Poll poll : polls) {
                    if (poll.getId() == pollId) {
                        if (status == Poll.Status.INTERRUPTED) {
                            poll.setStatus(Poll.Status.INTERRUPTED);
                        } else if (status == Poll.Status.PASSED) {
                            polls.remove(poll);
                            if (pollRemoveListener != null) {
                                poll.setStatus(Poll.Status.PASSED);
                                poll.setPassedDate(System.currentTimeMillis());
                                pollRemoveListener.onRemove(poll);
                            }
                        }
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_activepolls, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Statistics.enterPollsActive();
        GoogleStatistics.Survey.enterPollsActive();
    }

    @Override
    protected boolean isPagingEnable() {
        return true;
    }

    @Override
    protected void findViews(View view) {
        super.findViews(view);
        empty = ButterKnife.findById(view, android.R.id.empty);
        listView.setEmptyView(empty);
        subscriptionsContainer = ButterKnife.findById(view, R.id.subscriptionsContainer);
        subscriptionsContainer.setVisibility(View.GONE);
        subscribeQuestionsEmail = ButterKnife.findById(view, R.id.emailNew);
        subscribesController.loadAllSubscribes((BaseActivity) getActivity(), new SubscribesAPIController.StateListener() {
            @Override
            public void onSubscriptionsState(List<Subscription> typeChanells) {
                subscriptionsContainer.setVisibility(View.VISIBLE);
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

    @OnCheckedChanged(R.id.emailNew)
    void subscribeEmail(boolean isChecked) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        Subscription subscription = new Subscription(Subscription.TYPE_AG_NEW);
        subscription.getChannels().add(new Channel(Channel.CHANNEL_EMAIL, isChecked));
        subscriptions.add(subscription);
        subscribesController.saveAllSubscribes((BaseActivity) getActivity(), subscriptions);
    }

    @Override
    ArrayAdapter getAdapter() {
        return new ActivePollAdapter(getActivity(), polls, listener);
    }

    @Override
    PollApiController.Filter[] getFilter() {
        return new PollApiController.Filter[]{PollApiController.Filter.AVAILABLE};
    }

    public interface PollRemoveListener {
        void onRemove(Poll poll);
    }
}
