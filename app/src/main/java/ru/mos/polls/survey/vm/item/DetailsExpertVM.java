package ru.mos.polls.survey.vm.item;

import android.widget.TextView;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemExpertBinding;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.summary.ExpertsView;

public class DetailsExpertVM extends RecyclerBaseViewModel<DetailsExpert, ItemExpertBinding> {

    public DetailsExpertVM(DetailsExpert model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_expert;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public void onBind(ItemExpertBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        ((TextView)getViewDataBinding().mainRoot.findViewById(R.id.expertFio)).setText(getModel().getTitle());
        ((TextView)getViewDataBinding().mainRoot.findViewById(R.id.expertDescription)).setText(getModel().getDescription());
        getViewDataBinding().body.setText(getModel().getBody());
        ExpertsView.loadAvatar(getViewDataBinding().mainRoot.getContext(),
                getViewDataBinding().mainRoot.findViewById(R.id.expertAvatar),
                getViewDataBinding().mainRoot.findViewById(R.id.loadingProgress),
                getModel());
    }
}