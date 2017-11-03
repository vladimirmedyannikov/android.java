package ru.mos.polls.profile.ui.fragment;


import android.support.annotation.NonNull;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentInfoTabProfileBinding;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.profile.vm.InfoTabFragmentVM;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class InfoTabFragment extends BindingFragment<InfoTabFragmentVM, FragmentInfoTabProfileBinding> {


    @Override
    protected InfoTabFragmentVM onCreateViewModel(FragmentInfoTabProfileBinding binding) {
        return new InfoTabFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_info_tab_profile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getViewModel().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
