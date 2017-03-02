package ru.mos.polls.quest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.R;
import ru.mos.polls.quests.QuestFactory;
import ru.mos.polls.quests.quest.BackQuest;

/**
 * Created by Trunks on 02.03.2017.
 */

public class BackQuestUnitTest extends BaseUnitTest {
    BackQuest test;

    @Before
    public void init() {
        test = (BackQuest) QuestFactory.parse(1, fromTestRawAsJson("backquest.json"));
    }

    @Test
    public void parse() {
        Assert.assertEquals(BackQuest.TYPE_SOCIAL, test.getType());
        String testdetatil = "ВКонтакте, Facebook, Twitter, Одноклассники";
        String testtitle = "Рассказать друзьям";
        String testid = "postInSocial";
        String testurlschemes = "iSuperCitizen://task?task_id=postInSocial";
        assertNotNullOrEmpty(test.getTitle());
        assertNotNullOrEmpty(test.getDetails());
        assertNotNullOrEmpty(test.getUrlScheme());
        assertNotNullOrEmpty(test.getId());
        Assert.assertEquals(testdetatil, test.getDetails());
        Assert.assertNotEquals("asd", test.getDetails());

        Assert.assertEquals(testtitle, test.getTitle());
        Assert.assertNotEquals("asd", test.getTitle());

        Assert.assertEquals(testid, test.getId());
        Assert.assertNotEquals("asd", test.getId());


        Assert.assertEquals(testurlschemes, test.getUrlScheme());
        Assert.assertNotEquals("asd", test.getUrlScheme());

        Assert.assertEquals(5, test.getPoints());
        Assert.assertNotEquals(0, test.getUrlScheme());

        Assert.assertEquals(5, test.getPoints());
        Assert.assertNotEquals(0, test.getPoints());

        Assert.assertEquals("+5", BackQuest.processPoints(test.getPoints()));

        Assert.assertEquals(false, test.isHidden());

        Assert.assertEquals(test.icon, R.drawable.image_icon_category_social);
        Assert.assertEquals(1, test.getInnerId());

    }

    @Test
    public void jsonInputTest() {
        JSONObject testobject = new JSONObject();
        test.addJsonForHide(testobject, true);

        try {
            Assert.assertEquals(testobject.getString("type"), test.getType());
            Assert.assertEquals(testobject.getString("id"), test.getId());
            Assert.assertEquals(testobject.getBoolean("hide"), true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void compareTest() {
        BackQuest test2 = (BackQuest) QuestFactory.parse(1, fromTestRawAsJson("backquest.json"));
        Assert.assertEquals(0, test.compare(test2));
    }
}