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
    public void storeBadges() {
        testValue.storeState(state);
        //TODO check
    }
}
