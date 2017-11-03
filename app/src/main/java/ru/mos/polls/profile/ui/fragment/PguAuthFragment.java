package ru.mos.polls.profile.ui.fragment;

import android.os.Bundle;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentPguAuthBinding;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.profile.vm.PguAuthFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 03.08.2017.
 */

public class PguAuthFragment extends NavigateFragment<PguAuthFragmentVM, FragmentPguAuthBinding> {
    public static PguAuthFragment newInstance(boolean forWizard) {
        PguAuthFragment f = new PguAuthFragment();
        Bundle args = new Bundle();
        args.putBoolean(WizardProfileFragment.ARG_FOR_WIZARD, forWizard);
        f.setArguments(args);
        return f;
    }

    public static PguAuthFragment newInstanceForWizard() {
        return newInstance(true);
    }

    @Override
    protected PguAuthFragmentVM onCreateViewModel(FragmentPguAuthBinding binding) {
        return new PguAuthFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_pgu_auth;
    }

}
