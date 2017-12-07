package ru.mos.polls.newquests.service;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.newquests.model.QuestFamilyList;
import ru.mos.polls.newquests.model.quest.AchievementQuest;
import ru.mos.polls.newquests.model.quest.AdvertisementQuest;
import ru.mos.polls.newquests.model.quest.BackQuest;
import ru.mos.polls.newquests.model.quest.EventQuest;
import ru.mos.polls.newquests.model.quest.NewsQuest;
import ru.mos.polls.newquests.model.quest.NoveltyQuest;
import ru.mos.polls.newquests.model.quest.OtherQuest;
import ru.mos.polls.newquests.model.quest.ProfileQuest;
import ru.mos.polls.newquests.model.quest.ResultsQuest;
import ru.mos.polls.newquests.model.quest.SocialQuest;
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
            private List<QuestFamilyList> tasks;
            public List<BackQuest> getTasks() {
                List<BackQuest> tasks = new ArrayList<>();
                for (QuestFamilyList task : this.tasks) {
                    switch (task.getType()) {
                        case AchievementQuest.TYPE:
                            break;
                        case AdvertisementQuest.TYPE:
                            break;
                        case EventQuest.TYPE:
                            break;
                        case NewsQuest.TYPE:
                            break;
                        case NoveltyQuest.TYPE:
                            break;
                        case OtherQuest.TYPE:
                            break;
                        case ProfileQuest.TYPE_PROFILE:
                            break;
                        case ResultsQuest.TYPE:
                            break;
                        case SocialQuest.TYPE:
                            if (task.getAppIds() != null) {
                                //оценка приложения
                            } else {
                                //соц сеть
                            }
                            break;
                    }
                }
                return tasks;
            }
        }
    }
}
