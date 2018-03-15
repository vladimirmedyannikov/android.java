package ru.mos.polls.auth.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.VoidParams;
import ru.mos.polls.R;
import ru.mos.polls.auth.ui.AgAuthFragment;
import ru.mos.polls.base.ui.CommonToolbarFragment;


public class AgAuthState extends ContentBelowToolbarState<VoidParams> {

    public AgAuthState() {
        super(VoidParams.instance());
    }

    @Override
    public String getTitle(Context context, VoidParams params) {
        return context.getString(R.string.ag_auth_title);
    }

    @Override
    protected JugglerFragment onConvertContent(VoidParams params, @Nullable JugglerFragment fragment) {
        return AgAuthFragment.instance();
    }

    @Override
    protected JugglerFragment onConvertToolbar(VoidParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.create();
    }
}
