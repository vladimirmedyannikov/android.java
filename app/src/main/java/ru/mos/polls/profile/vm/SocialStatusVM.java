package ru.mos.polls.profile.vm;


import ru.mos.polls.base.vm.BaseVM;
import ru.mos.polls.databinding.ItemAgSocialStatusBinding;
import ru.mos.polls.profile.model.AgSocialStatus;

public class SocialStatusVM extends BaseVM<AgSocialStatus, ItemAgSocialStatusBinding> {

    public SocialStatusVM(AgSocialStatus agSocialStatus, ItemAgSocialStatusBinding binding) {
        super(agSocialStatus, binding);
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
