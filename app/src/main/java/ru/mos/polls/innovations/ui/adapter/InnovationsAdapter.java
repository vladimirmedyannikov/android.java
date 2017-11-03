package ru.mos.polls.innovations.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.innovations.model.Innovation;
import ru.mos.polls.innovations.model.Status;
import ru.mos.polls.innovations.vm.item.InnovationsItemActiveVM;
import ru.mos.polls.innovations.vm.item.InnovationsItemOldVM;
import ru.mos.polls.innovations.vm.item.InnovationsItemPassedVM;

/**
 * Created by Trunks on 02.10.2017.
 */

public class InnovationsAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {
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

    public void updateInnovations(long id, double rating, long passedDate) {
        int position = 0;
        for (RecyclerBaseViewModel recyclerBaseViewModel : list) {
            if (id == ((Innovation) recyclerBaseViewModel.getModel()).getId()) {
                Innovation innovation = (Innovation) recyclerBaseViewModel.getModel();
                innovation.setStatus(Status.PASSED);
                innovation.setFullRating(rating);
                innovation.setPassedDate(passedDate);
                position = list.indexOf(recyclerBaseViewModel);
                RecyclerBaseViewModel inn = new InnovationsItemPassedVM(innovation);
                list.remove(recyclerBaseViewModel);
                list.add(position, inn);
                break;
            }
        }
        notifyItemChanged(position);
    }
}