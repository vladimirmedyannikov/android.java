package ru.mos.polls.poll.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.common.controller.ScrollableController;
import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.common.model.UserStatus;
import ru.mos.polls.fragments.PullableFragment;
import ru.mos.polls.helpers.ActionBarHelper;
import ru.mos.polls.helpers.FragmentHelper;
import ru.mos.polls.poll.controller.PollApiController;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.survey.SurveyActivity;


public abstract class AbstractPollsFragment extends PullableFragment {
    /**
     * Общие view для всех типов экранов со списком опросов
     */
    protected AbsListView listView;
    protected View empty;
    protected Unbinder unbinder;
    private boolean isLastListEmpty;
    private boolean isRefreshPollsNeed = true;
    /**
     * Callback для обработки достижения конца списка
     */
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
            if (isPagingEnable() && isRefreshPollsNeed) {
                refreshPolls(true);
            }
        }
    });

    /**
     * Callback для обработки клика на опрос в списке
     */
    protected PollSelectedListener listener;

    /**
     * Адаптер для отображения списка,
     * нужно задавать в абстрактном методе
     * для конкретного списка опросов
     */
    protected ArrayAdapter adapter;
    /**
     * Список опросов
     */
    protected List<Poll> polls;
    /**
     * Используется для пейджинга в запросах, то есть
     * хранит текущую страницу и количество элментов на странице
     */
    protected PageInfo pageInfo;
    /**
     * Пока не используется, тут информация по баллам и статусу пользователя
     */
    protected UserStatus userStatus;

    /**
     * callback для обработки полученных в ходе запроса голосований
     */
    protected PollApiController.PollGroupListener pollGroupListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected void init() {
        pageInfo = new PageInfo();
        polls = new ArrayList<Poll>();
        listener = new PollSelectedListener() {
            @Override
            public void onSelected(Poll poll) {
                Fragment sender = FragmentHelper.getParentFragment(AbstractPollsFragment.this);
                if (sender != null) {
                    SurveyActivity.startActivityForResult(sender, poll.getId(), poll.getKind().isHearing());
                }
            }
        };
        adapter = getAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_pullable_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        refreshPolls(false);
    }

    /**
     * Инициализация общих контроллов для всех списков,
     * не забывать в переопределенном методе вначале
     * метода писать super.findViews(view)
     *
     * @param view
     */
    protected void findViews(View view) {
        empty = ButterKnife.findById(view, R.id.empty);
//                view.findViewById(R.id.empty);
        if (empty instanceof TextView) {
            ((TextView) empty).setText(getEmptyText());
        }
        listView = ButterKnife.findById(view, android.R.id.list);
//                (AbsListView) view.findViewById(android.R.id.list);
        listView.setEmptyView(empty);
        ((AdapterView<ListAdapter>) listView).setAdapter(adapter);
        listView.setOnScrollListener(scrollableController);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActionBarHelper.setNavigationModeStandard((BaseActivity) getActivity());
        if(unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(final Response.Listener<Object> responseListener, final Response.ErrorListener errorListener) {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /**
                 * при pull-to-refresh удаляем все сохранненные опросы
                 * обновляем pageInfo
                 */
                pageInfo.clear();
                polls.clear();
                isRefreshPollsNeed = true;
                refreshPolls();
            }
        };
    }

    protected void refreshPolls() {
        refreshPolls(false);
    }

    protected void refreshLocalParams(List<Poll> newPolls, UserStatus userStatus, PageInfo pageInfo) {
        for (Poll newPoll : newPolls) {
            boolean yetAdded = false;
            for (Poll oldPoll : this.polls) {
                if (oldPoll.getId() == newPoll.getId()) {
                    yetAdded = true;
                    break;
                }
            }
            if (!yetAdded) {
                polls.add(newPoll);
            }
        }
        this.userStatus = userStatus;
        if (newPolls.size() > 0) {
            isLastListEmpty = false;
        }
        this.pageInfo = pageInfo;
    }

    protected void onPrepareLoadPolls() {
        setRefreshing();
        scrollableController.setAllowed(false);
    }

    protected void refreshPolls(final boolean isNeedScrollToNewItems) {
        isLastListEmpty = true;
        onPrepareLoadPolls();
        pollGroupListener = new PollApiController.PollGroupListener() {
            @Override
            public void onLoad(List<Poll> polls, UserStatus userStatus, PageInfo pageInfo) {
                isRefreshPollsNeed = polls.size() == PageInfo.DEFAULT_COUNT_PER_PAGE;
                refreshLocalParams(polls, userStatus, pageInfo);
                adapter.notifyDataSetChanged();
                scrollableController.setAllowed(true);
                hideProgress();

                /**
                 * если опдгрузили новые данные и добалиил их вниз списка,
                 * скролим список немного вниз, чтобы пользоватлеь, понял,
                 * что етсь новые данные
                 */
                if (isNeedScrollToNewItems && polls.size() > 0) {
                    scrollableController.showNewItems();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                scrollableController.setAllowed(true);
                hideProgress();
            }

            private void hideProgress() {
                getPullToRefreshLayout().setRefreshing(false);
            }
        };
        PollApiController
                .loadPolls((BaseActivity) getActivity(), pageInfo, getFilter(), getKinds(), pollGroupListener);
    }

    private List<Poll> preparePolls(List<Poll> polls) {
        List<Poll> result = new ArrayList<Poll>();
        for (Poll poll : polls) {
            if (!poll.getKind().isHearing() && !poll.getKind().isHearingPreview()) {
                result.add(poll);
            }
        }
        return result;
    }

    public String getEmptyText() {
        return getString(R.string.empty_list);
    }

    protected List<Kind> getKinds() {
        List<Kind> result = new ArrayList<Kind>();
        result.add(Kind.STANDART);
        result.add(Kind.SPECIAL);
        result.add(Kind.HEARING);
        return result;
    }

    /**
     * Определить адаптер для отображения списка опросов
     *
     * @return объект адаптера
     */
    abstract ArrayAdapter getAdapter();

    /**
     * Определить тип фильтра, исопльзуемого при запросе списка голосований
     *
     * @return объект типа фильтра, возможные значения AVAILABLE, PASSED, OLD
     */
    abstract PollApiController.Filter[] getFilter();

    protected boolean isPagingEnable() {
        return true;
    }

    public interface PollSelectedListener {
        public void onSelected(Poll poll);
    }
}
