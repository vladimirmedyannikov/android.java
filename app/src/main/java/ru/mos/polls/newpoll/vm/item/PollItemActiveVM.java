package ru.mos.polls.newpoll.vm.item;

import android.view.View;
import android.widget.TextView;

import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemActivePollBinding;
import ru.mos.polls.newpoll.ui.adapter.PollAdapter;
import ru.mos.polls.poll.model.Poll;

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
    protected int getLayoutId() {
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
        displayDescription(viewDataBinding.description, model);
    }


    private void displayDescription(TextView v, Poll poll) {
        if (poll.getPoints() > 0) {
            String result = PointsManager.getSuitableString(v.getContext(), R.array.survey_points_pluse, poll.getPoints());
            result = String.format(result, poll.getPoints());
            if (poll.getKind().isHearing()) {
                result += ", " + v.getContext().getString(R.string.title_hearing_survey_summary).toLowerCase();
            } else if (poll.getKind().isSpecial()) {
                result += ", " + v.getContext().getString(R.string.special_hearing).toLowerCase();
            }
            v.setText(result);
        } else {
            String result = "";
            if (poll.getKind().isHearing()) {
                result += v.getContext().getString(R.string.title_hearing_survey_summary);
            } else if (poll.getKind().isSpecial()) {
                result += v.getContext().getString(R.string.special_hearing);
            } else {
                v.setVisibility(View.GONE);
            }
            v.setText(result);
        }
    }
}
