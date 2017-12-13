package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.QuestSocialBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.RateAppQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class RateAppQuestVM extends RecyclerBaseViewModel<RateAppQuest, QuestSocialBinding> {
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
}
