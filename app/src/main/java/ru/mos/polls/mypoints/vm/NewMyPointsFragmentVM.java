package ru.mos.polls.mypoints.vm;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.ui.RecyclerScrollableController;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.databinding.FragmentNewMyPointsBinding;
import ru.mos.polls.model.PointHistory;
import ru.mos.polls.mypoints.model.Points;
import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.mypoints.service.HistoryGet;
import ru.mos.polls.mypoints.ui.NewMyPointsAdapter;
import ru.mos.polls.mypoints.ui.NewMyPointsFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.util.StubUtils;

/**
 * Created by Trunks on 23.08.2017.
 */

public class NewMyPointsFragmentVM extends UIComponentFragmentViewModel<NewMyPointsFragment, FragmentNewMyPointsBinding> {
    TextView tvCurrentPointsUnit;
    RecyclerView list;
    NewMyPointsAdapter adapter;
    TextView empty;
    TextView tvPoints;
    TextView tvStatus;
    TextView tvTitleBalance;
    Status status;
    private Page historyPage;
    boolean isPaginationEnable;
    List<Points> filteredList;
    /**
     * Хранит текущий тип списка баллов
     */
    protected PointHistory.Action currentAction;

    public NewMyPointsFragmentVM(NewMyPointsFragment fragment, FragmentNewMyPointsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentNewMyPointsBinding binding) {
        tvCurrentPointsUnit = binding.tvCurrentPointsUnit;
        empty = binding.empty;
        list = binding.list;
        tvPoints = binding.tvPoints;
        tvStatus = binding.tvStatus;
        tvTitleBalance = binding.tvTitleBalance;
        UIhelper.setRecyclerList(list, getActivity());
        adapter = new NewMyPointsAdapter();
        list.setAdapter(adapter);
        list.addOnScrollListener(getScrollableListener());
        historyPage = new Page();
        isPaginationEnable = true;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        progressable = getProgressable();
        currentAction = PointHistory.Action.ALL;
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        setView();
        requestHistory();
    }

    public void setListeners() {
        tvCurrentPointsUnit.setOnClickListener(v -> showPopup(v));
    }

    /**
     * Контекстный диалог выбора списка баллов
     *
     * @param v - view, клик по которому вызывает данный диалог
     */
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.points_popup, popup.getMenu());
        /**
         * Задание кастомных view для пунктов меню, пока не работает
         */
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                boolean result = false;
                switch (menuItem.getItemId()) {
                    case R.id.action_debit:
                        currentAction = PointHistory.Action.DEBIT;
                        result = true;
                        break;
                    case R.id.action_credit:
                        currentAction = PointHistory.Action.CREDIT;
                        result = true;
                        break;
                    case R.id.action_all:
                        currentAction = PointHistory.Action.ALL;
                        result = true;
                        break;
                }
                adapter.clear();
                adapter.notifyDataSetChanged();
                requestHistory();
                return result;
            }
        });
        popup.show();
    }

    public void requestHistory() {
        HandlerApiResponseSubscriber<HistoryGet.Response.Result> handler =
                new HandlerApiResponseSubscriber<HistoryGet.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(HistoryGet.Response.Result result) {
                        if (!currentAction.toString().equalsIgnoreCase(PointHistory.Action.ALL.toString())) {
                            getFilteredList(result.getPoints());
                        } else {
                            adapter.add(result.getPoints());
                            adapter.notifyDataSetChanged();
                        }
                        empty.setVisibility(View.INVISIBLE);
                        progressable.end();
                        isPaginationEnable = true;
                        status = result.getStatus();
                        setView();
                    }
                };
        Observable<HistoryGet.Response> responseObservable = AGApplication.api
                .getHistory(new HistoryGet.Request(historyPage))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }


    public void getFilteredList(List<Points> list) {
        filteredList = new ArrayList<>();
        Observable.just(list)
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::fromIterable)
                .filter(points -> points.getAction().equalsIgnoreCase(currentAction.toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(points -> filteredList.add(points), throwable -> {
                }, this::addToAdapterList);
    }

    public void addToAdapterList() {
        adapter.add(filteredList);
        adapter.notifyDataSetChanged();
    }

    private void setView() {
        if (status != null) {
            processStatus(status.getState());
            processTitleBalance();
            processPoints();
            processPointUnits();
        }
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new PullableUIComponent(() -> {
                    progressable = getPullableProgressable();
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    requestHistory();
                }))
                .with(new ProgressableUIComponent())
                .build();
    }

    /**
     * Вызывается на экране MyPointsFragment
     */
    protected void processPoints() {
        tvPoints.setText(String.valueOf(getPointsValue()));
    }

    /**
     * Вызывается на экранах активных и пройденнх опросов
     */
    private void processCurrentPoints() {
        tvPoints.setVisibility(View.VISIBLE);
        /**
         * action.ALL, отображаем current_points
         */
        int points = PointsManager.getPoints(getActivity(), PointHistory.Action.ALL);
        String currentPoints = getActivity().getString(R.string.current_balance)
                + " " + String.valueOf(points)
                + " " + PointsManager.getPointUnitString(getActivity(), points);
        tvPoints.setText(currentPoints);
    }

    protected void processStatus(String status) {
        if (tvStatus != null) {
            if (!TextUtils.isEmpty(status) && !"null".equalsIgnoreCase(status)) {
                status = String.format(getActivity().getString(R.string.state), status);
                tvStatus.setText(status);
                tvStatus.setVisibility(View.VISIBLE);
            } else {
                tvStatus.setVisibility(View.GONE);
            }
        }
    }

    protected void processTitleBalance() {
        if (tvTitleBalance != null) {
            String result = PointHistory.Action.getDescription(getActivity(), currentAction);
            tvTitleBalance.setText(result + ":");
        }
    }

    public void processPointUnits() {
        tvCurrentPointsUnit.setText(PointsManager.getPointUnitString(getActivity(),  getPointsValue()));
    }

    public int getPointsValue() {
        int points = status.getCurrentPoints();
        if ("debit".equalsIgnoreCase((currentAction.toString()))) {
            points = status.getCurrentPoints();
        } else if ("credit".equalsIgnoreCase(currentAction.toString())) {
            points = status.getSpentPoints();
        } else if ("blocked".equalsIgnoreCase(currentAction.toString())) {
            points = status.getFreezedPoints();
        }
        return points;
    }

    protected RecyclerView.OnScrollListener getScrollableListener() {
        RecyclerScrollableController.OnLastItemVisibleListener onLastItemVisibleListener
                = () -> {
            if (isPaginationEnable) {
                isPaginationEnable = false;
                historyPage.increment();
                requestHistory();
            }
        };
        return new RecyclerScrollableController(onLastItemVisibleListener);
    }

    public static List<Points> mockList(Context context) {
        Gson gson = new Gson();
        List<Points> content = gson.fromJson(
                StubUtils.fromRawAsJsonArray(context, R.raw.points).toString(),
                new TypeToken<List<Points>>() {
                }.getType()
        );
        return content;
    }
}
