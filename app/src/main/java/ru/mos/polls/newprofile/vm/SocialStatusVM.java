package ru.mos.polls.newprofile.vm;

import android.databinding.BaseObservable;

import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.polls.databinding.ItemAgSocialStatusBinding;

/**
 * Created by Trunks on 07.07.2017.
 */

public class SocialStatusVM extends BaseObservable {
    AgSocialStatus agSocialStatus;
    ItemAgSocialStatusBinding binding;

    public SocialStatusVM(AgSocialStatus agSocialStatus, ItemAgSocialStatusBinding binding) {
        this.agSocialStatus = agSocialStatus;
        this.binding = binding;
    }

    public String getTitle() {
        return agSocialStatus.getTitle();
    }

    public int getId() {
        return agSocialStatus.getId();
    }

    public AgSocialStatus getAgSocialStatus() {
        return agSocialStatus;
    }
}
