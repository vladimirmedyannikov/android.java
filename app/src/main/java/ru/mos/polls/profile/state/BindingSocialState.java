package ru.mos.polls.profile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.profile.ui.fragment.BindingSocialFragment;

public class BindingSocialState extends ContentBelowToolbarState<BindingSocialState.Params> {

    public BindingSocialState() {
        super(new Params(false));
    }

    public BindingSocialState(boolean isTask) {
        super(new Params(isTask));
    }

    @Override
    protected JugglerFragment onConvertContent(Params params, @Nullable JugglerFragment fragment) {
        return BindingSocialFragment.newInstance(params.isTask);
    }

    @Override
    protected JugglerFragment onConvertToolbar(Params params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();

    }

    @Nullable
    @Override
    public String getTitle(Context context, Params params) {
        return context.getString(R.string.socials);
    }

    static class Params extends State.Params {

        boolean isTask;

        public Params(boolean isTask) {
            this.isTask = isTask;
        }
    }
}