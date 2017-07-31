package ru.mos.polls.newprofile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.newprofile.base.ui.CommonToolbarFragment;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.newprofile.vm.NewFlatFragmentVM;

/**
 * Created by Trunks on 04.07.2017.
 */

public class NewFlatState extends ContentBelowToolbarState<NewFlatState.FlatParams> {
    public NewFlatState(Flat flat, int flatType) {
        super(new FlatParams(flat, flatType));
    }

    @Override
    protected JugglerFragment onConvertContent(FlatParams params, @Nullable JugglerFragment fragment) {
        return NewFlatFragment.newInstance(params.flat, params.flatType);
    }

    @Override
    protected JugglerFragment onConvertToolbar(FlatParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();

    }

    @Nullable
    @Override
    public String getTitle(Context context, FlatParams params) {
        switch (params.flatType) {
            case NewFlatFragmentVM.FLAT_TYPE_REGISTRATION:
                return context.getString(R.string.flat_title_registration);
            case NewFlatFragmentVM.FLAT_TYPE_RESIDENCE:
                return context.getString(R.string.flat_title_residence);
            case NewFlatFragmentVM.FLAT_TYPE_WORK:
                return context.getString(R.string.flat_title_work);
        }
        return super.getTitle(context, params);
    }

    static class FlatParams extends State.Params {
        int flatType;
        Flat flat;

        FlatParams(Flat flat, int flatType) {
            this.flat = flat;
            this.flatType = flatType;
        }
    }
}
