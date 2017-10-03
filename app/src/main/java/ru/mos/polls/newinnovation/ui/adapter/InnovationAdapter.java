package ru.mos.polls.newinnovation.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.newinnovation.model.Innovation;
import ru.mos.polls.newinnovation.model.Status;
import ru.mos.polls.newinnovation.vm.item.InnovationsItemActiveVM;
import ru.mos.polls.newinnovation.vm.item.InnovationsItemOldVM;
import ru.mos.polls.newinnovation.vm.item.InnovationsItemPassedVM;

/**
 * Created by Trunks on 02.10.2017.
 */

public class InnovationAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {
    public interface Type {
        int ITEM_ACTIVE = 0;
        int ITEM_OLD = 1;
        int ITEM_PASSED = 2;
    }

    public void add(List<Innovation> innovations) {
        List<RecyclerBaseViewModel> content = new ArrayList<>();
        RecyclerBaseViewModel inn = null;
        for (Innovation innovation : innovations) {
            if (innovation.getStatus() == Status.ACTIVE) {
                inn = new InnovationsItemActiveVM(innovation);
            }
            if (innovation.getStatus() == Status.OLD) {
                inn = new InnovationsItemOldVM(innovation);
            }
            if (innovation.getStatus() == Status.PASSED) {
                inn = new InnovationsItemPassedVM(innovation);
            }
            content.add(inn);
        }
        addData(content);
    }
}