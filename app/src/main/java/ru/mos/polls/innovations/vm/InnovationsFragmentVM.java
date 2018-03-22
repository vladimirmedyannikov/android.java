package ru.mos.polls.innovations.vm;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.rxjava.RxEventDisposableSubscriber;
import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.FragmentInnovationsListBinding;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.innovations.service.NoveltySelect;
import ru.mos.polls.innovations.ui.adapter.InnovationsAdapter;
import ru.mos.polls.innovations.ui.fragment.InnovationsFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

public class InnovationsFragmentVM extends PullablePaginationFragmentVM<InnovationsFragment, FragmentInnovationsListBinding, InnovationsAdapter> {
    public InnovationsFragmentVM(InnovationsFragment fragment, FragmentInnovationsListBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentInnovationsListBinding binding) {
        TitleHelper.setTitle(getActivity(), R.string.title_novelty);
        recyclerView = binding.list;
        adapter = new InnovationsAdapter();
        super.initialize(binding);
        subscribeEventsBus();
    }

    @Override
    public void doRequest() {
        HandlerApiResponseSubscriber<NoveltySelect.Response.Result> handler =
                new HandlerApiResponseSubscriber<NoveltySelect.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(NoveltySelect.Response.Result result) {
                        progressable = getPullableProgressable();
                        adapter.add(result.getInnovations());
                        isPaginationEnable = result.getInnovations().size() > 0;
                        recyclerUIComponent.refreshUI();
                    }
                };
        NoveltySelect.Request requestBody = new NoveltySelect.Request(page);
        Observable<NoveltySelect.Response> responseObservable = AGApplication.api
                .noveltySelect(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

    private void subscribeEventsBus() {
        disposables.add(AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxEventDisposableSubscriber() {
                    @Override
                    public void onNext(Object o) {
                        if (o instanceof Events.InnovationsEvents) {
                            Events.InnovationsEvents action = (Events.InnovationsEvents) o;
                            switch (action.getEventType()) {
                                case Events.InnovationsEvents.PASSED_INNOVATIONS:
                                    adapter.updateInnovations(action.getInnovationId(), action.getRating(), action.getPassedDate());
                                    break;
                            }
                        }
                    }
                }));
    }
}
