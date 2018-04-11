package ru.mos.polls.support.state;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.support.ui.fragment.FeedBackFragment;
import ru.mos.polls.support.ui.fragment.SupportFragment;

public class SupportState extends ContentBelowToolbarState<SupportState.Params> {

    public SupportState(boolean startWithNewActivity) {
        super(new Params(startWithNewActivity));
    }

    @Override
    public String getTitle(Context context, SupportState.Params params) {
        return context.getString(R.string.title_support);
    }

    @Override
    public Drawable getUpNavigationIcon(Context context, SupportState.Params params) {
        return context.getResources().getDrawable(R.drawable.back);
    }

    @Override
    protected JugglerFragment onConvertContent(SupportState.Params params, @Nullable JugglerFragment fragment) {
        return FeedBackFragment.instance(params.startWithNewActivity);
    }

    @Override
    protected JugglerFragment onConvertToolbar(SupportState.Params params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    static class Params extends State.Params{
        boolean startWithNewActivity;

        Params(boolean startWithNewActivity) {
            this.startWithNewActivity = startWithNewActivity;
        }
    }
}
