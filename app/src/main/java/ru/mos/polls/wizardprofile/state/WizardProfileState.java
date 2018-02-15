package ru.mos.polls.wizardprofile.state;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class WizardProfileState extends ContentBelowToolbarState<WizardProfileState.WizardProfileParams> {
    public WizardProfileState(List<String> list, int percent) {
        super(new WizardProfileParams(list, percent));
    }

    @Override
    protected JugglerFragment onConvertContent(WizardProfileParams params, @Nullable JugglerFragment fragment) {
        return WizardProfileFragment.newInstance(params.list, params.percent);
    }

    @Override
    protected JugglerFragment onConvertToolbar(WizardProfileParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();

    }

    @Nullable
    @Override
    public String getTitle(Context context, WizardProfileParams params) {
        return "Заполнение профиля";
    }

    static class WizardProfileParams extends State.Params {
        List<String> list;
        int percent;

        public WizardProfileParams(List<String> list, int percent) {
            this.list = list;
            this.percent = percent;
        }

    }
}