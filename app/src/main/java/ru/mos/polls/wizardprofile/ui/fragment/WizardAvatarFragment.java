package ru.mos.polls.wizardprofile.ui.fragment;

import android.widget.Toast;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentMakeAvatarBinding;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.wizardprofile.vm.WizardAvatarFragmentVM;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardAvatarFragment extends NavigateFragment<WizardAvatarFragmentVM, FragmentMakeAvatarBinding> {
    @Override
    protected WizardAvatarFragmentVM onCreateViewModel(FragmentMakeAvatarBinding binding) {
        return new WizardAvatarFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_make_avatar;
    }

    @Override
    public void doRequestAction() {
        if (getViewModel().isAvatarLoaded) {

        } else {
            Toast.makeText(getActivity(), "Вы не загрузили аватарку", Toast.LENGTH_SHORT).show();
        }
    }
}
