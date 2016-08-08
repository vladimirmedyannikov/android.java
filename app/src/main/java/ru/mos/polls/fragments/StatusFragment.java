package ru.mos.polls.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley2.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.model.PointHistory;

/**
 * Фрагмент для отображения вверху экрана текущего количества баллов и статуса пользователя АГ
 *
 * @since 1.8
 */
public class StatusFragment extends PullableFragment {
    @BindView(R.id.tvPoints)
    protected TextView tvPoints;
    @BindView(R.id.tvStatus)
    protected TextView tvStatus;
    @BindView(R.id.tvTitleBalance)
    protected TextView tvTitleBalance;

    /**
     * Хранит текущий тип списка баллов
     */
    protected PointHistory.Action currentAction;

    public static StatusFragment newInstance() {
        StatusFragment fragment = new StatusFragment();
        return fragment;
    }

    public StatusFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_status, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentAction = PointHistory.Action.ALL;
//        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
//        tvPoints = (TextView) view.findViewById(R.id.tvPoints);
//        tvTitleBalance = (TextView) view.findViewById(R.id.tvTitleBalance);
    }

    @Override
    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(Response.Listener<Object> responseListener, Response.ErrorListener errorListener) {
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this instanceof MyPointsFragment) {
            processPoints();
        } else {
            processCurrentPoints();
        }
    }

    /**
     * Вызывается на экране MyPointsFragment
     */
    protected void processPoints() {
        tvPoints.setText(String.valueOf(PointsManager.getPoints(getActivity(), currentAction)));

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
        String currentPoints = getString(R.string.current_balance)
                + " " + String.valueOf(points)
                + " " + PointsManager.getPointUnitString(getActivity(), points);
        tvPoints.setText(currentPoints);
    }

    protected void processStatus() {
        tvStatus.setVisibility(View.VISIBLE);
        String status = PointsManager.getStatus(getActivity());
        if (!TextUtils.isEmpty(status) && !"null".equalsIgnoreCase(status)) {
            status = String.format(getString(R.string.state), status);
            tvStatus.setText(status);
        } else {
            tvStatus.setVisibility(View.GONE);
        }
    }

    protected void processTitleBalance() {
        if (tvTitleBalance != null) {
            String result = PointHistory.Action.getDescription(getContext(), currentAction);
            tvTitleBalance.setText(result + ":");
        }
    }
}
