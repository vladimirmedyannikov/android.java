package ru.mos.polls.newquests.vm.list;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.QuestHtmlBannerBinding;
import ru.mos.polls.newquests.model.quest.AdvertisementQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class AdvertisementQuestVM extends RecyclerBaseViewModel<AdvertisementQuest, QuestHtmlBannerBinding> {
    public AdvertisementQuestVM(AdvertisementQuest model, QuestHtmlBannerBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public AdvertisementQuestVM(AdvertisementQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_html_banner;
    }

    @Override
    public int getViewType() {
        return 0;// TODO: 06.12.17 ???
    }
}
