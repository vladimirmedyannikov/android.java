package ru.mos.polls.ourapps;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.FragmentOurAppsBinding;
import ru.mos.polls.ourapps.service.GetOurApps;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;


public class OurAppsFragmentVM extends PullablePaginationFragmentVM<OurAppsFragment, FragmentOurAppsBinding, OurAppsAdapter> {


    public OurAppsFragmentVM(OurAppsFragment fragment, FragmentOurAppsBinding binding) {
        super(fragment, binding);
    }

    @Override
    public void doRequest() {
        HandlerApiResponseSubscriber<GetOurApps.Response.Result> handler = new HandlerApiResponseSubscriber<GetOurApps.Response.Result>(getActivity(), progressable) {
            @Override
            protected void onResult(GetOurApps.Response.Result result) {

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
