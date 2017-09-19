package ru.mos.polls.wizardprofile.vm;


import android.content.Intent;

import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentMakeAvatarBinding;
import ru.mos.polls.newprofile.vm.BaseProfileTabFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.WizardAvatarFragment;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardAvatarFragmentVM extends BaseProfileTabFragmentVM<WizardAvatarFragment, FragmentMakeAvatarBinding> {
    public WizardAvatarFragmentVM(WizardAvatarFragment fragment, FragmentMakeAvatarBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return null; //no need
    }

    @Override
    protected void initialize(FragmentMakeAvatarBinding binding) {
        circleImageView = binding.wizardAvatar.avatar;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCropedUri(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAvatarLoaded) {
            setAvatar();
        }
    }
}
