package ru.mos.polls.event.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.event.gui.activity.EventActivity;
import ru.mos.polls.event.model.EventFromList;
import ru.mos.polls.event.model.Filter;


public class EventAdapter extends ArrayAdapter<EventFromList> {
    private Position currentPosition;
    private Filter filter;

    public EventAdapter(Context context, List<EventFromList> objects) {
        super(context, R.layout.item_current_event, objects);
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = View.inflate(getContext(), R.layout.item_current_event, null);
        EventFromList event = null;
        try {
            event = getItem(position);
        } catch (Exception ignored) {
        }
        if (view != null && event != null) {
            displayPoints(view, event);
            displayName(view, event);
            displayDate(view, event);
            displayAddress(view, event);
            displayDistance(view, event);
            setOnClickListener(view, event);
        }
        return view;
    }

    private void setOnClickListener(View view, final EventFromList event) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventActivity.startActivity(getContext(), event, filter);
            }
        });
    }

    private void displayPoints(View view, EventFromList event) {
        TextView pointsView = ButterKnife.findById(view,R.id.points);
        if (event.getPoints() > 0) {
            String s = PointsManager.getSuitableString(getContext(), R.array.survey_points_pluse, event.getPoints());
            String ss = String.format(s, event.getPoints());
            int visibility = View.VISIBLE;
            int color = pointsView.getCurrentTextColor();
            switch (filter) {
                case VISITED:
                    ss = String.format(getContext().getString(R.string.you_have_got), ss);
                    color = getContext().getResources().getColor(R.color.greyHint);
                    break;
                case PAST:
                    visibility = View.GONE;
                    break;
            }
            pointsView.setTextColor(color);
            pointsView.setText(ss);
            pointsView.setVisibility(visibility);
        } else {
            pointsView.setVisibility(View.GONE);
        }
    }

    private void displayName(View view, EventFromList event) {
        TextView name = ButterKnife.findById(view,R.id.name);
        name.setText(event.getTitle());
    }

    private void displayDate(View view, EventFromList event) {
        TextView date = ButterKnife.findById(view,R.id.date);
        SimpleDateFormat sdfOldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfNewFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm");
        String result = "";
        try {
            Date value = sdfOldFormat.parse(event.getStartDate());
            result = sdfNewFormat.format(value);
        } catch (ParseException ignored) {
        }
        date.setText(result);
    }

    private void displayAddress(View view, EventFromList event) {
        TextView address = ButterKnife.findById(view, R.id.address);
        String value = event.getPosition().getName() + "\n" + event.getPosition().getAddress();
        if (!TextUtils.isEmpty(value) && !"\n".equalsIgnoreCase(value)) {
            address.setText(value);
        } else {
            address.setVisibility(View.GONE);
        }
    }

    private void displayDistance(View view, EventFromList event) {
        TextView distance = ButterKnife.findById(view, R.id.distance);
        distance.setVisibility(View.GONE);
        if (filter == Filter.CURRENT) {
            if (event.getDistance() == EventFromList.NOT_CALCULATED_DISTANCE && currentPosition != null && !currentPosition.isEmpty()) {
                event.setDistance(currentPosition);
            }
            /**
             * Вычисляем порядок числа
             */
            int length = (int) (Math.log10(event.getDistance()) + 1);
            String result = "";
            if (length <= 3) { // метры
                result = String.format(getContext().getString(R.string.distance_m), String.valueOf(event.getDistance()));
            } else {           // км
                double km = event.getDistance() / 1000;
                DecimalFormat df = new DecimalFormat("###.##");
                result = String.format(getContext().getString(R.string.distance_km), df.format(km));
            }

            distance.setVisibility(View.VISIBLE);
            distance.setText(result);
        }
    }
}