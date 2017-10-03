package ru.mos.polls.newinnovation.vm.item;

import android.databinding.ViewDataBinding;
import android.view.View;

import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.innovation.gui.activity.InnovationActivity;
import ru.mos.polls.newinnovation.model.Innovation;

/**
 * Created by Trunks on 03.10.2017.
 */

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
        InnovationActivity.startActivity(v.getContext(), getModel().getId());
    }
}
