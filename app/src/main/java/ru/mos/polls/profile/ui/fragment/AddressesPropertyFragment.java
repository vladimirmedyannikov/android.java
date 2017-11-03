package ru.mos.polls.profile.ui.fragment;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentAddressesPropertyBinding;
import ru.mos.polls.profile.vm.AddressesPropertyFragmentVM;

/**
 * Created by Trunks on 25.10.2017.
 */

public class AddressesPropertyFragment extends NavigateFragment<AddressesPropertyFragmentVM, FragmentAddressesPropertyBinding> {

    public static AddressesPropertyFragment newInstance() {
        AddressesPropertyFragment f = new AddressesPropertyFragment();
        return f;
    }

    @Override
    protected AddressesPropertyFragmentVM onCreateViewModel(FragmentAddressesPropertyBinding binding) {
        return new AddressesPropertyFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_addresses_property;
    }

}
