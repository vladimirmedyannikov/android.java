package ru.mos.polls.quests.view.questviewholder;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.quests.quest.BackQuest;


public class PriceQuestHolder extends QuestsViewHolder {
    @BindView(R.id.points)
    public TextView priceTextView;
    @BindView(R.id.points_title)
    public TextView priceTitleTextView;

    public PriceQuestHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        String title = quest.getTitle();
        questTitle.setText(title);
        String pointsString = quest.processPoints(quest.getPoints());
        int points = quest.getPoints();
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
