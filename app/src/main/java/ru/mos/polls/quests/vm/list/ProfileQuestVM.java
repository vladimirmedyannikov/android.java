package ru.mos.polls.quests.vm.list;

import android.text.TextUtils;
import android.view.View;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestProfileBinding;
import ru.mos.polls.quests.adapter.QuestsItemAdapter;
import ru.mos.polls.quests.model.quest.ProfileQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class ProfileQuestVM extends PriceQuestVM<ProfileQuest, QuestProfileBinding> {
    public ProfileQuestVM(ProfileQuest model, QuestProfileBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public ProfileQuestVM(ProfileQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_profile;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.PROFILE;
    }

    @Override
    public void onBind(QuestProfileBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        /**
         * т.к. используется linearLayout, то если нет текста, скрываем вьюху, чтобы Title был по центру (по вертикали)
         */
        if (TextUtils.isEmpty(model.getDetails())) viewDataBinding.details.setVisibility(View.GONE);
        viewDataBinding.details.setText(model.getDetails());
    }
}
