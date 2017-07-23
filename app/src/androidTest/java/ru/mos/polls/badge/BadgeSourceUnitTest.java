package ru.mos.polls.badge;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.badge.model.BadgesSource;
import ru.mos.polls.badge.model.State;

/**
 *
 */

public class BadgeSourceUnitTest extends BaseUnitTest {
    private State state;
    private BadgesSource testValue;

    public BadgeSourceUnitTest() {
        state = new State(fromTestRawAsJson("badge_state.json"));
        Assert.assertNotNull(state);

        testValue = BadgesSource.getInstance();
        Assert.assertNotNull(testValue);
    }
    @Test
    public void getBadgeByStringTest() {
        testValue.storeState(state);
        String newsCount = testValue.get(BadgesSource.TAG_NEWS);
        Assert.assertEquals(newsCount, "19");
        String noveltyCount = testValue.get(BadgesSource.TAG_NOVELTY);
        Assert.assertEquals(noveltyCount, "1");
        String pollsCount = testValue.get(BadgesSource.TAG_POLLS);
        Assert.assertEquals(pollsCount, "28");
        String pointsCount = testValue.get(BadgesSource.TAG_POINTS);
        Assert.assertEquals(pointsCount, "+2061");
    }

    @Test
    public void markNewsAsReadTest() {
        testValue.markNewsAsReaded(255);
        long[] newsId = testValue.getReadedNewsIds();
        Assert.assertEquals(newsId[0], 255);
    }

    @Test
    public void storeBadges() {
        testValue.storeState(state);
        if (testValue.getBadgeList().size() <= 0) throw new RuntimeException();
    }


}
