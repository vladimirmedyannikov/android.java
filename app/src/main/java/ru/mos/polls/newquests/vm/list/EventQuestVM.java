package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.QuestEventBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.EventQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class EventQuestVM extends RecyclerBaseViewModel<EventQuest, QuestEventBinding> {

    public EventQuestVM(EventQuest model, QuestEventBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public EventQuestVM(EventQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_event;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.EVENT;
    }
}
