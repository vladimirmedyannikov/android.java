package ru.mos.polls.newinnovation.vm.item;


import android.view.View;

import ru.mos.polls.R;
import ru.mos.polls.databinding.ItemInnovationPassedBinding;
import ru.mos.polls.newinnovation.model.Innovation;
import ru.mos.polls.newinnovation.ui.adapter.InnovationsAdapter;

/**
 * Created by Trunks on 03.10.2017.
 */

public class InnovationsItemPassedVM extends InnovationsItemBaseVM<Innovation, ItemInnovationPassedBinding> {
    public InnovationsItemPassedVM(Innovation model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_innovation_passed;
    }

    @Override
    public int getViewType() {
        return InnovationsAdapter.Type.ITEM_PASSED;
    }


    @Override
    public void onBind(ItemInnovationPassedBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        UIInnovationViewHelper.setPassedTitleAndRating(getModel(), viewDataBinding.title, viewDataBinding.ratingbarLayout.rating);
        if (getModel().getPoints() > 0) {
            viewDataBinding.creditedPoints.setVisibility(View.VISIBLE);
            viewDataBinding.voteDateTxt.setVisibility(View.GONE);
            viewDataBinding.creditedPoints.setText(UIInnovationViewHelper.getCreditedAndPassedDateTxt(getModel(), getModel().getPoints(), viewDataBinding.creditedPoints.getContext()));
        } else {
            viewDataBinding.creditedPoints.setVisibility(View.GONE);
            viewDataBinding.voteDateTxt.setVisibility(View.VISIBLE);
            viewDataBinding.voteDateTxt.setText(String.format(viewDataBinding.title.getContext().getString(R.string.vote_date_text), UIInnovationViewHelper.getReadablePassedDate(getModel().getPassedDate())));
        }
    }

}
