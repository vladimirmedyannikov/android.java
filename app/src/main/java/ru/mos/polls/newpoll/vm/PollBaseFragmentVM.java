package ru.mos.polls.newpoll.vm;


import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.FragmentTabPollBinding;
import ru.mos.polls.newpoll.service.PollSelect;
import ru.mos.polls.newpoll.ui.PollBaseFragment;
import ru.mos.polls.newpoll.ui.adapter.PollAdapter;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

/**
 * Created by Trunks on 14.09.2017.
 */

public abstract class PollBaseFragmentVM extends PullablePaginationFragmentVM<PollBaseFragment, FragmentTabPollBinding, PollAdapter> {
    public static final int ARG_ACTIVE_POLL = 0;
    public static final int ARG_OLD_POLL = 1;

    protected List<Poll> list;
    protected int pollType;
    protected View subscriptionsContainer;

    public PollBaseFragmentVM(PollBaseFragment fragment, FragmentTabPollBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentTabPollBinding binding) {
        subscriptionsContainer = binding.subscriptionsContainer;
        subscribeEventsBus();
        recyclerView = binding.list;
        list = new ArrayList<>();
        adapter = new PollAdapter();
        super.initialize(binding);
    }

    protected void subscribeEventsBus() {
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    eventsBusPollAction(o);
                });
    }

    protected abstract void eventsBusPollAction(Object o);

    public void updateView() {
    }

    @Override
    public void doRequest() {
        HandlerApiResponseSubscriber<PollSelect.Response.Result> handler =
                new HandlerApiResponseSubscriber<PollSelect.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(PollSelect.Response.Result result) {
                        progressable = getPullableProgressable();
                        adapter.add(result.getPolls(), pollType);
                        isPaginationEnable = result.getPolls().size() >= page.getSize();
                        recyclerUIComponent.refreshUI();
                        updateView();
                    }
                };
        List<String> filters = new ArrayList<>();
        addFilters(filters);
        PollSelect.Request requestBody = new PollSelect.Request(page, filters);
        Observable<PollSelect.Response> responseObservable = AGApplication.api
                .pollselect(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

    protected abstract void addFilters(List<String> filters);

    @Override
    public void setErrorConnetionView() {
        super.setErrorConnetionView();
        subscriptionsContainer.setVisibility(View.GONE);
    }
}
