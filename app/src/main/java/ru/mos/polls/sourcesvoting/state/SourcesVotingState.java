package ru.mos.polls.sourcesvoting.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.VoidParams;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.sourcesvoting.ui.SourcesVotingFragment;

public class SourcesVotingState extends ContentBelowToolbarState<VoidParams> {
    public SourcesVotingState() {
        super(VoidParams.instance());
    }

    @Override
    protected JugglerFragment onConvertContent(VoidParams params, @Nullable JugglerFragment fragment) {
        return SourcesVotingFragment.newInstance();
    }

    @Override
    protected JugglerFragment onConvertToolbar(VoidParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, VoidParams params) {
        return context.getString(R.string.sources_poll);
    }
}
