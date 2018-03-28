package ru.mos.polls.survey.hearing.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.survey.hearing.gui.fragment.MeetingFragment;
import ru.mos.polls.survey.hearing.model.Meeting;

public class MeetingState extends ContentBelowToolbarState<MeetingState.Params>{

    public MeetingState(long hearingId, Meeting meeting, String surveyTitle) {
        super(new Params(hearingId, meeting, surveyTitle));
    }

    @Override
    protected JugglerFragment onConvertContent(Params params, @Nullable JugglerFragment fragment) {
        return MeetingFragment.getInstance(params.hearingId, params.meeting, params.surveyTitle);
    }

    @Override
    protected JugglerFragment onConvertToolbar(Params params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, Params params) {
        return context.getString(R.string.title_meeting);
    }

    static class Params extends State.Params {
        long hearingId;
        Meeting meeting;
        String surveyTitle;

        public Params(long hearingId, Meeting meeting, String surveyTitle) {
            this.hearingId = hearingId;
            this.meeting = meeting;
            this.surveyTitle = surveyTitle;
        }
    }
}
