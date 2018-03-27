package ru.mos.polls.survey.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.ui.DetailsExpertsFragment;

public class DetailsExpertsState extends ContentBelowToolbarState<DetailsExpertsState.Params> {

    public static DetailsExpertsState getStateByPollId(DetailsExpert detailsExpert, long pollId, boolean isHearing) {
        return new DetailsExpertsState(detailsExpert, pollId, 0, isHearing);
    }

    public static DetailsExpertsState getStateByQuestionId(DetailsExpert detailsExpert, long questionId, boolean isHearing) {
        return new DetailsExpertsState(detailsExpert, 0, questionId, isHearing);
    }

    private DetailsExpertsState(DetailsExpert detailsExpert, long pollId, long questionId, boolean isHearing) {
        super(new Params(detailsExpert, pollId, questionId, isHearing));
    }

    @Override
    protected JugglerFragment onConvertContent(DetailsExpertsState.Params params, @Nullable JugglerFragment fragment) {
        return DetailsExpertsFragment.instance(params.detailsExpert, params.pollId, params.questionId, params.isHearing);
    }

    @Override
    protected JugglerFragment onConvertToolbar(DetailsExpertsState.Params params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, Params params) {
        return context.getString(R.string.title_experts);
    }

    static class Params extends State.Params {
        DetailsExpert detailsExpert;
        long pollId;
        long questionId;
        boolean isHearing;

        public Params(DetailsExpert detailsExpert, long pollId, long questionId, boolean isHearing) {
            this.detailsExpert = detailsExpert;
            this.pollId = pollId;
            this.questionId = questionId;
            this.isHearing = isHearing;
        }
    }
}
