package ru.mos.polls.newprofile.vm;


import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.databinding.FragmentAddressesPropertyBinding;
import ru.mos.polls.newprofile.ui.adapter.AddressesAdapter;
import ru.mos.polls.newprofile.ui.fragment.AddressesPropertyFragment;

/**
 * Created by Trunks on 25.10.2017.
 */

public class AddressesPropertyFragmentVM extends PullablePaginationFragmentVM<AddressesPropertyFragment, FragmentAddressesPropertyBinding, AddressesAdapter> implements onAddressesDeleteIconClickListener {
    public AddressesPropertyFragmentVM(AddressesPropertyFragment fragment, FragmentAddressesPropertyBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentAddressesPropertyBinding binding) {
        recyclerView = binding.list;
        adapter = new AddressesAdapter(this);
        super.initialize(binding);
    }

    @Override
    public void doRequest() {

    }

    @Override
    public void onIconDeleteClick(String id) {

    }
}
