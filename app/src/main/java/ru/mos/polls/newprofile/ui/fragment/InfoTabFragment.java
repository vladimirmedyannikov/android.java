package ru.mos.polls.newprofile.ui.fragment;


import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutInfoTabProfileBinding;
import ru.mos.polls.newprofile.base.ui.BindingFragment;
import ru.mos.polls.newprofile.vm.InfoTabFragmentVM;

/**
 * Created by wlTrunks on 07.06.2017.
 */

public class InfoTabFragment extends BindingFragment<InfoTabFragmentVM, LayoutInfoTabProfileBinding> {


    @Override
    protected InfoTabFragmentVM onCreateViewModel(LayoutInfoTabProfileBinding binding) {
        return new InfoTabFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_info_tab_profile;
    }


}
