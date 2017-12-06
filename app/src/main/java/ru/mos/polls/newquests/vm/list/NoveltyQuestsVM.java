package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.QuestNoveltyBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.NoveltyQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class NoveltyQuestsVM extends RecyclerBaseViewModel<NoveltyQuest, QuestNoveltyBinding> {
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
