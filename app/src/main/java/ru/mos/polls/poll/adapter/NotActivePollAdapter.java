package ru.mos.polls.poll.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.poll.gui.AbstractPollsFragment;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.model.Poll;

/**
 * Адаптер для отображения списков пройденных и моих голосований
 */
public class NotActivePollAdapter extends ArrayAdapter<Poll> {
    private AbstractPollsFragment.PollSelectedListener listener;

    public NotActivePollAdapter(Context context, List<Poll> objects, AbstractPollsFragment.PollSelectedListener listener) {
        super(context, 0, objects);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(getContext(), R.layout.item_passed_poll, null);
        Poll poll = null;
        try {
            poll = getItem(position);
        } catch (Exception ignored) {
        }
        if (convertView != null && poll != null) {
            displayTitle(convertView, poll);
            displayDescription(convertView, poll);
            setCallbackListener(convertView, poll);
        }
        return convertView;
    }

    private void displayDescription(View v, Poll poll) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        TextView params = ButterKnife.findById(v, R.id.description);
        if (poll.isPassed()) {
            StringBuilder sb = new StringBuilder();
            if (poll.getPoints() > 0) {
                sb.append(getContext().getString(R.string.credited) + " ");
                String points = PointsManager.getSuitableString(getContext(), R.array.survey_points_pluse, poll.getPoints());
                points = String.format(points, poll.getPoints());
                sb.append(points + " ");
            } else {
                sb.append(String.format(getContext().getString(R.string.title_passed_polls_with_zero_points), " "));
            }
            sb.append(sdf.format(poll.getPassedDate()));
            if (Kind.isHearing(poll.getKind())) {
                sb.append(", " + getContext().getString(R.string.title_hearing_survey_summary).toLowerCase());
            }
            params.setTextColor(getContext().getResources().getColor(R.color.greenText));
            params.setText(sb);
        } else if (poll.isOld()) {
            params.setText(String.format(getContext().getString(R.string.title_old_polls), sdf.format(poll.getEndDate())));
        }
    }

    private void displayTitle(View v, Poll poll) {
        TextView params = ButterKnife.findById(v, R.id.title);
        params.setText(poll.getTitle());
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
