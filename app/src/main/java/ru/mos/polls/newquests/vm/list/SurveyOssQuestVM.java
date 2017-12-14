package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestOssPollBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.SurveyOssQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 12.12.17.
 */

public class SurveyOssQuestVM extends QuestVM<SurveyOssQuest, QuestOssPollBinding> {

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
