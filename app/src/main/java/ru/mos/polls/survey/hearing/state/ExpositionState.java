package ru.mos.polls.survey.hearing.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.survey.hearing.gui.fragment.ExpositionFragment;
import ru.mos.polls.survey.hearing.model.Exposition;

public class ExpositionState extends ContentBelowToolbarState<ExpositionState.Params> {

    public ExpositionState(Exposition exposition, String surveyTitle) {
        super(new Params(exposition, surveyTitle));
    }

    @Override
    protected JugglerFragment onConvertContent(Params params, @Nullable JugglerFragment fragment) {
        return ExpositionFragment.getInstance(params.exposition, params.surveyTitle);
    }

    @Override
    protected JugglerFragment onConvertToolbar(Params params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, Params params) {
        return context.getString(R.string.title_exposition);
    }

    static class Params extends State.Params {
        Exposition exposition;
        String surveyTitle;

        public Params(Exposition exposition, String surveyTitle) {
            this.exposition = exposition;
            this.surveyTitle = surveyTitle;
        }
    }
}
