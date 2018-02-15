package ru.mos.polls.quests.vm.list;

import android.view.View;

import ru.mos.elk.ElkTextUtils;
import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestAchievementBinding;
import ru.mos.polls.quests.adapter.QuestsItemAdapter;
import ru.mos.polls.quests.model.quest.AchievementQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class AchievementQuestVM extends QuestVM<AchievementQuest, QuestAchievementBinding> {
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

    @Override
    public void onBind(QuestAchievementBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        questTitle.setText(model.getTitle());
        viewDataBinding.details.setText(model.getDetails());
        if (ElkTextUtils.isEmpty(model.getDetails())) {
            viewDataBinding.details.setVisibility(View.GONE);
        }
    }
}
