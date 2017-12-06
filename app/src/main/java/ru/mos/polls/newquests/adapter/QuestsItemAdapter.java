package ru.mos.polls.newquests.adapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.newquests.model.quest.AchievementQuest;
import ru.mos.polls.newquests.model.quest.AdvertisementQuest;
import ru.mos.polls.newquests.model.quest.EventQuest;
import ru.mos.polls.newquests.model.quest.FavoriteSurveysQuest;
import ru.mos.polls.newquests.model.quest.NewsQuest;
import ru.mos.polls.newquests.model.quest.NoveltyQuest;
import ru.mos.polls.newquests.model.quest.OtherQuest;
import ru.mos.polls.newquests.model.quest.ProfileQuest;
import ru.mos.polls.newquests.model.quest.Quest;
import ru.mos.polls.newquests.model.quest.RateAppQuest;
import ru.mos.polls.newquests.model.quest.ResultsQuest;
import ru.mos.polls.newquests.model.quest.SocialQuest;
import ru.mos.polls.quests.QuestsFragment;

public class QuestsItemAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public static final int PROFILE = 0;
    public static final int SOCIAL = 1;
    public static final int FAVORITE_SURVEYS = 2;
    public static final int EVENT = 3;
    public static final int NEWS = 4;
    public static final int RESULTS = 5;
    public static final int OTHER = 6;
    public static final int BANNER = 7;
    public static final int RATE_APP = 8;
    public static final int NOVELTY = 9;
    public static final int ACHIEVEMENT = 10;

    private static final Map<Class<? extends Quest>, Integer> CLASSES = new HashMap<Class<? extends Quest>, Integer>();

    private List<String> questForRemoveList = new ArrayList<>();

    static {
        CLASSES.put(ProfileQuest.class, PROFILE);
        CLASSES.put(SocialQuest.class, SOCIAL);
        CLASSES.put(FavoriteSurveysQuest.class, FAVORITE_SURVEYS);
        CLASSES.put(EventQuest.class, EVENT);
        CLASSES.put(NewsQuest.class, NEWS);
        CLASSES.put(ResultsQuest.class, RESULTS);
        CLASSES.put(OtherQuest.class, OTHER);
        CLASSES.put(AdvertisementQuest.class, BANNER);
        CLASSES.put(RateAppQuest.class, RATE_APP);
        CLASSES.put(NoveltyQuest.class, NOVELTY);
        CLASSES.put(AchievementQuest.class, ACHIEVEMENT);
    }


    private List<Quest> quests;
    private QuestsFragment.ItemRecyclerViewListener listener;

    public QuestsItemAdapter(List<Quest> quests, QuestsFragment.ItemRecyclerViewListener listener) {
        this.quests = quests;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }
}
