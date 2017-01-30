package ru.mos.polls.achievement;


import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.Achievement;

/**
 * Created by Trunks on 30.01.2017.
 */

public class AchievementUnitTest extends BaseUnitTest {


    @Test
    public void parse() {
        Achievement test = new Achievement(fromTestRawAsJson("achievement.json"));
        Assert.assertNotNull(test.getTitle());
        Assert.assertEquals(true, test.isNext());
        assertNotNullOrEmpty(test.getId());
        assertNotNullOrEmpty(test.getBody());
        assertNotNullOrEmpty(test.getDescription());
        assertNotNullOrEmpty(test.getTitle());
    }
}
