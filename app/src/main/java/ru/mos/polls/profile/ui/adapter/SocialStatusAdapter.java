package ru.mos.polls.profile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.profile.model.AgSocialStatus;
import ru.mos.polls.profile.vm.OnSocialStatusItemClick;
import ru.mos.polls.profile.vm.SocialStatusVM;

public class SocialStatusAdapter extends BaseRecyclerAdapter<SocialStatusVM>/*BaseAdapter<SocialStatusVM, BindingHolder<ItemAgSocialStatusBinding>, ItemAgSocialStatusBinding, AgSocialStatus>*/ {

    private OnSocialStatusItemClick listener;

    public SocialStatusAdapter(List<AgSocialStatus> list, OnSocialStatusItemClick listener) {
        this.listener = listener;
        add(list);
    }

    public void add(List<AgSocialStatus> list) {
        List<SocialStatusVM> content = new ArrayList<>();
        for (AgSocialStatus item : list) {
            SocialStatusVM socialStatusesVM = new SocialStatusVM(item, listener);
            content.add(socialStatusesVM);
        }
        addData(content);
    }
}
