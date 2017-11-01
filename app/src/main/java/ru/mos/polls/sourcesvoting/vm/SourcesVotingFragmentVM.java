package ru.mos.polls.sourcesvoting.vm;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.FragmentSourcesVotingBinding;
import ru.mos.polls.newprofile.service.model.EmptyResult;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.sourcesvoting.model.SourcesVotingSet;
import ru.mos.polls.sourcesvoting.service.SourcesGet;
import ru.mos.polls.sourcesvoting.service.SourcesSet;
import ru.mos.polls.sourcesvoting.ui.SourcesVotingFragment;
import ru.mos.polls.sourcesvoting.ui.adapter.SourcesVotingAdapter;

/**
 * Created by Trunks on 13.10.2017.
 */

public class SourcesVotingFragmentVM extends PullablePaginationFragmentVM<SourcesVotingFragment, FragmentSourcesVotingBinding, SourcesVotingAdapter> {

    public SourcesVotingFragmentVM(SourcesVotingFragment fragment, FragmentSourcesVotingBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentSourcesVotingBinding binding) {
        adapter = new SourcesVotingAdapter();
        recyclerView = binding.list;
        super.initialize(binding);
        isPaginationEnable = false;
        setRxEventsBusListener();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
    }

    public void doRequest() {
        HandlerApiResponseSubscriber<SourcesGet.Response.Result> handler
                = new HandlerApiResponseSubscriber<SourcesGet.Response.Result>(getFragment().getContext(), progressable) {
            @Override
            protected void onResult(SourcesGet.Response.Result result) {
                adapter.clear();
                adapter.add(result.getSourcesVotings());
                getComponent(RecyclerUIComponent.class).refreshUI();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        };
        AGApplication
                .api
                .getSources(new AuthRequest())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    public void setRxEventsBusListener() {
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.SourcesVotingEvents) {
                        Events.SourcesVotingEvents events = (Events.SourcesVotingEvents) o;
                        subscribeSourcesVoting(events.getSourcesVotingId(), events.isEnable());
                    }
                });
    }

    public void subscribeSourcesVoting(int sourcesId, boolean enable) {
        List<SourcesVotingSet> list = new ArrayList<>();
        list.add(new SourcesVotingSet(sourcesId, enable));
        HandlerApiResponseSubscriber<EmptyResult[]> handler
                = new HandlerApiResponseSubscriber<EmptyResult[]>(getFragment().getContext(), getProgressable()) {
            @Override
            protected void onResult(EmptyResult[] result) {
            }
        };
        AGApplication
                .api
                .setSources(new SourcesSet.Request(list))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }
}
