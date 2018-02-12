package ru.mos.polls.profile.vm;

import android.text.TextUtils;

import ru.mos.polls.profile.model.flat.Flat;
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
    public int getLayoutId() {
        return R.layout.item_addresse_property;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public void onBind(ItemAddressePropertyBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(this);
        viewDataBinding.setListener(clickListener);
        viewDataBinding.executePendingBindings();
    }

    public String getFlatId() {
        return model.getFlatId();
    }

    public String getPropertyAddress() {
        String street = TextUtils.isEmpty(model.getStreet()) ? "" : model.getStreet();
        String building = TextUtils.isEmpty(model.getBuilding()) ? "" : model.getBuilding();
        return street + ", " + building;
    }
}
