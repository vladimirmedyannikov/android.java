package ru.mos.polls.wizardprofile.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentMakeAvatarBinding;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.wizardprofile.vm.MakeAvatarFragmentVM;

/**
 * Created by Trunks on 27.07.2017.
 */

public class MakeAvatarFragment extends BindingFragment<MakeAvatarFragmentVM, FragmentMakeAvatarBinding> {
    @Override
    protected MakeAvatarFragmentVM onCreateViewModel(FragmentMakeAvatarBinding binding) {
        return new MakeAvatarFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_make_avatar;
    }
}
