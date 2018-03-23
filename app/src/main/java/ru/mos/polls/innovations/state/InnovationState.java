package ru.mos.polls.innovations.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.innovations.ui.fragment.InnovationFragment;

public class InnovationState extends ContentBelowToolbarState<InnovationState.InnovationParams> {

    public InnovationState(long id) {
        super(new InnovationParams(id));
    }

    @Override
    protected JugglerFragment onConvertContent(InnovationParams params, @Nullable JugglerFragment fragment) {
        return InnovationFragment.instance(params.id);
    }

    @Override
    protected JugglerFragment onConvertToolbar(InnovationParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, InnovationParams params) {
        return context.getString(R.string.title_innovation);
    }

    static class InnovationParams extends State.Params {
        long id;

        public InnovationParams(long id) {
            this.id = id;
        }
    }
}