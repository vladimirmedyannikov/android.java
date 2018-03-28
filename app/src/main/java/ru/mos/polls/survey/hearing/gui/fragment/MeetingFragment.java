package ru.mos.polls.survey.hearing.gui.fragment;

import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentMeetingBinding;
import ru.mos.polls.survey.hearing.model.Meeting;
import ru.mos.polls.survey.hearing.vm.MeetingFragmentVM;

/**
 * Экран для отображения данных по собранию публичного слушания
 *
 * @since 2.0
 */

public class MeetingFragment extends NavigateFragment<MeetingFragmentVM, FragmentMeetingBinding>{

    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_MEETING = "extra_meeting";
    private static final String EXTRA_HEARING_ID = "extra_hearing_id";

    public static MeetingFragment getInstance(long hearingId, Meeting meeting, String surveyTitle) {
        MeetingFragment res = new MeetingFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_HEARING_ID, hearingId);
        bundle.putSerializable(EXTRA_MEETING, meeting);
        bundle.putString(EXTRA_TITLE, surveyTitle);
        res.setArguments(bundle);
        return res;
    }

    @Override
    protected MeetingFragmentVM onCreateViewModel(FragmentMeetingBinding binding) {
        return new MeetingFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_meeting;
    }

    @Override
    public boolean onBackPressed() {
        if (getActivity() != null) getActivity().setResult(getViewModel().getResult());
        return super.onBackPressed();
    }

    @Override
    public boolean onUpPressed() {
        if (getActivity() != null) getActivity().setResult(getViewModel().getResult());
        return super.onUpPressed();
    }

    public long getExtraHearingId() {
        if (getArguments() == null) return 0;
        return getArguments().getLong(EXTRA_HEARING_ID, 0);
    }

    public String getExtraTitle() {
        if (getArguments() == null) return "";
        return getArguments().getString(EXTRA_TITLE, "");
    }

    public Meeting getExtraMeeting() {
        if (getArguments() == null) return null;
        return (Meeting) getArguments().getSerializable(EXTRA_MEETING);
    }
}
