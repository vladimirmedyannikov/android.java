package ru.mos.polls.quests.view.questviewholder;


import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import ru.mos.polls.R;
import ru.mos.polls.quests.quest.BackQuest;

public class ProfileHolder extends PriceQuestHolder {
    @BindView(R.id.details)
    TextView detailsTextView;

    public ProfileHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(BackQuest quest) {
        super.setDataOnView(quest);
        /**
         * т.к. используется linearLayout, то если нет текста, скрываем вьюху, чтобы Title был по центру (по вертикали)
         */
        if (TextUtils.isEmpty(quest.getDetails())) detailsTextView.setVisibility(View.GONE);
        detailsTextView.setText(quest.getDetails());
    }
}
