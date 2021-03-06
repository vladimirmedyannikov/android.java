package ru.mos.polls.sourcesvoting.vm.item;

import android.databinding.Bindable;
import android.widget.CompoundButton;

import ru.mos.polls.AGApplication;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.ItemSourcesVotingBinding;
import ru.mos.polls.sourcesvoting.model.SourcesVoting;

/**
 * Created by Trunks on 13.10.2017.
 */

public class SourcesVotingVM extends RecyclerBaseViewModel<SourcesVoting, ItemSourcesVotingBinding> {

    public SourcesVotingVM(SourcesVoting model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_sources_voting;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public void onBind(ItemSourcesVotingBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(this);
        viewDataBinding.executePendingBindings();
    }

    public boolean isEditable() {
        return model.isEditable();
    }

    @Bindable
    public boolean isEnable() {
        return model.isEnable();
    }

    public void setEnable(boolean enable) {
        model.setEnable(enable);
        getViewDataBinding().notifyPropertyChanged(BR.enable);
    }

    public String getTitle() {
        return getModel().getTitle();
    }

    public void onCheckedChanged(CompoundButton v, boolean checked) {
        if (v.isPressed()) {
            setEnable(checked);
            AGApplication.bus().send(new Events.SourcesVotingEvents(model.getId(), checked));
        }
    }
}
