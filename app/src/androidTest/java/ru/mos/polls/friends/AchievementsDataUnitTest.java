package ru.mos.polls.friends;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.friend.model.AchievementsData;

public class AchievementsDataUnitTest extends BaseUnitTest {
    AchievementsData mAchievementsData;

    @Before
    public void init() {
        mAchievementsData = mockObj("friend_achievements.json", AchievementsData.class);
    }

    @Test
    public void gettersTest() {
        Assert.assertNotNull(mAchievementsData.getCount());
        Assert.assertEquals(mAchievementsData.getCount() > 0, true);
        Assert.assertEquals(mAchievementsData.getLast().size() > 0, true);
    }
}
