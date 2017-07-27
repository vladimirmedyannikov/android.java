package ru.mos.polls.wizardprofile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.VoidParams;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.newprofile.base.ui.CommonToolbarFragment;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;
import ru.mos.polls.newprofile.vm.NewFlatFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class WizardProfileState extends ContentBelowToolbarState<VoidParams> {
    public WizardProfileState() {
        super(VoidParams.instance());
    }

    @Override
    protected JugglerFragment onConvertContent(VoidParams params, @Nullable JugglerFragment fragment) {
        return new WizardProfileFragment();
    }

    @Override
    protected JugglerFragment onConvertToolbar(VoidParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();

    }

    @Nullable
    @Override
    public String getTitle(Context context, VoidParams params) {
        return "Заполнение профиля";
    }
}
