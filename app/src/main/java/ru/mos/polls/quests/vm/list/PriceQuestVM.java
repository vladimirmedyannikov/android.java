package ru.mos.polls.quests.vm.list;

import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.quests.model.quest.BackQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 07.12.17.
 */

public abstract class PriceQuestVM<M extends BackQuest, VDB extends ViewDataBinding> extends QuestVM<M, VDB> {
    @Nullable
    @BindView(R.id.points)
    public TextView priceTextView;
    @Nullable
    @BindView(R.id.points_title)
    public TextView priceTitleTextView;

    public PriceQuestVM(M model, VDB viewDataBinding) {
        super(model, viewDataBinding);
    }

    public PriceQuestVM(M model) {
        super(model);
    }

    @Override
    public void onBind(VDB viewDataBinding) {
        super.onBind(viewDataBinding);
        String title = model.getTitle();
        questTitle.setText(title);
        String pointsString = model.processPoints(model.getPoints());
        int points = model.getPoints();
        int visibility = View.GONE;
        if (points > 0) {
            String price = String.format(pointsString, points);
            priceTextView.setText(price);
            String pointsUnitStirng = PointsManager.getPointUnitString(view.getContext(), points);
            priceTitleTextView.setText(pointsUnitStirng);
            visibility = View.VISIBLE;
        }
        priceTitleTextView.setVisibility(visibility);
        priceTextView.setVisibility(visibility);
    }
}
