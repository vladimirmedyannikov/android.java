package ru.mos.polls.newprofile.vm;

import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemAddressePropertyBinding;

/**
 * Created by Trunks on 25.10.2017.
 */

public class AddressesPropertyVM extends RecyclerBaseViewModel<Flat, ItemAddressePropertyBinding> {
    onAddressesDeleteIconClickListener clickListener;

    public void setClickListener(onAddressesDeleteIconClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public AddressesPropertyVM(Flat model) {
        super(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_addresse_property;
    }

    @Override
    public int getViewType() {
        return 0;
    }
}
