package ru.mos.polls.mainbanner;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.mainbanner.service.GetBannerStatistics;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

public class BannerController {


    RecyclerView recyclerView;
    MainBannerView bannerView;
    boolean headerAdded;

    public BannerController(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        bannerView = new MainBannerView(recyclerView.getContext());
    }

    public void requestStatistic() {
        HandlerApiResponseSubscriber<GetBannerStatistics.Response.Result> handler = new HandlerApiResponseSubscriber<GetBannerStatistics.Response.Result>() {
            @Override
            protected void onResult(GetBannerStatistics.Response.Result result) {
                bannerView.clearData();
                bannerView.addItems(prepareList(result.getUsersCount(), result.getPollsCount(), result.getPassedPollsCount()));
                if (!headerAdded) {
                    recyclerView.addItemDecoration(new HeaderDecoration(bannerView));
                    headerAdded = true;
                }
            }

            @Override
            public void onNext(GeneralResponse<GetBannerStatistics.Response.Result> generalResponse) {
                if (!generalResponse.hasError()) {
                    onResult(generalResponse.getResult());
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }
        };
        AGApplication
                .api
                .getBannerStatistics()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }


    public List<BannerItem> prepareList(long usersCount, long pollsPassed, long voteCount) {
        List<BannerItem> result = new ArrayList<>();
        result.add(new BannerItem(R.drawable.ic_banner_citizens, "Активных граждан", usersCount));
        result.add(new BannerItem(R.drawable.ic_banner_hearing_passed, "Прошло голосований", pollsPassed));
        result.add(new BannerItem(R.drawable.ic_banner_citizens_vote, "Принято мнений", voteCount));
        return result;
    }

    public void addHeader() {
        bannerView = new MainBannerView(recyclerView.getContext());
        List<BannerItem> list = new ArrayList<>();
        list.add(new BannerItem(R.drawable.ic_banner_citizens, "Активных граждан", 1974943));
        list.add(new BannerItem(R.drawable.ic_banner_hearing_passed, "Прошло голосований", 2703));
        list.add(new BannerItem(R.drawable.ic_banner_citizens_vote, "Принято мнений", 85928639));
        bannerView.addItems(list);
        recyclerView.addItemDecoration(new HeaderDecoration(bannerView));
    }
}
