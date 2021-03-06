package ru.mos.polls.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;
import com.appsflyer.AppsFlyerLib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.adapters.DynamicsAdapter;
import ru.mos.elk.netframework.model.results.ResultTableLink;
import ru.mos.elk.netframework.request.JsonArrayRequest;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.common.controller.ExtendScrollableController;
import ru.mos.polls.common.controller.ScrollableController;
import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.helpers.AppsFlyerConstants;


public class NewsDynamicFragment extends AgDynamicFragment {
    public static final int FIRST_PAGE_NUMBER_FOR_NEWS = 2;
    public static final int COUNT_PER_PAGE_FOR_NEWS = 20;

    public static DynamicFragment newInstance(String defTitle, String params, String baseUrl) {
        DynamicFragment fragment = new NewsDynamicFragment();
        Bundle args = new Bundle();
        args.putString(DEF_TITLE, defTitle);
        args.putString(PARAMS, params);
        args.putString(BASE_URL, baseUrl);

        fragment.setArguments(args);
        return fragment;
    }

    private PageInfo pagination = new PageInfo(COUNT_PER_PAGE_FOR_NEWS, FIRST_PAGE_NUMBER_FOR_NEWS);
    private Timer timer;
    private AbsListView list;
    private ExtendScrollableController extendScrollableController;
    @BindView(R.id.mosNews)
    CardView mosNews;
    boolean mosNewsVisible;
    ViewGroup root;

    @Override
    protected void onBeforeActivityStart(ResultTableLink element) {
        super.onBeforeActivityStart(element);
        AppsFlyerLib.sendTrackingWithEvent(getActivity(), AppsFlyerConstants.NEWS_OPENED, element.getLinkId());
    }

    @Override
    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(Response.Listener<Object> responseListener, Response.ErrorListener errorListener) {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        };
    }

    @Override
    public String getEmptyViewText() {
        return getString(R.string.no_news);
    }

    @Override
    public void refresh() {
        extendScrollableController.setAllowed(true);
        pagination.setPageNumber(2);
        getManager().invalidateAll();
        getManager().start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.news_fragment, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.mosNews)
    public void openNewsSite() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mos.ru/news/"));
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = ButterKnife.findById(getView(), android.R.id.list);
        root = ButterKnife.findById(getView(), R.id.root);
        ViewCompat.setNestedScrollingEnabled(list, true);
        ScrollableController.OnLastItemVisibleListener onLastItemVisibleListener = new ScrollableController.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                load();
            }
        };
        ExtendScrollableController.ScrollListener scrollListener = new ExtendScrollableController.ScrollListener() {
            @Override
            public void onScrolled(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    UIhelper.hideWithFadeView(false, mosNews, mosNews);
                    mosNewsVisible = true;
                }
                if (mosNewsVisible && firstVisibleItem > 2) {
                    UIhelper.hideWithFadeView(true, mosNews, mosNews);
                    mosNewsVisible = false;
                }

            }
        };
        extendScrollableController
                = new ExtendScrollableController(onLastItemVisibleListener, scrollListener);
        list.setOnScrollListener(extendScrollableController);
        /**
         * Помечаем все новости как прочитанные, такая логика используется с версии 1.9.6<br/>
         */
        BadgeManager.uploadAllNewsAsReaded((BaseActivity) getActivity());
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
    }

    /**
     * Использовать при пагниции списка новостей {@see <a href=""></a>}
     */
    private void load() {
        getPullToRefreshLayout().setRefreshing(true);
        extendScrollableController.setAllowed(false);
        String url = API.getURL(UrlManager.url(UrlManager.V250, UrlManager.Controller.NEWS, UrlManager.Methods.GET));
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("count_per_page", pagination.getCountPerPage());
            requestJson.put("page_number", pagination.getPageNumber());
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                getPullToRefreshLayout().setRefreshing(false);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject json = jsonArray.optJSONObject(i);
                        String type = json.optString("type");
                        if ("table_link".equalsIgnoreCase(type)) {
                            String style = json.optString("style");
                            ResultTableLink resultTableLink = new ResultTableLink(style);
                            try {
                                resultTableLink.fill(json);
                                ((DynamicsAdapter) list.getAdapter()).add(resultTableLink);
                            } catch (JSONException ignored) {
                            }
                        }
                    }
                    ((DynamicsAdapter) list.getAdapter()).notifyDataSetChanged();
                    if (jsonArray.length() >= COUNT_PER_PAGE_FOR_NEWS) {
                        pagination.incrementPageNumber();
                        extendScrollableController.setAllowed(true);
                    }
                    extendScrollableController.showNewItems();
                }
            }
        };
        Response.ErrorListener errorListener =  new StandartErrorListener(getActivity(), R.string.error_occurs)  {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                getPullToRefreshLayout().setRefreshing(false);
                extendScrollableController.setAllowed(true);
            }
        };
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url, requestJson, responseListener, errorListener);
        ((BaseActivity) getActivity()).addRequest(jsonObjectRequest);
    }

}
