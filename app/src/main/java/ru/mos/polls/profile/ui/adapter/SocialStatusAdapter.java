package ru.mos.polls.profile.ui.adapter;

import java.util.List;

import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.polls.R;
import ru.mos.polls.databinding.ItemAgSocialStatusBinding;
import ru.mos.polls.base.ui.BindingHolder;
import ru.mos.polls.base.ui.adapter.BaseAdapter;
import ru.mos.polls.profile.vm.OnSocialStatusItemClick;
import ru.mos.polls.profile.vm.SocialStatusVM;

/**
 * Created by Trunks on 07.07.2017.
 */

public class SocialStatusAdapter extends BaseAdapter<SocialStatusVM, BindingHolder<ItemAgSocialStatusBinding>, ItemAgSocialStatusBinding, AgSocialStatus> {

    OnSocialStatusItemClick listener;

    public SocialStatusAdapter(List<AgSocialStatus> list, OnSocialStatusItemClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public BindingHolder<ItemAgSocialStatusBinding> getVH(ItemAgSocialStatusBinding binding) {
        return new BindingHolder<>(binding);
    }

    @Override
    public SocialStatusVM getVM(AgSocialStatus obj, ItemAgSocialStatusBinding binding) {
        binding.setClicklistener(listener);
        return new SocialStatusVM(obj, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.item_ag_social_status;
    }
}
