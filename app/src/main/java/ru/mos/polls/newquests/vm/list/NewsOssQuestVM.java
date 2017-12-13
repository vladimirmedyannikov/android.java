package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestOssNewsBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.NewsOssQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 12.12.17.
 */

public class NewsOssQuestVM extends QuestVM<NewsOssQuest, QuestOssNewsBinding> {
    public NewsOssQuestVM(NewsOssQuest model, QuestOssNewsBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public NewsOssQuestVM(NewsOssQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_oss_news;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.NEWS_OSS;
    }

    @Override
    public void onBind(QuestOssNewsBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        questTitle.setText(model.getTitle());
    }
}