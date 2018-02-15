package ru.mos.polls.poll.vm.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import ru.mos.polls.AGApplication;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.ItemActivePollBinding;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.model.Poll;
import ru.mos.polls.poll.ui.adapter.PollAdapter;

/**
 * Created by Trunks on 14.09.2017.
 */

public class PollItemActiveVM extends RecyclerBaseViewModel<Poll, ItemActivePollBinding> {

    public PollItemActiveVM(Poll model, ItemActivePollBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public PollItemActiveVM(Poll model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_active_poll;
    }

    @Override
    public int getViewType() {
        return PollAdapter.Type.ITEM_ACTIVE;
    }

    @Override
    public void onBind(ItemActivePollBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(model);
        viewDataBinding.notifyChange();
        /**
         * см {@link Poll#getDescriptionForActivePollDataBinding(Context)}
         * и {@link Poll#getDescriptionVisibleForActivePollDataBinding()}
         */
//        displayDescription(viewDataBinding.description, model);
        displayInterruptedMark(viewDataBinding.interrupted, model);
        setListener(viewDataBinding.baseInfoContainer, model);
    }


    public void displayDescription(TextView v, Poll poll) {
        if (poll.getPoints() > 0) {
            String result = PointsManager.getSuitableString(v.getContext(), R.array.survey_points_pluse, poll.getPoints());
            result = String.format(result, poll.getPoints());
            if (Kind.isHearing(poll.getKind())) {
                result += ", " + v.getContext().getString(R.string.title_hearing_survey_summary).toLowerCase();
            } else if (Kind.isSpecial(poll.getKind())) {
                result += ", " + v.getContext().getString(R.string.special_hearing).toLowerCase();
            }
            v.setText(result);
        } else {
            String result = "";
            if (Kind.isHearing(poll.getKind())) {
                result += v.getContext().getString(R.string.title_hearing_survey_summary);
            } else if (Kind.isSpecial(poll.getKind())) {
                result += v.getContext().getString(R.string.special_hearing);
            } else {
                v.setVisibility(View.GONE);
            }
            v.setText(result);
        }
    }

    public void displayInterruptedMark(TextView v, Poll poll) {
        int visibility = View.GONE;
        if (poll.isInterrupted()) {
            visibility = View.VISIBLE;
        }
        v.setVisibility(visibility);
    }

    public void setListener(View v, Poll poll) {
        v.setOnClickListener((view) -> AGApplication.bus().send(new Events.PollEvents(Events.PollEvents.OPEN_POLL, poll)));
    }
}
