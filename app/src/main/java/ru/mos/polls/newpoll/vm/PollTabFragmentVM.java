package ru.mos.polls.newpoll.vm;


import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.newpoll.service.PollSelect;
import ru.mos.polls.newpoll.ui.PollTabFragment;
import ru.mos.polls.newpoll.ui.adapter.PollAdapter;
import ru.mos.polls.poll.controller.PollApiController;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollTabFragmentVM extends PullablePaginationFragmentVM<PollTabFragment, FragmentTabPollBinding, PollAdapter> {
    static final int ARG_ACTIVE_POLL = 0;
    static final int ARG_OLD_POLL = 1;

    private List<Poll> list;
    private int pollType;

    public PollTabFragmentVM(PollTabFragment fragment, FragmentTabPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentTabPollBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            pollType = extras.getInt(PollTabFragment.ARG_POLL_TYPE);
        }
        switch (pollType) {
            case ARG_ACTIVE_POLL:
                Statistics.enterPollsActive();
                GoogleStatistics.Survey.enterPollsActive();
                subscribeEventsBus();
                break;
            case ARG_OLD_POLL:
                GoogleStatistics.Survey.enterPollsUnactive();
                break;
        }
        recyclerView = binding.list;
        list = new ArrayList<>();
        adapter = new PollAdapter();
        super.initialize(binding);
    }

    private void subscribeEventsBus() {
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.PollEvents) {
                        Events.PollEvents action = (Events.PollEvents) o;
                        switch (action.getEventType()) {
                            case Events.PollEvents.FINISHED_POLL:
                            case Events.PollEvents.INTERRUPTED_POLL:
                                processPoll(action.getPollId(), action.getEventType());
                                break;
                        }
                    }
                });
    }

    @Override
    public void doRequest() {
        HandlerApiResponseSubscriber<PollSelect.Response.Result> handler =
                new HandlerApiResponseSubscriber<PollSelect.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(PollSelect.Response.Result result) {
                        adapter.add(result.getPolls(), pollType);
                        isPaginationEnable = result.getPolls().size() >= page.getSize();
                        recyclerUIComponent.refreshUI();
                    }
                };
        List<String> filters = new ArrayList<>();
        addFilters(filters, pollType);
        PollSelect.Request requestBody = new PollSelect.Request(page, filters);
        Observable<PollSelect.Response> responseObservable = AGApplication.api
                .pollselect(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

    private void addFilters(List<String> filters, int type) {
        if (type == ARG_ACTIVE_POLL) {
            filters.add(PollApiController.Filter.AVAILABLE.toString());
        }
        if (type == ARG_OLD_POLL) {
            filters.add(PollApiController.Filter.OLD.toString());
            filters.add(PollApiController.Filter.PASSED.toString());
        }
    }

    private void processPoll(long pollId, int typeEvent) {
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
                        break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
}
