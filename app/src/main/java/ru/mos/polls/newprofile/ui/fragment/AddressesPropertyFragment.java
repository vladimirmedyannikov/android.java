package ru.mos.polls.newprofile.ui.fragment;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentAddressesPropertyBinding;
import ru.mos.polls.newprofile.vm.AddressesPropertyFragmentVM;

/**
 * Created by Trunks on 25.10.2017.
 */

public class AddressesPropertyFragment extends MenuBindingFragment<AddressesPropertyFragmentVM, FragmentAddressesPropertyBinding> {

    public static AddressesPropertyFragment newInstance() {
        AddressesPropertyFragment f = new AddressesPropertyFragment();
        return f;
    }

    @Override
    protected AddressesPropertyFragmentVM onCreateViewModel(FragmentAddressesPropertyBinding binding) {
        return new AddressesPropertyFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_addresses_property;
    }

    @Override
    public int getMenuResource() {
        return R.menu.confirm;
    }
}
