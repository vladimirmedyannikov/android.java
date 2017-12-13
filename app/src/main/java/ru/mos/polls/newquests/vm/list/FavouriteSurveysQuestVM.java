package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.databinding.QuestFavoriteSurveyBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.FavoriteSurveysQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class FavouriteSurveysQuestVM extends PriceQuestVM<FavoriteSurveysQuest, QuestFavoriteSurveyBinding> {
    public static final String ID_HEARING = "hearing";

    public FavouriteSurveysQuestVM(FavoriteSurveysQuest model, QuestFavoriteSurveyBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public FavouriteSurveysQuestVM(FavoriteSurveysQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_favorite_survey;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.FAVORITE_SURVEYS;
    }

    @Override
    public void onBind(QuestFavoriteSurveyBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        if (ID_HEARING.equalsIgnoreCase(model.getType())) {
            questTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hearing, 0, 0, 0);
        }
    }
}
