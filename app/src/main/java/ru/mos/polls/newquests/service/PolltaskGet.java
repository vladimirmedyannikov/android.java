package ru.mos.polls.newquests.service;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.newquests.model.QuestFamilyElement;
import ru.mos.polls.newquests.model.quest.AchievementQuest;
import ru.mos.polls.newquests.model.quest.AdvertisementQuest;
import ru.mos.polls.newquests.model.quest.EventQuest;
import ru.mos.polls.newquests.model.quest.FavoriteSurveysQuest;
import ru.mos.polls.newquests.model.quest.NewsOssQuest;
import ru.mos.polls.newquests.model.quest.NewsQuest;
import ru.mos.polls.newquests.model.quest.NoveltyQuest;
import ru.mos.polls.newquests.model.quest.OtherQuest;
import ru.mos.polls.newquests.model.quest.ProfileQuest;
import ru.mos.polls.newquests.model.quest.Quest;
import ru.mos.polls.newquests.model.quest.RateAppQuest;
import ru.mos.polls.newquests.model.quest.ResultsQuest;
import ru.mos.polls.newquests.model.quest.SocialQuest;
import ru.mos.polls.newquests.model.quest.SurveyOssQuest;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 10:53.
 */

public class PolltaskGet {
    public static class Request extends AuthRequest {
    }

    public static class Response extends GeneralResponse<Response.Result> {

        public static class Result {
            private List<QuestFamilyElement> tasks;
            public List<Quest> getTasks() {
                List<Quest> tasks = new ArrayList<>();
                for (int i = 0; i < this.tasks.size(); i++) {
                    QuestFamilyElement currIterTask = this.tasks.get(i);
                    switch (currIterTask.getType()) {
                        case AchievementQuest.TYPE:
                            tasks.add(new AchievementQuest(i, currIterTask));
                            break;
                        case AdvertisementQuest.TYPE:
                            tasks.add(new AdvertisementQuest(i, currIterTask));
                            break;
                        case EventQuest.TYPE:
                            tasks.add(new EventQuest(i, currIterTask));
                            break;
                        case FavoriteSurveysQuest.ID_HEARING:
                            tasks.add(new FavoriteSurveysQuest(i, currIterTask));
                            break;
                        case FavoriteSurveysQuest.ID_POLL:
                            tasks.add(new FavoriteSurveysQuest(i, currIterTask));
                            break;
                        case NewsQuest.TYPE:
                            tasks.add(new NewsQuest(i, currIterTask));
                            break;
                        case NoveltyQuest.TYPE:
                            tasks.add(new NoveltyQuest(i, currIterTask));
                            break;
                        case OtherQuest.TYPE:
                            tasks.add(new OtherQuest(i, currIterTask));
                            break;
                        case ProfileQuest.TYPE_PROFILE:
                            tasks.add(new ProfileQuest(i, currIterTask));
                            break;
                        case ResultsQuest.TYPE:
                            tasks.add(new ResultsQuest(i, currIterTask));
                            break;
                        case SocialQuest.TYPE_SOCIAL:
                            if (currIterTask.getAppIds() != null) {
                                //оценка приложения
                                tasks.add(new RateAppQuest(i, currIterTask));
                            } else {
                                //соц сеть
                                tasks.add(new SocialQuest(i, currIterTask));
                            }
                            break;
                        case SurveyOssQuest.TYPE:
                            tasks.add(new SurveyOssQuest(i, currIterTask));
                            break;
                        case NewsOssQuest.TYPE:
                            tasks.add(new NewsOssQuest(i, currIterTask));
                            break;
                    }
                }
                return tasks;
            }
        }
    }
}
