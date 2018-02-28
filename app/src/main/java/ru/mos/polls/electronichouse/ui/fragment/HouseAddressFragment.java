package ru.mos.polls.electronichouse.ui.fragment;

import com.android.databinding.library.baseAdapters.BR;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentHouseAddressBinding;
import ru.mos.polls.electronichouse.vm.HouseAddressFragmentVM;


public class HouseAddressFragment extends BindingFragment<HouseAddressFragmentVM, FragmentHouseAddressBinding> {

    public static HouseAddressFragment newInstance() {
        return new HouseAddressFragment();
    }

    @Override
    protected HouseAddressFragmentVM onCreateViewModel(FragmentHouseAddressBinding binding) {
        return new HouseAddressFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_house_address;
    }
}
