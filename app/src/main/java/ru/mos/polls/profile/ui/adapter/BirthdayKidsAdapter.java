package ru.mos.polls.profile.ui.adapter;


import android.support.v4.app.FragmentManager;

import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.databinding.ItemBirthdayKidsBinding;
import ru.mos.polls.base.ui.BindingHolder;
import ru.mos.polls.base.ui.adapter.BaseAdapter;
import ru.mos.polls.profile.model.BirthdayKids;
import ru.mos.polls.profile.vm.BirthdayKidsVM;

/**
 * Created by Trunks on 07.07.2017.
 */

public class BirthdayKidsAdapter extends BaseAdapter<BirthdayKidsVM, BindingHolder<ItemBirthdayKidsBinding>, ItemBirthdayKidsBinding, BirthdayKids> {

    FragmentManager fr;

    public BirthdayKidsAdapter(List<BirthdayKids> list, FragmentManager fr) {
        this.list = list;
        this.fr = fr;
    }

    @Override
    public BindingHolder<ItemBirthdayKidsBinding> getVH(ItemBirthdayKidsBinding binding) {
        return new BindingHolder<>(binding);
    }

    @Override
    public BirthdayKidsVM getVM(BirthdayKids obj, ItemBirthdayKidsBinding binding) {
        return new BirthdayKidsVM(obj, binding, fr);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.item_birthday_kids;
    }
}
