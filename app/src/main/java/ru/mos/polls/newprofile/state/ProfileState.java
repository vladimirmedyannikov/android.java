package ru.mos.polls.newprofile.state;

import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentOnlyState;
import me.ilich.juggler.states.VoidParams;
import ru.mos.polls.newprofile.ui.fragment.ProfileFragment;

/**
 * Created by Trunks on 06.06.2017.
 */

public class ProfileState extends ContentOnlyState<VoidParams> {
    public ProfileState(@Nullable VoidParams params) {
        super(params);
    }

    @Override
    protected JugglerFragment onConvertContent(VoidParams params, @Nullable JugglerFragment fragment) {
        return null;
    }

//    @Override
//    protected JugglerFragment onConvertNavigation(VoidParams params, @Nullable JugglerFragment fragment) {
//        return  ProfileFragment.newInstance();
//    }
}
