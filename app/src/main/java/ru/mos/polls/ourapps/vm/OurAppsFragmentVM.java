package ru.mos.polls.ourapps.vm;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.FragmentOurAppsBinding;
import ru.mos.polls.ourapps.model.OurApplication;
import ru.mos.polls.ourapps.service.GetOurApps;
import ru.mos.polls.ourapps.ui.OurAppsFragment;
import ru.mos.polls.ourapps.ui.adapter.OurAppsAdapter;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;


public class OurAppsFragmentVM extends PullablePaginationFragmentVM<OurAppsFragment, FragmentOurAppsBinding, OurAppsAdapter> {


    public OurAppsFragmentVM(OurAppsFragment fragment, FragmentOurAppsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentOurAppsBinding binding) {
        recyclerView = binding.list;
        adapter = new OurAppsAdapter();
        super.initialize(binding);
    }

    @Override
    public void doRequest() {
        progressable = getPullableProgressable();
        HandlerApiResponseSubscriber<List<OurApplication>> handler = new HandlerApiResponseSubscriber<List<OurApplication>>(getActivity(), progressable) {
            @Override
            protected void onResult(List<OurApplication> result) {
                adapter.clear();
                adapter.add(result);
            }
        };
        GetOurApps.Request request = new GetOurApps.Request();
        disposables.add(AGApplication
                .api
                .getOurApps(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }
}
