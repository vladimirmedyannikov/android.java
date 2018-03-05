package ru.mos.polls.profile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.profile.vm.SocialVM;
import ru.mos.polls.social.model.AppSocial;

public class SocialBindAdapter extends BaseRecyclerAdapter<SocialVM> {

    public SocialBindAdapter(List<AppSocial> list) {
        add(list);
    }

    public void add(List<AppSocial> socials) {
        List<SocialVM> content = new ArrayList<>();
        for (AppSocial social : socials) {
            SocialVM addressesPropertyVM = new SocialVM(social);
            content.add(addressesPropertyVM);
        }
        addData(content);
    }
}
