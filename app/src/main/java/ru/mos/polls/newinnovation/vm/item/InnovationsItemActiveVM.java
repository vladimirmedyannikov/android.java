package ru.mos.polls.newinnovation.vm.item;

import android.content.Context;
import android.widget.RatingBar;
import android.widget.TextView;

import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemInnovationActiveBinding;
import ru.mos.polls.newinnovation.model.Innovation;
import ru.mos.polls.newinnovation.ui.adapter.InnovationAdapter;

/**
 * Created by Trunks on 02.10.2017.
 */

public class InnovationsItemActiveVM extends RecyclerBaseViewModel<Innovation, ItemInnovationActiveBinding> {
    public InnovationsItemActiveVM(Innovation model) {
        super(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_innovation_active;
    }

    @Override
    public int getViewType() {
        return InnovationAdapter.Type.ITEM_ACTIVE;
    }

    private void setPassedTitleAndRating(Innovation shortInnovation, TextView title, RatingBar ratingBar) {
        title.setText(shortInnovation.getTitle());
        ratingBar.setRating((float) shortInnovation.getFullRating());
        ratingBar.setIsIndicator(true);
    }

    @Override
    public void onBind(ItemInnovationActiveBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        if (getModel().getPoints() > 0) {
            viewDataBinding.title.setText(getRatingTitleTxt(getModel().getPoints(), viewDataBinding.title.getContext()));
        } else {
            viewDataBinding.title.setText(viewDataBinding.title.getContext().getString(R.string.innovation_rate_your));
        }
    }

    private String getRatingTitleTxt(int pointsValue, Context context) {
        String pointTxt = PointsManager.getPointUnitString(context, pointsValue);
        return String.format(context.getString(R.string.active_points_formatted), pointsValue, pointTxt);
    }
}
