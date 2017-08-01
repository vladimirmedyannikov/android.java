package ru.mos.polls.wizardprofile.vm;


import android.content.Intent;

import ru.mos.polls.databinding.FragmentMakeAvatarBinding;
import ru.mos.polls.newprofile.vm.BaseTabFragmentVM;
import ru.mos.polls.wizardprofile.ui.fragment.MakeAvatarFragment;

/**
 * Created by Trunks on 27.07.2017.
 */

public class MakeAvatarFragmentVM extends BaseTabFragmentVM<MakeAvatarFragment, FragmentMakeAvatarBinding> {
    public MakeAvatarFragmentVM(MakeAvatarFragment fragment, FragmentMakeAvatarBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentMakeAvatarBinding binding) {
        circleImageView = binding.wizardAvatar;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCropedUri(requestCode, resultCode, data);
    }
}
