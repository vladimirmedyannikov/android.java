package ru.mos.polls.innovation.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.common.controller.ScrollableController;
import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.common.model.UserStatus;
import ru.mos.polls.fragments.PullableFragment;
import ru.mos.polls.helpers.ListViewHelper;
import ru.mos.polls.innovation.controller.InnovationApiController;
import ru.mos.polls.innovation.gui.activity.InnovationActivity;
import ru.mos.polls.innovation.gui.adapter.InnovationAdapter;
import ru.mos.polls.innovation.model.Innovation;
import ru.mos.polls.innovation.model.ShortInnovation;
import ru.mos.polls.innovation.model.Status;


public abstract class AbstractInnovationsFragment extends PullableFragment {
    /**
     * Общие view для всех типов экранов со списком опросов
     */
    protected ListView listView;
    protected TextView empty;

    private boolean isLastListEmpty;
    private boolean hasAnymoreForLoading;

    protected ScrollableController scrollableController = new ScrollableController(new ScrollableController.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
            if (pageInfo == null) {
                pageInfo = new PageInfo();
            } else {
                if (!isLastListEmpty) {
                    pageInfo.incrementPageNumber();
                }
            }
            if (hasAnymoreForLoading && isPagingEnable()) {
                refreshShortInnovation(true);
            }
        }
    });

    protected ArrayAdapter adapter;
    protected List<ShortInnovation> shortInnovations;

    protected PageInfo pageInfo;
    protected UserStatus userStatus;
    protected Position currentPosition;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Innovation markedInnovation = InnovationActivity.onResult(requestCode, resultCode, data);
        if (markedInnovation != null && markedInnovation.isPassed()) {
            for (ShortInnovation shortInnovation : shortInnovations) {
                if (shortInnovation.getId() == markedInnovation.getId()) {
                    shortInnovation.setStatus(Status.PASSED);
                    shortInnovation.setPassedDate(markedInnovation.getPassedDate());
                    shortInnovation.setFullRating(markedInnovation.getRating().getFullRating());
                    ListViewHelper.saveScrollableState("innovations", listView);
                    adapter.notifyDataSetChanged();
                    ListViewHelper.restoreScrollableState("innovations", listView);
                    break;
                }
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_pullable_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        refreshShortInnovation(false);
    }

    protected void findViews(View view) {
        TextView empty = (TextView) view.findViewById(R.id.empty);
        empty.setText(getEmptyText());
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setEmptyView(empty);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(scrollableController);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InnovationActivity.startActivity(AbstractInnovationsFragment.this, shortInnovations.get(position));
            }
        });
    }

    private void init() {
        pageInfo = new PageInfo();
        hasAnymoreForLoading = true;
        shortInnovations = new ArrayList<>();
        adapter = new InnovationAdapter(getActivity(), shortInnovations);
    }

    protected boolean isPagingEnable() {
        return false;
    }

    protected Status getStatus() {
        return Status.ACTIVE;
    }

    protected String getEmptyText() {
        return getString(R.string.empty_list);
    }

    protected void onPrepareLoadEvents() {
        setRefreshing();
        scrollableController.setAllowed(false);
    }

    protected void refreshShortInnovation(final boolean isNeedScrollToNewItems) {
        isLastListEmpty = true;
        onPrepareLoadEvents();
        InnovationApiController.ShortInnovationListener listener = new InnovationApiController.ShortInnovationListener() {
            @Override
            public void onLoaded(List<ShortInnovation> loadedShortInnovations, UserStatus userStatus, PageInfo pageInfo) {
                hasAnymoreForLoading = loadedShortInnovations != null
                        && loadedShortInnovations.size() >= pageInfo.getCountPerPage();
                refreshParams(loadedShortInnovations, userStatus, pageInfo);
                adapter.notifyDataSetChanged();
                scrollableController.setAllowed(true);
                hideProgress();
            }

            @Override
            public void onError(VolleyError volleyError) {
                scrollableController.setAllowed(true);
                hideProgress();
            }

            private void hideProgress() {
                getPullToRefreshLayout().setRefreshing(false);
                try {
                    if (isNeedScrollToNewItems) {
                        scrollableController.showNewItems();
                    }
                } catch (Exception ignored) {
                }
            }
        };
        InnovationApiController.select((BaseActivity) getActivity(), getStatus(), pageInfo, listener);
    }

    private void refreshParams(List<ShortInnovation> shortInnovations, UserStatus userStatus, PageInfo pageInfo) {
        this.shortInnovations.addAll(shortInnovations);
        this.pageInfo = pageInfo;
        this.userStatus = userStatus;
        /**
         * если список не подгрузился, то не увеличиваем pageNumber
         */
        if (shortInnovations != null && shortInnovations.size() > 0) {
            isLastListEmpty = false;
        }
    }

    @Override
    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(Response.Listener<Object> responseListener, Response.ErrorListener errorListener) {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hasAnymoreForLoading = true;
                pageInfo.clear();
                shortInnovations.clear();
                adapter.notifyDataSetChanged();
                refreshShortInnovation(false);
            }
        };
    }
}
