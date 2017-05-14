package ru.mos.polls.poll;

import org.junit.Assert;
import org.junit.Test;


import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.poll.model.Poll;

/**
 * Created by Trunks on 25.04.2017.
 */

public class StatusUnitTest extends BaseUnitTest {

    @Test
    public void getStatus() {
        Poll.Status active = Poll.Status.fromString("active");
        Assert.assertEquals(Poll.Status.ACTIVE, active);

        Poll.Status interrupted = Poll.Status.fromString("interrupted");
        Assert.assertEquals(Poll.Status.INTERRUPTED, interrupted);

        Poll.Status passed = Poll.Status.fromString("passed");
        Assert.assertEquals(Poll.Status.PASSED, passed);

        Poll.Status old = Poll.Status.fromString("old");
        Assert.assertEquals(Poll.Status.OLD, old);
    }
}
