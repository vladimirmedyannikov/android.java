package ru.mos.polls.electronichouse.vm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;

import ru.mos.polls.MainActivity;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentHouseAddressBinding;
import ru.mos.polls.electronichouse.ui.adapter.AddressListAdapter;
import ru.mos.polls.electronichouse.ui.fragment.HouseAddressFragment;
import ru.mos.polls.navigation.drawer.NavigationDrawerFragment;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 01.12.17.
 */

public class HouseAddressFragmentVM extends UIComponentFragmentViewModel<HouseAddressFragment, FragmentHouseAddressBinding> {

    private Button profileButton;

    public HouseAddressFragmentVM(HouseAddressFragment fragment, FragmentHouseAddressBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentHouseAddressBinding binding) {
        profileButton = binding.profileButton;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        profileButton.setOnClickListener(v -> {
            /**
             * говорим {@link NavigationDrawerFragment} чтобы открыл профиль
             */
            LocalBroadcastManager.getInstance(getFragment().getContext()).sendBroadcast(new Intent(NavigationDrawerFragment.ACTION_NEED_OPEN_PROFILE));
        });
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
