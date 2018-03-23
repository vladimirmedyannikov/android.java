package ru.mos.polls.profile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.profile.ui.fragment.AchievementFragment;

public class AchievementState extends ContentBelowToolbarState<AchievementState.Params> {

    public AchievementState(String id, boolean isOwn) {
        super(new Params(id, isOwn));
    }

    @Override
    protected JugglerFragment onConvertContent(AchievementState.Params params, @Nullable JugglerFragment fragment) {
        return AchievementFragment.instance(params.id, params.isOwn);
    }

    @Override
    protected JugglerFragment onConvertToolbar(AchievementState.Params params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, AchievementState.Params params) {
        return context.getString(R.string.achievement_tab_title);
    }

    static class Params extends State.Params {
        String id;
        boolean isOwn;

        public Params(String id, boolean isOwn) {
            this.id = id;
            this.isOwn = isOwn;
        }
    }
}