package ru.mos.polls.newpoll.vm;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.base.vm.PullableFragmentVM;
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

public class PollTabFragmentVM extends PullableFragmentVM<PollTabFragment, FragmentTabPollBinding, PollAdapter> {
    List<Poll> list;
    public int type;

    public PollTabFragmentVM(PollTabFragment fragment, FragmentTabPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentTabPollBinding binding) {
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
                        adapter.add(result.getPolls(), type);
                        progressPull();
                    }
                };
        List<String> filters = new ArrayList<>();
        filters.add(PollApiController.Filter.AVAILABLE.toString());
        PollSelect.Request requestBody = new PollSelect.Request(page, filters);
        Observable<PollSelect.Response> responseObservable = AGApplication.api
                .pollselect(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

}
