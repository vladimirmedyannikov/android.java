package ru.mos.polls.profile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.profile.ui.fragment.EditProfileFragment;

public class EditProfileState extends ContentBelowToolbarState<EditProfileState.Params> {
    public EditProfileState(int scrollCoord) {
        super(new Params(scrollCoord));
    }

    @Override
    protected JugglerFragment onConvertContent(EditProfileState.Params params, @Nullable JugglerFragment fragment) {
        return EditProfileFragment.newInstance(params.scrollCoord);
    }

    @Override
    protected JugglerFragment onConvertToolbar(EditProfileState.Params params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, EditProfileState.Params params) {
        return context.getString(R.string.edit_profile);
    }

    /**
     * координата на которой были на экране {@link ru.mos.polls.profile.vm.InfoTabFragmentVM}
     */
    static class Params extends State.Params {
        int scrollCoord;

        public Params(int scrollCoord) {
            this.scrollCoord = scrollCoord;
        }
    }
}
