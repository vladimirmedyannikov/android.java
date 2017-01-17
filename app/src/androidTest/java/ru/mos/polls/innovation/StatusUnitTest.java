package ru.mos.polls.innovation;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.innovation.model.Status;

/**
 */

public class StatusUnitTest {
    @Test
    public void parse() {
        Status testValue = Status.parse("active");
        Assert.assertNotNull(testValue);
        Assert.assertEquals(Status.ACTIVE, testValue);
        Assert.assertEquals(Status.ACTIVE.toString(), testValue.toString());

        testValue = Status.parse("passed");
        Assert.assertNotNull(testValue);
        Assert.assertEquals(Status.PASSED, testValue);
        Assert.assertEquals(Status.PASSED.toString(), testValue.toString());

        testValue = Status.parse("old");
        Assert.assertNotNull(testValue);
        Assert.assertEquals(Status.OLD, testValue);
        Assert.assertEquals(Status.OLD.toString(), testValue.toString());

        testValue = Status.parse("");
        Assert.assertNull(testValue);
    }
}
