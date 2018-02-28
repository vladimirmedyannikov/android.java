package ru.mos.polls.innovations.vm.item;

import android.databinding.ViewDataBinding;
import android.view.View;

import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.innovations.model.Innovation;

public abstract class InnovationsItemBaseVM<M extends Innovation, VDB extends ViewDataBinding> extends RecyclerBaseViewModel<M, VDB> {
    public InnovationsItemBaseVM(M model) {
        super(model);
    }

    @Override
    public void onBind(VDB viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.getRoot().setOnClickListener((v -> openInnovations(v)));
    }

    public void openInnovations(View v) {
        Statistics.innovationsDetail();
        GoogleStatistics.Innovation.innovationsDetail();
        AGApplication.bus().send(new Events.InnovationsEvents(getModel().getId(), Events.InnovationsEvents.OPEN_INNOVATIONS));
    }
}
