package ru.mos.polls.newprofile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.newprofile.base.ui.CommonToolbarFragment;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoState extends ContentBelowToolbarState<EditPersonalInfoState.PesonalInfoParams> {
    public EditPersonalInfoState(int params) {
        super(new PesonalInfoParams(params));
    }

    @Override
    protected JugglerFragment onConvertContent(PesonalInfoParams params, @Nullable JugglerFragment fragment) {
        return EditPersonalInfoFragment.newInstance(params.personalType);
    }

    @Override
    protected JugglerFragment onConvertToolbar(PesonalInfoParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();

    }

    @Nullable
    @Override
    public String getTitle(Context context, PesonalInfoParams params) {
        switch (params.personalType) {
            case EditPersonalInfoFragmentVM.PERSONAL_EMAIL:
                return context.getString(R.string.contact_info_title);
            case EditPersonalInfoFragmentVM.PERSONAL_FIO:
                return context.getString(R.string.full_user_name_title);
        }
        return super.getTitle(context, params);
    }

    static class PesonalInfoParams extends State.Params {

        int personalType;

        public PesonalInfoParams(int personalType) {
            this.personalType = personalType;
        }
    }
}
