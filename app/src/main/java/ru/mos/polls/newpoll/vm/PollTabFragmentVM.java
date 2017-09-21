package ru.mos.polls.newpoll.vm;


import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.vm.PullableFragmentVM;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.newpoll.service.PollSelect;
import ru.mos.polls.newpoll.ui.PollTabFragment;
import ru.mos.polls.newpoll.ui.adapter.PollAdapter;
import ru.mos.polls.poll.controller.PollApiController;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.survey.SurveyActivity;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollTabFragmentVM extends PullableFragmentVM<PollTabFragment, FragmentTabPollBinding, PollAdapter> {
    public static final int ARG_ACTIVE_POLL = 0;
    public static final int ARG_OLD_POLL = 1;
    List<Poll> list;
    int pollType;

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

    @Override
    public void doRequest() {
        HandlerApiResponseSubscriber<PollSelect.Response.Result> handler =
                new HandlerApiResponseSubscriber<PollSelect.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(PollSelect.Response.Result result) {
                        adapter.add(result.getPolls(), pollType);
                        progressPull();
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

    public void addFilters(List<String> filters, int type) {
        if (type == ARG_ACTIVE_POLL) {
            filters.add(PollApiController.Filter.AVAILABLE.toString());
        }
        if (type == ARG_OLD_POLL) {
            filters.add(PollApiController.Filter.OLD.toString());
            filters.add(PollApiController.Filter.PASSED.toString());
        }
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
                for (Poll poll : list) {
                    if (poll.getId() == pollId) {
                        if (status == Poll.Status.INTERRUPTED) {
                            poll.setStatus(Poll.Status.INTERRUPTED.status);
                        } else if (status == Poll.Status.PASSED) {
                            list.remove(poll);
//                            if (pollRemoveListener != null) {
                                poll.setStatus(Poll.Status.PASSED.status);
                                poll.setPassedDate(System.currentTimeMillis());
//                                pollRemoveListener.onRemove(poll);
//                            }
                        }
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
