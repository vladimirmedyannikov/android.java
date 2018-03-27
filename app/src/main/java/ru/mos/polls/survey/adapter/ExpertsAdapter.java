package ru.mos.polls.survey.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.vm.item.DetailsExpertVM;

public class ExpertsAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public void add(List<DetailsExpert> list) {
        List<RecyclerBaseViewModel> rbvm = new ArrayList<>();
        for (DetailsExpert news : list) {
            rbvm.add(new DetailsExpertVM(news));
        }
        addData(rbvm);
    }
}
