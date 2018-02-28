package ru.mos.polls.quests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestOssPollBinding;
import ru.mos.polls.quests.adapter.QuestsItemAdapter;
import ru.mos.polls.quests.model.quest.SurveyOssQuest;


public class SurveyOssQuestVM extends PriceQuestVM<SurveyOssQuest, QuestOssPollBinding> {

    public SurveyOssQuestVM(SurveyOssQuest model, QuestOssPollBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public SurveyOssQuestVM(SurveyOssQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_oss_poll;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.SURVEY_OSS;
    }

    @Override
    public void onBind(QuestOssPollBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        questTitle.setText(model.getTitle());
    }
}
