package ru.mos.polls.quests;

import org.json.JSONObject;

import ru.mos.polls.quests.quest.AchievementQuest;
import ru.mos.polls.quests.quest.AdvertisementQuest;
import ru.mos.polls.quests.quest.EventQuest;
import ru.mos.polls.quests.quest.FavoriteSurveysQuest;
import ru.mos.polls.quests.quest.NewsQuest;
import ru.mos.polls.quests.quest.NoveltyQuest;
import ru.mos.polls.quests.quest.OssNewsQuest;
import ru.mos.polls.quests.quest.OssPollQuest;
import ru.mos.polls.quests.quest.OtherQuest;
import ru.mos.polls.quests.quest.ProfileQuest;
import ru.mos.polls.quests.quest.Quest;
import ru.mos.polls.quests.quest.RateAppQuest;
import ru.mos.polls.quests.quest.ResultsQuest;
import ru.mos.polls.quests.quest.SocialQuest;

public class QuestFactory {

    private static final String ID = "id";
    public static final String TYPE = "type";

    /**
     * Парсинг данных для главной ленты
     *
     * @param innerId
     * @param jsonObject
     * @return
     */
    public static Quest parse(long innerId, JSONObject jsonObject) {
        Quest result;
        final String type = jsonObject.optString(TYPE);
        if (ProfileQuest.TYPE_PROFILE.equals(type)) {
            result = new ProfileQuest(innerId, jsonObject);
        } else if (SocialQuest.TYPE_SOCIAL.equals(type)) {
            final String id = jsonObject.optString(ID);
            result = new SocialQuest(innerId, jsonObject);
            if (SocialQuest.ID_RATE_THIS_APP.equals(id)) {
                result = new RateAppQuest(innerId, jsonObject);
            }
        } else if (FavoriteSurveysQuest.ID_POLL.equals(type) || FavoriteSurveysQuest.ID_HEARING.equals(type)) {
            result = new FavoriteSurveysQuest(innerId, jsonObject);
        } else if (EventQuest.TYPE.equals(type)) {
            result = new EventQuest(innerId, jsonObject);
        } else if (NewsQuest.TYPE.equalsIgnoreCase(type)) {
            result = new NewsQuest(innerId, jsonObject);
        } else if (OtherQuest.TYPE.equalsIgnoreCase(type)) {
            result = new OtherQuest(innerId, jsonObject);
        } else if (ResultsQuest.TYPE.equalsIgnoreCase(type)) {
            result = new ResultsQuest(innerId, jsonObject);
        } else if (AdvertisementQuest.TYPE.equalsIgnoreCase(type)) {
            result = new AdvertisementQuest(innerId, jsonObject);
        } else if (NoveltyQuest.TYPE.equalsIgnoreCase(type)) {
            result = new NoveltyQuest(innerId, jsonObject);
        } else if (AchievementQuest.TYPE.equalsIgnoreCase(type)) {
            result = new AchievementQuest(innerId, jsonObject);
        } else if (OssPollQuest.TYPE.equalsIgnoreCase(type)) {
            result = new OssPollQuest(innerId, jsonObject);
        } else if (OssNewsQuest.TYPE.equalsIgnoreCase(type)) {
            result = new OssNewsQuest(innerId, jsonObject);
        } else {
            result = null;
        }
        return result;
    }
}
