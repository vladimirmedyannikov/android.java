package ru.mos.polls.quest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.quests.QuestFactory;
import ru.mos.polls.quests.controller.QuestStateController;
import ru.mos.polls.quests.quest.BackQuest;
import ru.mos.polls.quests.quest.Quest;

/**
 * Created by Trunks on 02.03.2017.
 */

public class QuestStateControllerUnitTest extends BaseUnitTest {
    List<Quest> testlist;

    @Before
    public void init() {
        testlist = new ArrayList<>();
        JSONArray array = fromTestRawAsJsonArray("list_quest.json");
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonTask = array.optJSONObject(i);
            Quest quest = QuestFactory.parse(i, jsonTask);
            testlist.add(quest);
        }
    }

    @Test
    public void updateSocialAvaibleTest() {
        QuestStateController qsc = QuestStateController.getInstance();
        qsc.clearList();

        Assert.assertEquals(qsc.isUpdateSocialAvaible(), false);

        qsc.process(testlist);

        Assert.assertEquals(qsc.isUpdateSocialAvaible(), true);

        qsc.updateSocialUnavaible();
        Assert.assertEquals(qsc.isUpdateSocialAvaible(), false);

    }

    @Test
    public void executedTest() {
        QuestStateController qsc = QuestStateController.getInstance();

        qsc.clearList();

        Assert.assertEquals(0, qsc.getExecutedQuestQueue().size());

        qsc.add((BackQuest) testlist.get(0));
        Assert.assertEquals(1, qsc.getExecutedQuestQueue().size());

        qsc.add((BackQuest) testlist.get(0));
        Assert.assertEquals(1, qsc.getExecutedQuestQueue().size());

        BackQuest bq = (BackQuest) testlist.get(1);
        qsc.add(bq.getId());
        Assert.assertEquals(2, qsc.getExecutedQuestQueue().size());

        qsc.add(bq.getId());
        Assert.assertEquals(2, qsc.getExecutedQuestQueue().size());
    }
}
