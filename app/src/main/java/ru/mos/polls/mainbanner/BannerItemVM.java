package ru.mos.polls.mainbanner;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemMainBannerBinding;


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

        DecimalFormat formatter = new DecimalFormat();
        formatter.applyPattern("###,###,###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        String formattedString = formatter.format(model.value);
        viewDataBinding.bannerItemValue.setText(formattedString);
    }
}
