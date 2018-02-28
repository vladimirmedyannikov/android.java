package ru.mos.polls.quests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestNoveltyBinding;
import ru.mos.polls.quests.adapter.QuestsItemAdapter;
import ru.mos.polls.quests.model.quest.NoveltyQuest;


public class NoveltyQuestsVM extends PriceQuestVM<NoveltyQuest, QuestNoveltyBinding> {
    public NoveltyQuestsVM(NoveltyQuest model, QuestNoveltyBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public NoveltyQuestsVM(NoveltyQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_novelty;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.NOVELTY;
    }
}
