package ru.mos.polls.poll.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.poll.gui.AbstractPollsFragment;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.model.Poll;

/**
 * Адаптер для отображения списка активных голосований
 */
public class ActivePollAdapter extends ArrayAdapter<Poll> {

    private AbstractPollsFragment.PollSelectedListener listener;

    public ActivePollAdapter(Context context, List<Poll> objects, AbstractPollsFragment.PollSelectedListener listener) {
        super(context, 0, objects);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(getContext(), R.layout.item_active_poll, null);
        Poll poll = null;
        try {
            poll = getItem(position);
        } catch (Exception ignored) {
        }
        if (convertView != null && poll != null) {
            displayTitle(convertView, poll);
            displayDescription(convertView, poll);
            displayInterruptedMark(convertView, poll);
            setCallbackListener(convertView, poll);
        }
        return convertView;
    }

    private void displayTitle(View v, Poll poll) {
        TextView params = ButterKnife.findById(v, R.id.title);
        params.setText(poll.getTitle());
        if (poll.isActive()) {
            int green = getContext().getResources().getColor(R.color.greenText);
            params.setTextColor(green);
        }
    }

    private void displayDescription(View v, Poll poll) {
        TextView params = ButterKnife.findById(v, R.id.description);
        if (poll.getPoints() > 0) {
            String result = PointsManager.getSuitableString(getContext(), R.array.survey_points_pluse, poll.getPoints());
            result = String.format(result, poll.getPoints());
            if (Kind.isHearing(poll.getKind())) {
                result += ", " + getContext().getString(R.string.title_hearing_survey_summary).toLowerCase();
            } else if (Kind.isSpecial(poll.getKind())) {
                result += ", " + getContext().getString(R.string.special_hearing).toLowerCase();
            }
            params.setText(result);
        } else {
            String result = "";
            if (Kind.isHearing(poll.getKind())) {
                result += getContext().getString(R.string.title_hearing_survey_summary);
            } else if (Kind.isSpecial(poll.getKind())) {
                result += getContext().getString(R.string.special_hearing);
            } else {
                params.setVisibility(View.GONE);
            }
            params.setText(result);
        }
    }

    private void displayInterruptedMark(View v, Poll poll) {
        TextView params = ButterKnife.findById(v, R.id.interrupted);
        int visibility = View.GONE;
        if (poll.isInterrupted()) {
            visibility = View.VISIBLE;
        }
        params.setVisibility(visibility);
    }

    private void setCallbackListener(View v, final Poll poll) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelected(poll);
                }
            }
        });
    }
}
