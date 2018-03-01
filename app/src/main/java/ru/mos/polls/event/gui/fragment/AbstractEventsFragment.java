package ru.mos.polls.event.gui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.common.controller.LocationController;
import ru.mos.polls.common.controller.ScrollableController;
import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.event.adapter.EventAdapter;
import ru.mos.polls.event.controller.EventApiControllerRX;
import ru.mos.polls.event.model.EventFromList;
import ru.mos.polls.event.model.Filter;
import ru.mos.polls.fragments.PullableFragment;
import ru.mos.polls.helpers.ActionBarHelper;
import ru.mos.polls.mypoints.model.Status;


public abstract class AbstractEventsFragment extends PullableFragment {
    /**
     * Общие view для всех типов экранов со списком опросов
     */
    protected ListView listView;
    protected TextView empty;

    private boolean isLastListEmpty;
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
            if (isPagingEnable()) {
                refreshEvents(true, true);
            }
        }
    });

    /**
     * Адаптер для отображения списка,
     * нужно задавать в абстрактном методе
     * для конкретного списка мероприятий
     */
    protected ArrayAdapter adapter;
    /**
     * Список мкроприятий
     */
    protected List<EventFromList> events;
    /**
     * Используется для пейджинга в запросах, то есть
     * хранит текущую страницу и количество элментов на странице
     */
    protected PageInfo pageInfo;
    protected Status userStatus;
    protected Position currentPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * для списка текущих мероприятий;
         * после чекина, при возврате на экран списка мероприятий
         * нам потребутеся обновить экран
         */
        if (this instanceof CurrentEventsFragment) {
            events.clear();
            pageInfo.clear();
            refreshEvents(true, false);
        }
    }

    protected void init() {
        ActionBarHelper.setNavigationModeList((BaseActivity) getActivity());
        pageInfo = new PageInfo();
        events = new ArrayList<EventFromList>();
        adapter = getAdapter();
        getLocationController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_pullable_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        /**
         * для списка посещенных и прошедших мерпоприятий
         */
        if (!(this instanceof CurrentEventsFragment)) {
            refreshEvents(true, false);
        }
    }

    /**
     * Инициализация общих контроллов для всех списков,
     * не забывать в переопределенном методе вначале
     * метода писать super.findViews(view)
     *
     * @param view
     */
    protected void findViews(View view) {
        TextView empty = ButterKnife.findById(view, R.id.empty);
        empty.setText(getEmptyText());
        listView = ButterKnife.findById(view, android.R.id.list);
        listView.setEmptyView(empty);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(scrollableController);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActionBarHelper.setNavigationModeStandard((BaseActivity) getActivity());
    }

    private void getLocationController() {
        LocationController locationController = LocationController.getInstance(getActivity());
        locationController.setOnPositionListener(new LocationController.OnPositionListener() {
            @Override
            public void onGet(Position position) {
                currentPosition = position;
            }
        });
        currentPosition = locationController.getCurrentPosition();
        locationController.connect(getActivity());
        /**
         * Проверяем включена ли функция определения местоположения,
         * если нет, то показываем диалог с прдложением включить
         */
        if (!locationController.isLocationGPSProviderEnabled(getContext()) && !locationController.isLocationNetworkProviderEnabled(getContext())) {
            locationController.showDialogEnableLocationProvider(getContext(), null);
        }
    }

//
//    @Override
//    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(final Response.Listener<Object> responseListener, final Response.ErrorListener errorListener) {
//        return new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                /**
//                 * при pull-to-refresh удаляем все мероприятия из списка
//                 * обновляем pageInfo
//                 */
//                pageInfo.clear();
//                events.clear();
//                refreshEvents();
//            }
//        };
//    }

    protected void refreshEvents() {
        refreshEvents(false, false);
    }

    protected void refreshLocalParams(List<EventFromList> events, PageInfo pageInfo, Status userStatus) {
        this.events.addAll(events);
        this.pageInfo = pageInfo;
        this.userStatus = userStatus;
        /**
         * если список не подгрузился, то не увеличиваем pageNumber
         */
        if (events != null && events.size() > 0) {
            isLastListEmpty = false;
        }
    }

    protected void onPrepareLoadEvents(boolean isNeedProgress) {
        /**
         * Для искючения срабатывания несколько раз метода обратного вызова
         * при достижении конца списка голосований отключаем контроллер
         */
        scrollableController.setAllowed(false);
    }

    protected void refreshEvents(final boolean isNeedProgress, final boolean isNeedScrollToNewItems) {
        isLastListEmpty = true;
        onPrepareLoadEvents(isNeedProgress);
        EventApiControllerRX.EventsListener listener = new EventApiControllerRX.EventsListener() {
            @Override
            public void onLoad(List<EventFromList> events, Filter filter, Status userStatus, PageInfo pageInfo) {
                refreshLocalParams(events, pageInfo, userStatus);
                adapter.notifyDataSetChanged();
                scrollableController.setAllowed(true);
                hideProgress();
            }

            @Override
            public void onError() {
                scrollableController.setAllowed(true);
                hideProgress();
            }

            private void hideProgress() {
                getPullToRefreshLayout().setRefreshing(false);
                try {
                    ((BaseActivity) getActivity())
                            .setSupportProgressBarIndeterminateVisibility(false);
                    if (isNeedScrollToNewItems) {
                        scrollableController.showNewItems();
                    }
                } catch (Exception ignored) {
                }
            }
        };
        EventApiControllerRX.loadEvents(disposables, currentPosition, getFilter(), pageInfo, listener);
    }

    /**
     * Адаптер для отображения списка меропрятий
     *
     * @return
     */
    protected ArrayAdapter getAdapter() {
        EventAdapter result = new EventAdapter(getActivity(), events);
        result.setCurrentPosition(currentPosition);
        result.setFilter(getFilter());
        return result;
    }

    /**
     * Тип списка мероприятий
     *
     * @return фильтр, исопльзуемый в запросе получения списка мероприятий (CURRENT, PAST, VISITED)
     */
    abstract Filter getFilter();

    /**
     * Нужен ли пейджинг по достижении конца списка мероприятий
     *
     * @return true - будет подгрузка списка
     */
    abstract boolean isPagingEnable();

    public String getEmptyText() {
        return getString(R.string.empty_list);
    }
}
