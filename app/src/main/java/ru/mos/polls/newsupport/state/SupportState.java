package ru.mos.polls.newsupport.state;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.VoidParams;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.newsupport.ui.fragment.SupportFragment;

/**
 * Created by matek3022 on 13.09.17.
 */

public class SupportState extends ContentBelowToolbarState<VoidParams> {

    public SupportState(@Nullable VoidParams params) {
        super(params);
    }

    @Override
    public String getTitle(Context context, VoidParams params) {
        return context.getString(R.string.title_support);
    }

    @Override
    public Drawable getUpNavigationIcon(Context context, VoidParams params) {
        return context.getResources().getDrawable(R.drawable.back);
    }

    @Override
    protected JugglerFragment onConvertContent(VoidParams params, @Nullable JugglerFragment fragment) {
        return SupportFragment.instance();
    }

    @Override
    protected JugglerFragment onConvertToolbar(VoidParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }


}
