package ru.mos.polls.newprofile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.newprofile.ui.fragment.PguAuthFragment;
import ru.mos.polls.survey.hearing.gui.fragment.PguBindFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class PguAuthState extends ContentBelowToolbarState<PguAuthState.PguParams> {

    public static final int PGU_STATUS = 1;
    public static final int PGU_AUTH = 2;

    public PguAuthState(int pgu) {
        super(new PguParams(pgu));
    }

    @Override
    protected JugglerFragment onConvertContent(PguParams params, @Nullable JugglerFragment fragment) {
        switch (params.pgu) {
            case PGU_STATUS:
                return PguAuthFragment.newInstance(false);
            case PGU_AUTH:
                return new PguBindFragment();
        }
        return null;
    }

    @Override
    protected JugglerFragment onConvertToolbar(PguParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();

    }

    @Nullable
    @Override
    public String getTitle(Context context, PguParams params) {
        switch (params.pgu) {
            case PGU_STATUS:
                return context.getString(R.string.connection_mos_pgu_title);
            case PGU_AUTH:
                return context.getString(R.string.title_pgu_auth);
        }
        return context.getString(R.string.connection_mos_pgu_title);
    }

    static class PguParams extends State.Params {
        int pgu;

        PguParams(int pgu) {
            this.pgu = pgu;
        }
    }
}
