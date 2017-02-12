package ru.mos.polls.flat;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.Statistics;

/**
 * Created by Trunks on 12.02.2017.
 */

public class StatisticsUnitTest extends BaseUnitTest {

    @Test
    public void parse() {
        Statistics test = new Statistics(fromTestRawAsJson("statistics.json"));
        int intval = 1;
        long logval = 60000000;
        String year = "01.01.1970";
        Assert.assertEquals(test.getAllPoints(), intval);
        Assert.assertEquals(test.getAnsweredQuestions(), intval);
        Assert.assertEquals(test.getEventsVisited(), intval);
        Assert.assertEquals(test.getPassedPolls(), intval);
        Assert.assertEquals(test.getPromoCodes(), intval);
        Assert.assertEquals(test.getSocialMessages(), intval);
        Assert.assertEquals(test.getSpentPoints(), intval);
        Assert.assertEquals(test.getLastVisit(), logval);
        Assert.assertEquals(test.getRegistrationDate(), logval);
        Assert.assertEquals(test.getRegistrationDateFormatted(), year);
        Assert.assertEquals(test.getLastVisitFormatted(), year);
    }
}
