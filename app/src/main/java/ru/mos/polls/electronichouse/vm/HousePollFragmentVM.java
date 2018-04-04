package ru.mos.polls.electronichouse.vm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.rxjava.RxEventDisposableSubscriber;
import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.FragmentHousePollBinding;
import ru.mos.polls.electronichouse.ui.fragment.HousePollFragment;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.poll.service.PollSelect;
import ru.mos.polls.poll.ui.adapter.PollAdapter;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.GuiUtils;
import ru.mos.polls.util.UrlHelper;
import ru.mos.polls.webview.state.WebViewState;


public class HousePollFragmentVM extends PullablePaginationFragmentVM<HousePollFragment, FragmentHousePollBinding, PollAdapter> {

    public static final String ACTION_POLL_CHANGED = "ru.mos.polls.electronichouse.vm.ACTION_POLL_CHANGED";
    public static final String ARG_POLL = "arg_poll";
    FloatingActionButton fabAddHousePoll;
    private BroadcastReceiver pollPassedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long pollId = intent.getLongExtra(ARG_POLL, 0);
            if (adapter != null) {
                Poll poll = adapter.getPoll(pollId);
                if (poll != null) {
                    adapter.removeItem(poll);
                    poll.setStatus(Poll.Status.PASSED.status);
                    poll.setPassedDate(System.currentTimeMillis() / 1000L);
                    adapter.addOldPoll(poll);
                }
            }
        }
    };

    public HousePollFragmentVM(HousePollFragment fragment, FragmentHousePollBinding binding) {
        super(fragment, binding);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        recyclerUIComponent.setEmptyText(R.string.empty_housepolls_hint);
        LocalBroadcastManager.getInstance(getFragment().getContext()).registerReceiver(pollPassedReceiver, new IntentFilter(ACTION_POLL_CHANGED));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getFragment().getContext()).unregisterReceiver(pollPassedReceiver);
    }

    @Override
    protected void initialize(FragmentHousePollBinding binding) {
        fabAddHousePoll = binding.fabAddHousePoll;
        recyclerView = binding.list;
        adapter = new PollAdapter();
        subscribeEventsBus();
        fabAddHousePoll.setOnClickListener(view -> addClick(view));
        super.initialize(binding);
    }

    private void subscribeEventsBus() {
        disposables.add(AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxEventDisposableSubscriber() {
                    @Override
                    public void onNext(Object o) {
                        if (o instanceof Events.PollEvents) {
                            Events.PollEvents action = (Events.PollEvents) o;
                            switch (action.getEventType()) {
                                case Events.PollEvents.INTERRUPTED_POLL:
                                    processPoll(action.getPollId(), action.getEventType());
                                    break;
                            }
                        }
                    }
                }));
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
                }
            }
        }
    }

    @Override
    public void doRequest() {
        HandlerApiResponseSubscriber<PollSelect.Response.Result> handler =
                new HandlerApiResponseSubscriber<PollSelect.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(PollSelect.Response.Result result) {
                        progressable = getPullableProgressable();
                        adapter.add(result.getPolls(), PollAdapter.Type.ITEM_DEFAULT);
                        isPaginationEnable = result.getPolls().size() >= page.getSize();
                        recyclerUIComponent.refreshUI();
                    }
                };
        List<PollSelect.Source> sources = new ArrayList<>();
        sources.add(PollSelect.Source.OSS);
        PollSelect.Request requestBody = new PollSelect.Request(page, sources, null);
        Observable<PollSelect.Response> responseObservable = AGApplication.api
                .pollselect(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

    void addClick(View v) {
        GuiUtils.displayYesOrNotDialog(getActivity(), getActivity().getString(R.string.go_to_house_poll_desc), (DialogInterface.OnClickListener) (dialog, which) -> {
            getFragment().navigateTo(WebViewState.getStateSetCookie( null,
                    getActivity().getString(R.string.electronic_house_placing_an_order),
                    UrlHelper.getHouseConstructorUrl()), ru.mos.polls.base.activity.BaseActivity.class);
        }, null);
    }
}
