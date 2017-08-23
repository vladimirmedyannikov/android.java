package ru.mos.polls.newprofile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.newprofile.ui.fragment.CustomFlatFragment;

/**
 * Created by Trunks on 04.08.2017.
 */

public class CustomFlatState extends ContentBelowToolbarState<CustomFlatState.CustomFlatParams> {

    public CustomFlatState(String street, String house, Flat flat, boolean hideWarning, boolean forWizard, int flatType) {
        super(new CustomFlatParams(street, house, flat, hideWarning, forWizard,flatType));
    }

    @Override
    protected JugglerFragment onConvertContent(CustomFlatParams params, @Nullable JugglerFragment fragment) {
        return CustomFlatFragment.newInstance(params.flat, params.hideWarning, params.street, params.house, params.forWizard, params.flatType);
    }

    @Override
    protected JugglerFragment onConvertToolbar(CustomFlatParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, CustomFlatParams params) {
        return "Адрес";
    }

    static class CustomFlatParams extends State.Params {
        String street;
        String house;
        Flat flat;
        boolean hideWarning;
        boolean forWizard;
        int flatType;

        public CustomFlatParams(String street, String house, Flat flat, boolean hideWarning, boolean forWizard, int flatType) {
            this.street = street;
            this.house = house;
            this.flat = flat;
            this.hideWarning = hideWarning;
            this.forWizard = forWizard;
            this.flatType = flatType;
        }
    }
}
