package ru.mos.polls.ourapps.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.ourapps.model.OurApplication;
import ru.mos.polls.ourapps.vm.item.OurApplicationVM;


public class OurAppsAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public void add(List<OurApplication> list) {
        List<RecyclerBaseViewModel> rbvm = new ArrayList<>();
        for (OurApplication ourApp : list) {
            rbvm.add(new OurApplicationVM(ourApp));
        }
        addData(rbvm);
    }
}
