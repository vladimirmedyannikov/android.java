package ru.mos.polls.newinnovation.vm.item;

import ru.mos.polls.R;
import ru.mos.polls.databinding.ItemInnovationActiveBinding;
import ru.mos.polls.newinnovation.model.Innovation;
import ru.mos.polls.newinnovation.ui.adapter.InnovationsAdapter;

/**
 * Created by Trunks on 02.10.2017.
 */

public class InnovationsItemActiveVM extends InnovationsItemBaseVM<Innovation, ItemInnovationActiveBinding> {
    public InnovationsItemActiveVM(Innovation model) {
        super(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_innovation_active;
    }

    @Override
    public int getViewType() {
        return InnovationsAdapter.Type.ITEM_ACTIVE;
    }

    @Override
    public void onBind(ItemInnovationActiveBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        UIInnovationViewHelper.setPassedTitleAndRating(getModel(), viewDataBinding.title, viewDataBinding.innovationRatingbarLayout.rating);
        if (getModel().getPoints() > 0) {
            viewDataBinding.title.setText(UIInnovationViewHelper.getRatingTitleTxt(getModel().getPoints(), viewDataBinding.title.getContext()));
        } else {
            viewDataBinding.title.setText(viewDataBinding.title.getContext().getString(R.string.innovation_rate_your));
        }
    }
}
