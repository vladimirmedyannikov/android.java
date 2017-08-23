package ru.mos.polls.newprofile.ui.adapter;

import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.databinding.ItemBindSocialNewBinding;
import ru.mos.polls.base.ui.BindingHolder;
import ru.mos.polls.base.ui.adapter.BaseAdapter;
import ru.mos.polls.newprofile.vm.SocialVM;
import ru.mos.polls.social.model.Social;

/**
 * Created by Trunks on 11.07.2017.
 */

public class SocialBindAdapter extends BaseAdapter<SocialVM, BindingHolder<ItemBindSocialNewBinding>, ItemBindSocialNewBinding, Social> {

    public SocialBindAdapter(List<Social> list) {
        this.list = list;
    }

    @Override
    public BindingHolder<ItemBindSocialNewBinding> getVH(ItemBindSocialNewBinding binding) {
        return new BindingHolder<>(binding);
    }

    @Override
    public SocialVM getVM(Social obj, ItemBindSocialNewBinding binding) {
        return new SocialVM(obj, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.item_bind_social_new;
    }
}
