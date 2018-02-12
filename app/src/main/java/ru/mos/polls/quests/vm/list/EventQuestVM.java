package ru.mos.polls.quests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestEventBinding;
import ru.mos.polls.quests.adapter.QuestsItemAdapter;
import ru.mos.polls.quests.model.quest.EventQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class EventQuestVM extends PriceQuestVM<EventQuest, QuestEventBinding> {

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
