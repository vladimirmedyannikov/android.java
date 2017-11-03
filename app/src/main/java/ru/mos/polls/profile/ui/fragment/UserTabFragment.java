package ru.mos.polls.profile.ui.fragment;

import android.support.annotation.NonNull;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentUserTabProfileBinding;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.profile.vm.UserTabFragmentVM;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class UserTabFragment extends BindingFragment<UserTabFragmentVM, FragmentUserTabProfileBinding> {

    public static UserTabFragment newInstance() {
        return new UserTabFragment();
    }


    public UserTabFragment() {
    }

    @Override
    protected UserTabFragmentVM onCreateViewModel(FragmentUserTabProfileBinding binding) {
        return new UserTabFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_user_tab_profile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getViewModel().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
