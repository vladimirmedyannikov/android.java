package ru.mos.polls.electronichouse.vm;


import ru.mos.polls.MainActivity;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentHouseAddressBinding;
import ru.mos.polls.electronichouse.ui.adapter.AddressListAdapter;
import ru.mos.polls.electronichouse.ui.fragment.HouseAddressFragment;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 01.12.17.
 */

public class HouseAddressFragmentVM extends UIComponentFragmentViewModel<HouseAddressFragment, FragmentHouseAddressBinding> {


    public HouseAddressFragmentVM(HouseAddressFragment fragment, FragmentHouseAddressBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentHouseAddressBinding binding) {
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AddressListAdapter)getComponent(RecyclerUIComponent.class).getAdapter()).refreshData();
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().with(new RecyclerUIComponent<>(new AddressListAdapter((MainActivity) getActivity()))).build();
    }
}
