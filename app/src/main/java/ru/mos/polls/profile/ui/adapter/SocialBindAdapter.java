package ru.mos.polls.profile.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.profile.vm.SocialVM;
import ru.mos.polls.social.model.AppSocial;

public class SocialBindAdapter extends BaseRecyclerAdapter<SocialVM> {
    private Listener listener;

    public SocialBindAdapter(List<AppSocial> list, Listener listener) {
        this.listener = listener;
        add(list);
    }

    private void add(List<AppSocial> socials) {
        List<SocialVM> content = new ArrayList<>();
        for (AppSocial social : socials) {
            SocialVM addressesPropertyVM = new SocialVM(social, listener);
            content.add(addressesPropertyVM);
        }
        addData(content);
    }

    public void set(List<AppSocial> socials) {
        List<SocialVM> content = new ArrayList<>();
        for (AppSocial social : socials) {
            SocialVM addressesPropertyVM = new SocialVM(social, listener);
            content.add(addressesPropertyVM);
        }
        setData(content);
    }

    public interface Listener {
        void onBindClick(AppSocial social);

        void onCloseClick(AppSocial social);
    }
}
