package ru.mos.polls.news.vm;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.FragmentNewsBinding;
import ru.mos.polls.news.model.News;
import ru.mos.polls.news.service.NewsGet;
import ru.mos.polls.news.ui.NewsFragment;
import ru.mos.polls.news.ui.adapter.NewsAdapter;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 26.02.18.
 */

public class NewsFragmentVM extends PullablePaginationFragmentVM<NewsFragment, FragmentNewsBinding, NewsAdapter> {

    private CardView mosNews;
    private boolean mosNewsVisible;

    public NewsFragmentVM(NewsFragment fragment, FragmentNewsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentNewsBinding binding) {
        adapter = new NewsAdapter();
        recyclerView = binding.list;
        mosNews = (CardView) binding.mosNews;
        super.initialize(binding);
        /**
         * клик по плашке с новостями правительства Москвы
         */
        mosNews.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mos.ru/news/"));
            if (getFragment() != null && getFragment().getContext() != null) {
                getFragment().getContext().startActivity(intent);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (firstVisiblePosition == 0 && !mosNewsVisible) {
                    UIhelper.hideWithFadeView(false, mosNews, mosNews);
                    mosNewsVisible = true;
                }
                if (mosNewsVisible && firstVisiblePosition > 1) {
                    UIhelper.hideWithFadeView(true, mosNews, mosNews);
                    mosNewsVisible = false;
                }
            }
        });
    }

    @Override
    public void doRequest() {
        if (page.getNum() == 1) {
            progressable = getComponent(ProgressableUIComponent.class);
        } else {
            progressable = new Progressable() {
                @Override
                public void begin() {
                    getBinding().swipe.setRefreshing(true);
                }

                @Override
                public void end() {
                    getBinding().swipe.setRefreshing(false);
                }
            };
        }
        HandlerApiResponseSubscriber<List<News>> handler
                = new HandlerApiResponseSubscriber<List<News>>(getFragment().getContext(), progressable) {
            @Override
            protected void onResult(List<News> result) {
                if (result.size() > 0) {
                    List<News> normalNews = new ArrayList<>();
                    /**
                     * отбираем новости с типом "table_link"
                     */
                    for (News news : result) {
                        if ("table_link".equalsIgnoreCase(news.getType())) {
                            normalNews.add(news);
                        }
                    }
                    adapter.add(normalNews);
                    getComponent(RecyclerUIComponent.class).refreshUI();
                } else {
                    isPaginationEnable = false;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        };
        disposables.add(AGApplication
                .api
                .getNews(new NewsGet.Request(page))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    @Override
    public void resetData() {
        isPaginationEnable = true;
        getBinding().swipe.setRefreshing(false);
        super.resetData();
    }
}
