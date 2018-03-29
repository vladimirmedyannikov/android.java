package ru.mos.polls.changepassword.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.VoidParams;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.changepassword.ui.ChangePasswordFragment;


public class ChangePasswordState extends ContentBelowToolbarState<VoidParams> {
    public ChangePasswordState() {
        super(VoidParams.instance());
    }

    @Override
    protected JugglerFragment onConvertContent(VoidParams params, @Nullable JugglerFragment fragment) {
        return ChangePasswordFragment.newInstance();
    }

    @Override
    protected JugglerFragment onConvertToolbar(VoidParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, VoidParams params) {
        return context.getString(R.string.change_password);
    }
}
