package ru.mos.polls.auth.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.auth.ui.AgPhoneConfirmFragment;
import ru.mos.polls.base.ui.CommonToolbarFragment;

public class AgPhoneConfirmState extends ContentBelowToolbarState<AgPhoneConfirmState.Params> {

    public AgPhoneConfirmState(String phone) {
        super(new Params(phone));
    }

    @Override
    protected JugglerFragment onConvertContent(AgPhoneConfirmState.Params params, @Nullable JugglerFragment fragment) {
        return AgPhoneConfirmFragment.instance(params.phone);
    }

    @Override
    protected JugglerFragment onConvertToolbar(AgPhoneConfirmState.Params params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    @Nullable
    @Override
    public String getTitle(Context context, Params params) {
        return "Подтверждение";
    }

    static class Params extends State.Params {
        String phone;

        public Params(String phone) {
            this.phone = phone;
        }
    }
}
