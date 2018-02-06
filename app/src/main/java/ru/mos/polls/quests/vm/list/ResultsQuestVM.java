package ru.mos.polls.quests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestResultsBinding;
import ru.mos.polls.quests.adapter.QuestsItemAdapter;
import ru.mos.polls.quests.model.quest.ResultsQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 08.12.17.
 */

public class ResultsQuestVM extends QuestVM<ResultsQuest, QuestResultsBinding> {
    public ResultsQuestVM(ResultsQuest model, QuestResultsBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public ResultsQuestVM(ResultsQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_results;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.RESULTS;
    }

    @Override
    public void onBind(QuestResultsBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        questTitle.setText(model.getTitle());
    }
}
