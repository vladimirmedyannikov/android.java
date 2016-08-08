package ru.mos.polls.fragments;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.db.PollsData.TPointHistory;


public class MyPointsAdapter extends ResourceCursorAdapter {

    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy в HH:mm");
    private final int greenColor;
    private final int redColor;

    private int titleInd = -1;
    private int writeOffDateInd;
    private int pointsInd;
    private int action;

    public MyPointsAdapter(Context context, Cursor c) {
        super(context, R.layout.item_point_history, c, false);
        greenColor = context.getResources().getColor(R.color.greenText);
        redColor = context.getResources().getColor(R.color.ag_red);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = super.newView(context, cursor, parent);
        PollsHolder h = new PollsHolder(v);
        v.setTag(h);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        PollsHolder h = (PollsHolder) view.getTag();
        h.tvTitle.setText(cursor.getString(titleInd));
        h.tvWriteOffDate.setText(df.format(new Date(cursor.getLong(writeOffDateInd))));
        int points = cursor.getInt(pointsInd);
        h.tvPointsText.setText(PointsManager.getPointUnitString(context, points));
        String type = cursor.getString(action);
        boolean isRefilled = "debit".equalsIgnoreCase(type);
        if (isRefilled) {
            h.tvPoints.setText("+" + points);
            h.tvPoints.setTextColor(greenColor);
            h.tvPointsText.setTextColor(greenColor);
        } else {
            h.tvPoints.setText("-" + points);
            h.tvPoints.setTextColor(redColor);
            h.tvPointsText.setTextColor(redColor);
        }
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        initIndexes(newCursor);
        return super.swapCursor(newCursor);
    }

    private void initIndexes(Cursor c) {
        if (c == null || titleInd != -1) return;
        titleInd = c.getColumnIndex(TPointHistory.TITLE);
        writeOffDateInd = c.getColumnIndex(TPointHistory.WRITE_OFF_DATE);
        pointsInd = c.getColumnIndex(TPointHistory.POINTS);
        action = c.getColumnIndex(TPointHistory.ACTION);
    }

    static class PollsHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvWriteOffDate)
        TextView tvWriteOffDate;
        @BindView(R.id.tvPoints)
        TextView tvPoints;
        @BindView(R.id.tvPointsText)
        TextView tvPointsText;

        PollsHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
