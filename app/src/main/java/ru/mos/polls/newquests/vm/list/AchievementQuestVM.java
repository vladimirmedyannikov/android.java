package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.QuestAchievementBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.AchievementQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class AchievementQuestVM extends RecyclerBaseViewModel<AchievementQuest, QuestAchievementBinding> {
    public AchievementQuestVM(AchievementQuest model, QuestAchievementBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public AchievementQuestVM(AchievementQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_achievement;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.ACHIEVEMENT;
    }
}
