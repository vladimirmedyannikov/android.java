package ru.mos.polls.badge;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.badge.model.State;

/**
 *
 */

public class StateUnitTest extends BaseUnitTest {
    @Test
    public void parse() {
        State testValue = new State(fromTestRawAsJson("badge_state.json"));
        Assert.assertNotNull(testValue);
        Assert.assertNotNull(testValue.getPersonal());
        Assert.assertNotNull(testValue.getBadges());
        Assert.assertEquals(true, testValue.getBadges().size() > 0);
        Assert.assertEquals(true, testValue.getPointsCount() > 0);
    }
}
