package ru.mos.polls.newpoll.vm.item;

import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import ru.mos.polls.AGApplication;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.ItemActivePollBinding;
import ru.mos.polls.databinding.ItemPassedPollBinding;
import ru.mos.polls.newpoll.ui.adapter.PollAdapter;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.model.Poll;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollItemOldVM extends RecyclerBaseViewModel<Poll, ItemPassedPollBinding> {
    public PollItemOldVM(Poll model) {
        super(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_passed_poll;
    }

    @Override
    public int getViewType() {
        return PollAdapter.Type.ITEM_OLD;
    }

    @Override
    public void onBind(ItemPassedPollBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(model);
        viewDataBinding.notifyChange();
        displayTitle(viewDataBinding.title, model);
        displayDescription(viewDataBinding.description, model);
        setListener(viewDataBinding.baseInfoContainer, model);
    }

    public void displayDescription(TextView v, Poll poll) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (poll.isPassed()) {
            StringBuilder sb = new StringBuilder();
            if (poll.getPoints() > 0) {
                sb.append(v.getContext().getString(R.string.credited) + " ");
                String points = PointsManager.getSuitableString(v.getContext(), R.array.survey_points_pluse, poll.getPoints());
                points = String.format(points, poll.getPoints());
                sb.append(points + " ");
            } else {
                sb.append(String.format(v.getContext().getString(R.string.title_passed_polls_with_zero_points), " "));
            }
            sb.append(sdf.format(poll.getPassedDate() * 1000));
            if (Kind.isHearing(poll.getKind())) {
                sb.append(", " + v.getContext().getString(R.string.title_hearing_survey_summary).toLowerCase());
            }
            v.setTextColor(v.getContext().getResources().getColor(R.color.greenText));
            v.setText(sb);
        } else if (poll.isOld()) {
            v.setText(String.format(v.getContext().getString(R.string.title_old_polls), sdf.format(poll.getEndDate() * 1000)));
        }
    }

    public void displayTitle(TextView v, Poll poll) {
        v.setText(poll.getTitle());
    }

    public void setListener(View v, Poll poll) {
        v.setOnClickListener((view) -> AGApplication.bus().send(new Events.PollEvents(Events.PollEvents.OPEN_POLL, poll)));
    }
}
