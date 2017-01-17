package ru.mos.polls.badge;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.badge.model.Badge;

/**
 *
 */

public class TypeUnitTest {
    @Test
    public void parse() {
        Badge.Type testValue = Badge.Type.parse("poll");
        Assert.assertNotNull(testValue);
        Assert.assertEquals(Badge.Type.POLLS, testValue);
        Assert.assertEquals(Badge.Type.POLLS.getValue(), testValue.getValue());

        testValue = Badge.Type.parse("news");
        Assert.assertNotNull(testValue);
        Assert.assertEquals(Badge.Type.NEWS, testValue);
        Assert.assertEquals(Badge.Type.NEWS.getValue(), testValue.getValue());

        testValue = Badge.Type.parse("novelty");
        Assert.assertNotNull(testValue);
        Assert.assertEquals(Badge.Type.NOVELTIES, testValue);
        Assert.assertEquals(Badge.Type.NOVELTIES.getValue(), testValue.getValue());
    }
}
