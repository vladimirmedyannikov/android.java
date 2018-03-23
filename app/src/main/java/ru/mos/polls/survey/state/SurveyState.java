package ru.mos.polls.survey.state;


import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.survey.ui.SurveyMainFragment;

public class SurveyState extends ContentBelowToolbarState<SurveyState.SurveyParams> {
    public SurveyState(long id, boolean isHearing) {
        super(new SurveyParams(id, isHearing));
    }

    @Override
    protected JugglerFragment onConvertContent(SurveyState.SurveyParams params, @Nullable JugglerFragment fragment) {
        return SurveyMainFragment.instance(params.id, params.isHearing);
    }

    @Override
    protected JugglerFragment onConvertToolbar(SurveyState.SurveyParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    static class SurveyParams extends State.Params {
        long id;
        boolean isHearing;

        public SurveyParams(long id, boolean isHearing) {
            this.id = id;
            this.isHearing = isHearing;
        }
    }
}
