package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestSocialBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.SocialQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class SocialQuestVM extends PriceQuestVM<SocialQuest, QuestSocialBinding> {
    public SocialQuestVM(SocialQuest model, QuestSocialBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public SocialQuestVM(SocialQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_social;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.SOCIAL;
    }

    @Override
    public void onBind(QuestSocialBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.icon.setImageResource(model.getIcon());
        viewDataBinding.details.setText(model.getDetails());
    }
}
