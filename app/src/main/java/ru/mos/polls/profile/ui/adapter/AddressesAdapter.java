package ru.mos.polls.profile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.profile.model.flat.Flat;
import ru.mos.polls.profile.vm.AddressesPropertyVM;
import ru.mos.polls.profile.vm.onAddressesDeleteIconClickListener;

public class AddressesAdapter extends BaseRecyclerAdapter<AddressesPropertyVM> {
    onAddressesDeleteIconClickListener clickListener;

    public AddressesAdapter(onAddressesDeleteIconClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void add(List<Flat> flats) {
        List<AddressesPropertyVM> content = new ArrayList<>();
        for (Flat flat : flats) {
            AddressesPropertyVM addressesPropertyVM = new AddressesPropertyVM(flat);
            addressesPropertyVM.setClickListener(clickListener);
            content.add(addressesPropertyVM);
        }
        addData(content);
    }


    public void add(Flat flat) {
        AddressesPropertyVM addressesPropertyVM = new AddressesPropertyVM(flat);
        addressesPropertyVM.setClickListener(clickListener);
        add(addressesPropertyVM);
    }

    public void removeItem(String flatId) {
        for (RecyclerBaseViewModel recyclerBaseViewModel : list) {
            if (flatId.equalsIgnoreCase(((Flat) recyclerBaseViewModel.getModel()).getFlatId())) {
                list.remove(recyclerBaseViewModel);
                break;
            }
        }
        notifyDataSetChanged();
    }
}
