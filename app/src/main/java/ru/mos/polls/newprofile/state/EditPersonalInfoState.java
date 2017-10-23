package ru.mos.polls.newprofile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoState extends ContentBelowToolbarState<EditPersonalInfoState.PesonalInfoParams> {
    public EditPersonalInfoState(AgUser agUser, int personalType) {
        super(new PesonalInfoParams(agUser, personalType));
    }

    @Override
    protected JugglerFragment onConvertContent(PesonalInfoParams params, @Nullable JugglerFragment fragment) {
        return EditPersonalInfoFragment.newInstance(params.agUser, params.personalType, false);
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
            case EditPersonalInfoFragmentVM.COUNT_KIDS:
            case EditPersonalInfoFragmentVM.BIRTHDAY_KIDS:
                return context.getString(R.string.childs);
            case EditPersonalInfoFragmentVM.SOCIAL_STATUS:
            case EditPersonalInfoFragmentVM.WIZARD_SOCIAL_STATUS:
                return context.getString(R.string.title_social_status);
        }
        return super.getTitle(context, params);
    }

    static class PesonalInfoParams extends State.Params {

        int personalType;
        AgUser agUser;

        public PesonalInfoParams(AgUser agUser, int personalType) {
            this.personalType = personalType;
            this.agUser = agUser;
        }
    }
}
