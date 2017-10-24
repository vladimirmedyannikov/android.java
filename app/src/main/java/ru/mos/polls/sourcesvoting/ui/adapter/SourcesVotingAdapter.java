package ru.mos.polls.sourcesvoting.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.sourcesvoting.model.SourcesVoting;
import ru.mos.polls.sourcesvoting.vm.item.SourcesVotingVM;

/**
 * Created by Trunks on 13.10.2017.
 */

public class SourcesVotingAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public void add(List<SourcesVoting> list) {
        List<RecyclerBaseViewModel> rbvm = new ArrayList<>();
        for (SourcesVoting sourcesVoting : list) {
            rbvm.add(new SourcesVotingVM(sourcesVoting));
        }
        addData(rbvm);
    }
}
