package ru.mos.polls.quests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestNewsBinding;
import ru.mos.polls.quests.adapter.QuestsItemAdapter;
import ru.mos.polls.quests.model.quest.NewsQuest;


public class NewsQuestVM extends QuestVM<NewsQuest, QuestNewsBinding> {
    public NewsQuestVM(NewsQuest model, QuestNewsBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public NewsQuestVM(NewsQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_news;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.NEWS;
    }

    @Override
    public void onBind(QuestNewsBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        questTitle.setText(model.getTitle());
    }
}
