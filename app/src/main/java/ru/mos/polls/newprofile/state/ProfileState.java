package ru.mos.polls.newprofile.state;

import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.ContentNavigationState;
import me.ilich.juggler.states.ContentOnlyState;
import me.ilich.juggler.states.VoidParams;
import ru.mos.polls.navigation.drawer.NavigationDrawerFragment;
import ru.mos.polls.newprofile.MainFragment;
import ru.mos.polls.newprofile.ProfileFtagment;

/**
 * Created by Trunks on 06.06.2017.
 */

public class ProfileState extends ContentOnlyState<VoidParams> {
    public ProfileState(@Nullable VoidParams params) {
        super(params);
    }

    @Override
    protected JugglerFragment onConvertContent(VoidParams params, @Nullable JugglerFragment fragment) {
        return ProfileFtagment.newInstance();
    }

//    @Override
//    protected JugglerFragment onConvertNavigation(VoidParams params, @Nullable JugglerFragment fragment) {
//        return  ProfileFtagment.newInstance();
//    }
}
