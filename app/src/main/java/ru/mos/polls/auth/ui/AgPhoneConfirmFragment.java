package ru.mos.polls.auth.ui;

import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.auth.vm.AgPhoneConfirmFragmentVM;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentAgPhoneConfirmBinding;

public class AgPhoneConfirmFragment extends MenuBindingFragment<AgPhoneConfirmFragmentVM, FragmentAgPhoneConfirmBinding>{
    public static final String EXTRA_PHONE = "extra_phone";

    public static AgPhoneConfirmFragment instance(String phone) {
        AgPhoneConfirmFragment res = new AgPhoneConfirmFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PHONE, phone);
        res.setArguments(bundle);
        return res;
    }

    @Override
    protected AgPhoneConfirmFragmentVM onCreateViewModel(FragmentAgPhoneConfirmBinding binding) {
        return new AgPhoneConfirmFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_ag_phone_confirm;
    }

    @Override
    public int getMenuResource() {
        return R.menu.confirm;
    }

    public String getExtraPhone() {
        if (getArguments() != null) {
            return getArguments().getString(EXTRA_PHONE);
        }
        return "";
    }
}
