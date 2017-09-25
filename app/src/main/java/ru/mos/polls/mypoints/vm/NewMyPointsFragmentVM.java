package ru.mos.polls.mypoints.vm;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
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
import ru.mos.polls.base.vm.PullableFragmentVM;
import ru.mos.polls.databinding.FragmentNewMyPointsBinding;
import ru.mos.polls.model.PointHistory;
import ru.mos.polls.mypoints.model.Points;
import ru.mos.polls.mypoints.model.Status;
import ru.mos.polls.mypoints.service.HistoryGet;
import ru.mos.polls.mypoints.ui.NewMyPointsAdapter;
import ru.mos.polls.mypoints.ui.NewMyPointsFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.StubUtils;

/**
 * Created by Trunks on 23.08.2017.
 */

public class NewMyPointsFragmentVM extends PullableFragmentVM<NewMyPointsFragment, FragmentNewMyPointsBinding, NewMyPointsAdapter> {
    TextView tvCurrentPointsUnit;
    TextView empty;
    TextView tvPoints;
    TextView tvStatus;
    TextView tvTitleBalance;
    Status status;
    List<String> action;
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
        recyclerView = binding.list;
        tvPoints = binding.tvPoints;
        tvStatus = binding.tvStatus;
        tvTitleBalance = binding.tvTitleBalance;
        adapter = new NewMyPointsAdapter();
        action = new ArrayList<>();
        currentAction = PointHistory.Action.ALL;
        setListeners();
        super.initialize(binding);
    }

    @Override
    public void onResume() {
        super.onResume();
        setView();
    }

    @Override
    public void doRequest() {
        HandlerApiResponseSubscriber<HistoryGet.Response.Result> handler =
                new HandlerApiResponseSubscriber<HistoryGet.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(HistoryGet.Response.Result result) {
                        if (result.getPoints().size() > 0) {
                            adapter.add(result.getPoints());
                            empty.setVisibility(View.INVISIBLE);
                            progressPull();
                            status = result.getStatus();
                            setView();
                        } else {
                            empty.setVisibility(View.VISIBLE);
                        }
                    }
                };
        HistoryGet.Request request = new HistoryGet.Request(page);
        request.setAction(action);
        Observable<HistoryGet.Response> responseObservable = AGApplication.api
                .getHistory(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
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
                action = new ArrayList<>();
                switch (menuItem.getItemId()) {
                    case R.id.action_debit:
                        currentAction = PointHistory.Action.DEBIT;
                        action.add(PointHistory.Action.DEBIT.toString());
                        result = true;
                        break;
                    case R.id.action_credit:
                        currentAction = PointHistory.Action.CREDIT;
                        action.add(PointHistory.Action.CREDIT.toString());
                        result = true;
                        break;
                    case R.id.action_all:
                        currentAction = PointHistory.Action.ALL;
                        action = null;
                        result = true;
                        break;
                }
                clearList();
                page.reset();
                doRequest();
                return result;
            }
        });
        popup.show();
    }

    private void setView() {
        if (status != null) {
            processStatus(status.getState());
            processTitleBalance();
            processPoints();
            processPointUnits();
        }
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
        tvCurrentPointsUnit.setText(PointsManager.getPointUnitString(getActivity(), getPointsValue()));
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
