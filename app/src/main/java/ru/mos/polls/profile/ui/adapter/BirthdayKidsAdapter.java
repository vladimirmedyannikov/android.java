package ru.mos.polls.profile.ui.adapter;


import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.profile.model.BirthdayKids;
import ru.mos.polls.profile.vm.BirthdayKidsVM;

public class BirthdayKidsAdapter extends BaseRecyclerAdapter<BirthdayKidsVM> {

    private FragmentManager fr;

    public BirthdayKidsAdapter(List<BirthdayKids> list, FragmentManager fr) {
        this.fr = fr;
        add(list);
    }

    public void add(List<BirthdayKids> kids) {
        List<BirthdayKidsVM> content = new ArrayList<>();
        for (BirthdayKids kid : kids) {
            BirthdayKidsVM addressesPropertyVM = new BirthdayKidsVM(kid, fr);
            content.add(addressesPropertyVM);
        }
        addData(content);
    }
}
