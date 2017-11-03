package ru.mos.polls.about.state;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentToolbarNavigationState;
import me.ilich.juggler.states.VoidParams;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.base.ui.NavigationFragmentJuggler;
import ru.mos.polls.about.ui.fragment.AboutAppFragment;

/**
 * Created by matek3022 on 21.09.17.
 */

public class AboutAppState extends ContentToolbarNavigationState<VoidParams> {

    public AboutAppState(VoidParams params) {
        super(params);
    }

    @Override
    public String getTitle(Context context, VoidParams params) {
        return context.getString(R.string.title_help);
    }

    @Override
    public Drawable getUpNavigationIcon(Context context, VoidParams params) {
        return context.getResources().getDrawable(R.drawable.nb_icon_navbar);
    }

    @Override
    protected JugglerFragment onConvertContent(VoidParams params, @Nullable JugglerFragment fragment) {
        return AboutAppFragment.instance(null);
    }

    @Override
    protected JugglerFragment onConvertToolbar(VoidParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createNavigation();
    }

    @Override
    protected JugglerFragment onConvertNavigation(VoidParams params, @Nullable JugglerFragment fragment) {
        return NavigationFragmentJuggler.create(R.id.menu_support);
    }
}