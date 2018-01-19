package ru.mos.polls.mainbanner;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemMainBannerBinding;

/**
 * Created by Trunks on 18.01.2018.
 */

public class BannerItemVM extends RecyclerBaseViewModel<BannerItem, ItemMainBannerBinding> {
    public BannerItemVM(BannerItem model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_main_banner;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public void onBind(ItemMainBannerBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.bannerItemImg.setImageResource(model.image);
        viewDataBinding.bannerItemTitle.setText(model.title);
        viewDataBinding.bannerItemValue.setText(String.valueOf(model.value));
    }
}
