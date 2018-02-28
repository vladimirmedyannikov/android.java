package ru.mos.polls.quests.vm.list;

import android.view.View;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.QuestSocialBinding;
import ru.mos.polls.quests.adapter.QuestsItemAdapter;
import ru.mos.polls.quests.model.quest.RateAppQuest;


public class RateAppQuestVM extends QuestVM<RateAppQuest, QuestSocialBinding> {
    public RateAppQuestVM(RateAppQuest model, QuestSocialBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public RateAppQuestVM(RateAppQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_social;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.RATE_APP;
    }


    @Override
    public void onBind(QuestSocialBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.title.setText(model.getTitle());
        viewDataBinding.icon.setImageResource(model.getIcon());
        viewDataBinding.details.setVisibility(View.GONE);
        viewDataBinding.contprice.rrPrice.setVisibility(View.GONE);
    }
}
