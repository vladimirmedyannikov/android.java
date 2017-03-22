package ru.mos.polls.quest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.quests.QuestFactory;
import ru.mos.polls.quests.quest.Quest;

/**
 * Created by Trunks on 02.03.2017.
 */

public class QuestFactoryUnitTest extends BaseUnitTest {

    @Test
    public void parseList() {
        List<Quest> testlist = new ArrayList<>();
        JSONArray array = fromTestRawAsJsonArray("list_quest.json");
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonTask = array.optJSONObject(i);
            Quest quest = QuestFactory.parse(i, jsonTask);
            testlist.add(quest);
        }

        Assert.assertEquals(9, testlist.size());
    }
}
