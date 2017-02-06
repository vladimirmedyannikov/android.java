package ru.mos.polls.poll.gui;

import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.poll.adapter.NotActivePollAdapter;
import ru.mos.polls.poll.controller.PollApiController;
import ru.mos.polls.poll.model.Poll;


public class OldPollsFragment extends AbstractPollsFragment implements ActivePollsFragment.PollRemoveListener {
    public static Fragment newInstance() {
        return new OldPollsFragment();
    }

    @Override
    ArrayAdapter getAdapter() {
        return new NotActivePollAdapter(getActivity(), polls, listener);
    }

    @Override
    PollApiController.Filter[] getFilter() {
        return new PollApiController.Filter[]{PollApiController.Filter.OLD, PollApiController.Filter.PASSED};
    }

    @Override
    public void onResume() {
        super.onResume();
        GoogleStatistics.Survey.enterPollsUnactive();
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.empty_old_pollslist);
    }

    @Override
    public void onRemove(Poll poll) {
        polls.add(0, poll);
        adapter.notifyDataSetChanged();
    }
}
