package ru.mos.polls.ourapps.vm.item;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.ItemOurAppBinding;
import ru.mos.polls.ourapps.model.OurApplication;

public class OurApplicationVM extends RecyclerBaseViewModel<OurApplication, ItemOurAppBinding> {

    public OurApplicationVM(OurApplication model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_our_app;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public void onBind(ItemOurAppBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(this);
        viewDataBinding.executePendingBindings();
        AGApplication.getImageLoader().displayImage(model.getLinkToIcon(), viewDataBinding.image);
        viewDataBinding.rootItem.setOnClickListener(view -> {
            AGApplication.bus().send(new Events.OurAppEvents(model.getLinkToStore()));
        });
    }

    public String getTitle() {
        return model.getTitle();
    }

    public String getBody() {
        return model.getBody();
    }
}
