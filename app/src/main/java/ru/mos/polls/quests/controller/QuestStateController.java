package ru.mos.polls.quests.controller;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.quests.model.quest.BackQuest;
import ru.mos.polls.quests.model.quest.ProfileQuest;
import ru.mos.polls.quests.model.quest.Quest;
import ru.mos.polls.quests.model.quest.SocialQuest;

/**
 * Контроллер для хранения в памяти списка идентификаторов выполненных заданий
 *
 * @see #add(BackQuest)  добавления идентификатора задания в список пройденных
 * @see #process(List) фильтрайия списка заданий, в соотвествии с уже выполеннными заданиями
 * @since 1.9
 */
public class QuestStateController {
    private static QuestStateController instance;
    /**
     * Храним идентфиикаторы в Object,
     * так как они имеют различный тип
     */
    private List<Object> executedQuestQueue;
    private boolean isUpdateSocialAvaible;

    private List<String> idsList;

    private QuestStateController() {
        executedQuestQueue = new ArrayList<>();
    }

    public static synchronized QuestStateController getInstance() {
        if (instance == null) {
            instance = new QuestStateController();
        }
        return instance;
    }

    public void add(BackQuest quest) {
        if (!isExecuted(quest)) {
            executedQuestQueue.add(getId(quest));
        }
    }

    public void add(String taskId) {
        if (!isExecuted(taskId)) {
            executedQuestQueue.add(taskId);
        }
    }

    public synchronized List<Quest> process(List<Quest> quests) {
        List<Quest> result = new ArrayList<>();
        for (Quest quest : quests) {
            if (!isExecuted((BackQuest) quest)) {
                result.add(quest);
                if (((BackQuest) quest).getId().equals(ProfileQuest.ID_UPDATE_SOCIAL))
                    isUpdateSocialAvaible = true;
                if (((BackQuest) quest).getId().equals(ProfileQuest.ID_PERSONAL_WIZARD)) { //сохраняем списод заданий визарда для urlScheme
                    idsList = ((ProfileQuest) quest).idsList;
                }
            }
        }
        return result;
    }

    private Object getId(BackQuest quest) {
        Object result = null;
        if (quest instanceof SocialQuest || quest instanceof ProfileQuest) {
            result = quest.getId();
        }
        return result;
    }

    private boolean isExecuted(BackQuest quest) {
        Object id = getId(quest);
        return executedQuestQueue.contains(id);
    }

    private boolean isExecuted(String taskId) {
        return executedQuestQueue.contains(taskId);
    }

    public boolean isUpdateSocialAvaible() {
        return isUpdateSocialAvaible;
    }

    public void updateSocialUnavaible() {
        isUpdateSocialAvaible = false;
    }

    public void clearList() {
        executedQuestQueue.clear();
    }

    public List<Object> getExecutedQuestQueue() {
        return executedQuestQueue;
    }

    public List<String> getIdsList() {
        if (idsList == null) {
            idsList = new ArrayList<>();
        }
        return idsList;
    }
}
