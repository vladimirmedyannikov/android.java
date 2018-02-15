package ru.mos.polls.auth.state;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.auth.ui.AuthFragment;
import ru.mos.polls.base.ui.CommonToolbarFragment;


public class AuthState extends ContentBelowToolbarState<AuthState.Params> {


    public AuthState(String phone) {
        super(new Params(phone));
    }

    @Override
    public String getTitle(Context context, AuthState.Params params) {
        return context.getString(R.string.elk_authorization);
    }

    @Override
    public Drawable getUpNavigationIcon(Context context, AuthState.Params params) {
        return context.getResources().getDrawable(R.drawable.back);
    }

    @Override
    protected JugglerFragment onConvertContent(AuthState.Params params, @Nullable JugglerFragment fragment) {
        return AuthFragment.newInstance(params.phone);
    }

    @Override
    protected JugglerFragment onConvertToolbar(AuthState.Params params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    static class Params extends State.Params {
        String phone;

        public Params(String phone) {
            this.phone = phone;
        }
    }
}
