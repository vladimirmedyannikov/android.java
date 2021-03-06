package ru.mos.polls.quests.adapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.quests.model.quest.AchievementQuest;
import ru.mos.polls.quests.model.quest.AdvertisementQuest;
import ru.mos.polls.quests.model.quest.BackQuest;
import ru.mos.polls.quests.model.quest.EventQuest;
import ru.mos.polls.quests.model.quest.FavoriteSurveysQuest;
import ru.mos.polls.quests.model.quest.NewsOssQuest;
import ru.mos.polls.quests.model.quest.NewsQuest;
import ru.mos.polls.quests.model.quest.NoveltyQuest;
import ru.mos.polls.quests.model.quest.OtherQuest;
import ru.mos.polls.quests.model.quest.ProfileQuest;
import ru.mos.polls.quests.model.quest.Quest;
import ru.mos.polls.quests.model.quest.RateAppQuest;
import ru.mos.polls.quests.model.quest.ResultsQuest;
import ru.mos.polls.quests.model.quest.SocialQuest;
import ru.mos.polls.quests.model.quest.SurveyOssQuest;
import ru.mos.polls.quests.vm.list.AchievementQuestVM;
import ru.mos.polls.quests.vm.list.AdvertisementQuestVM;
import ru.mos.polls.quests.vm.list.EventQuestVM;
import ru.mos.polls.quests.vm.list.FavouriteSurveysQuestVM;
import ru.mos.polls.quests.vm.list.NewsOssQuestVM;
import ru.mos.polls.quests.vm.list.NewsQuestVM;
import ru.mos.polls.quests.vm.list.NoveltyQuestsVM;
import ru.mos.polls.quests.vm.list.OtherQuestVM;
import ru.mos.polls.quests.vm.list.ProfileQuestVM;
import ru.mos.polls.quests.vm.list.RateAppQuestVM;
import ru.mos.polls.quests.vm.list.ResultsQuestVM;
import ru.mos.polls.quests.vm.list.SocialQuestVM;
import ru.mos.polls.quests.vm.list.SurveyOssQuestVM;

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
    public static final int NEWS_OSS = 11;
    public static final int SURVEY_OSS = 12;

    private static final Map<Class<? extends Quest>, Integer> CLASSES = new HashMap<Class<? extends Quest>, Integer>();

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
        CLASSES.put(NewsOssQuest.class, NEWS_OSS);
        CLASSES.put(SurveyOssQuest.class, SURVEY_OSS);
    }


    private List<Quest> quests;

    public QuestsItemAdapter(List<Quest> quests) {
        this.quests = quests;
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }

    public void removeItem(int position) {
        if (quests.size() > 0 && position >= 0) {
            quests.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final Quest quest = quests.get(position);
        final int result = CLASSES.get(quest.getClass());
        return result;
    }

    public void redrawViewHolder(int position) {
        if (list.get(position).getModel() != null) ((BackQuest) list.get(position).getModel()).setSwiped(true);
        ((BackQuest) quests.get(position)).setSwiped(true);
        notifyItemChanged(position);
    }

    @Override
    public void clear() {
        quests.clear();
        super.clear();
    }

    public void onCancelClick(String questId) {
        for (int i = 0; i < quests.size(); i++) {
            if (((BackQuest) quests.get(i)).getId().equals(questId)) {
                if (list.get(i).getModel() != null) ((BackQuest) list.get(i).getModel()).setSwiped(false);
                ((BackQuest) quests.get(i)).setSwiped(false);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void onJustClick(String questId) {

    }

    public void onDeleteClick(String questId) {
        for (int i = 0; i < quests.size(); i++) {
            if (((BackQuest) quests.get(i)).getId().equals(questId)) {
                if (list.get(i).getModel() != null) list.remove(i);
                quests.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void add(List<Quest> quests) {
        List<RecyclerBaseViewModel> data = new ArrayList<>();
        for (Quest iterQuest : quests) {
            RecyclerBaseViewModel model = null;
            switch (((BackQuest)iterQuest).getType()) {
                case AchievementQuest.TYPE:
                    model = new AchievementQuestVM((AchievementQuest) iterQuest);
                    break;
                case AdvertisementQuest.TYPE:
                    model = new AdvertisementQuestVM((AdvertisementQuest) iterQuest);
                    break;
                case EventQuest.TYPE:
                    model = new EventQuestVM((EventQuest) iterQuest);
                    break;
                case FavoriteSurveysQuest.ID_HEARING:
                    model = new FavouriteSurveysQuestVM((FavoriteSurveysQuest) iterQuest);
                    break;
                case FavoriteSurveysQuest.ID_POLL:
                    model = new FavouriteSurveysQuestVM((FavoriteSurveysQuest) iterQuest);
                    break;
                case NewsQuest.TYPE:
                    model = new NewsQuestVM((NewsQuest) iterQuest);
                    break;
                case NoveltyQuest.TYPE:
                    model = new NoveltyQuestsVM((NoveltyQuest) iterQuest);
                    break;
                case OtherQuest.TYPE:
                    model = new OtherQuestVM((OtherQuest) iterQuest);
                    break;
                case ProfileQuest.TYPE_PROFILE:
                    model = new ProfileQuestVM((ProfileQuest) iterQuest);
                    break;
                case ResultsQuest.TYPE:
                    model = new ResultsQuestVM((ResultsQuest) iterQuest);
                    break;
                case SocialQuest.TYPE_SOCIAL:
                    try {
                        //оценка приложения
                        model = new RateAppQuestVM((RateAppQuest) iterQuest);
                    } catch (ClassCastException e) {
                        //соц сеть
                        model = new SocialQuestVM((SocialQuest) iterQuest);
                    }
                    break;
                case SurveyOssQuest.TYPE:
                    model = new SurveyOssQuestVM((SurveyOssQuest) iterQuest);
                    break;
                case NewsOssQuest.TYPE:
                    model = new NewsOssQuestVM((NewsOssQuest) iterQuest);
                    break;
            }
            data.add(model);
        }
        addData(data);
    }
}
