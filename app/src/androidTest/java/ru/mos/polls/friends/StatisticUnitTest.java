package ru.mos.polls.friends;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.mos.elk.profile.Statistics;
import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.friend.model.Statistic;

/**
 * Created by Trunks on 15.11.2017.
 */

public class StatisticUnitTest extends BaseUnitTest {
    Statistic statistic;

    @Before
    public void init() {
        statistic = mockObj("friend_statistics.json", Statistic.class);
    }

    @Test
    public void gettersTest() {
        Statistic statistic = mockObj("friend_statistics.json", Statistic.class);
        Assert.assertNotNull(statistic.getStatus());
        Assert.assertNotNull(statistic.getParams());
        Assert.assertEquals(statistic.getParams().size() > 0, true);
        Assert.assertEquals(statistic.getRating() >= 0, true);
    }

    @Test
    public void gettersStatisticsTest() {
        Statistics statistics = statistic.getParams().get(0);
        Assert.assertNotNull(statistics.getValue());
        Assert.assertNotNull(statistics.getTitle());
    }
}
