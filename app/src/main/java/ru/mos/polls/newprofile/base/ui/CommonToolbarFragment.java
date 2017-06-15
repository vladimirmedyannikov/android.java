package ru.mos.polls.newprofile.base.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.gui.JugglerToolbarFragment;
import ru.mos.polls.R;

/**
 * Created by wlTrunks on 14.06.2017.
 */

public class CommonToolbarFragment extends JugglerToolbarFragment {

    public static JugglerFragment createBack() {
        CommonToolbarFragment f = new CommonToolbarFragment();
        f.setArguments(addDisplayOptionsToBundle(null, ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP));
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.toolbar, container, false);
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }
}
