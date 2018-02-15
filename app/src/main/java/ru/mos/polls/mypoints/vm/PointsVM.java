package ru.mos.polls.mypoints.vm;

import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemPointHistoryBinding;
import ru.mos.polls.mypoints.model.Points;

/**
 * Created by Trunks on 24.08.2017.
 */

public class PointsVM extends RecyclerBaseViewModel<Points, ItemPointHistoryBinding> {
    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy в HH:mm");
    TextView tvPoints;
    TextView tvPointsText;
    TextView tvTitle;
    TextView tvWriteOffDate;

    public PointsVM(Points model) {
        super(model);
    }

    public PointsVM(Points model, ItemPointHistoryBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_point_history;
    }

    @Override
    public void onBind(ItemPointHistoryBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        tvPoints = viewDataBinding.tvPoints;
        tvPointsText = viewDataBinding.tvPointsText;
        tvTitle = viewDataBinding.tvTitle;
        tvWriteOffDate = viewDataBinding.tvWriteOffDate;
        viewDataBinding.setViewModel(this);
        setView();
    }

    @Override
    public int getViewType() {
        return 0;
    }

    public void setView() {
        tvWriteOffDate.setText(df.format(new Date(model.getDate() * 1000)));
        tvTitle.setText(model.getTitle());
        int color = isRefilled() ? tvPoints.getResources().getColor(R.color.greenText) : tvPoints.getResources().getColor(R.color.ag_red);
        tvPointsText.setTextColor(color);
        tvPoints.setTextColor(color);
        if (isRefilled()) {
            tvPoints.setText("+" + model.getPoints());
        } else {
            tvPoints.setText("-" + model.getPoints());
        }
    }

    public boolean isRefilled() {
        return "debit".equalsIgnoreCase(model.getAction());
    }

    public int getPoints() {
        return model.getPoints();
    }
}
