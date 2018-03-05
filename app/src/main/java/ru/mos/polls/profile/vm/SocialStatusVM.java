package ru.mos.polls.profile.vm;


import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemAgSocialStatusBinding;
import ru.mos.polls.profile.model.AgSocialStatus;

public class SocialStatusVM extends RecyclerBaseViewModel<AgSocialStatus, ItemAgSocialStatusBinding>/*BaseVM<AgSocialStatus, ItemAgSocialStatusBinding>*/ {

    private OnSocialStatusItemClick listener;

    public SocialStatusVM(AgSocialStatus agSocialStatus, OnSocialStatusItemClick listener) {
        super(agSocialStatus);
        this.listener = listener;
    }

    @Override
    public void onBind(ItemAgSocialStatusBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(this);
        viewDataBinding.executePendingBindings();
        viewDataBinding.setClicklistener(listener);
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_ag_social_status;
    }

    public String getTitle() {
        return model.getTitle();
    }

    public int getId() {
        return model.getId();
    }

    public AgSocialStatus getAgSocialStatus() {
        return model;
    }

}
