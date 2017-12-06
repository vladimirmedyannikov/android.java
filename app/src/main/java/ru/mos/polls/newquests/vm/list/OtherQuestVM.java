package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.QuestOtherBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.OtherQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class OtherQuestVM extends RecyclerBaseViewModel<OtherQuest, QuestOtherBinding> {
    public OtherQuestVM(OtherQuest model, QuestOtherBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public OtherQuestVM(OtherQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_other;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.OTHER;
    }

    @Override
    public void onBind(QuestOtherBinding viewDataBinding) {
        super.onBind(viewDataBinding);
    }
}
