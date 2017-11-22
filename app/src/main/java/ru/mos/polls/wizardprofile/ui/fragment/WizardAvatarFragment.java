package ru.mos.polls.wizardprofile.ui.fragment;

import android.support.annotation.NonNull;
import android.widget.Toast;

import ru.mos.polls.AGApplication;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.rxjava.Events;
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
//        if (!getViewModel().isAvatarLoaded) {
//            Toast.makeText(getActivity(), "Вы не загрузили аватарку", Toast.LENGTH_SHORT).show();
//        }
        AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_AVATAR, 0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getViewModel().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
