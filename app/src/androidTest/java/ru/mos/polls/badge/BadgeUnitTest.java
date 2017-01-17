package ru.mos.polls.badge;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.badge.model.Badge;

/**
 *
 */

public class BadgeUnitTest extends BaseUnitTest {
    @Test
    public void parse() {
        Badge testValue = new Badge(fromTestRawAsJson("badge.json"));
        Assert.assertNotNull(testValue);
        Assert.assertNotNull(testValue.getType());
        Assert.assertEquals(Badge.Type.POLLS, testValue.getType());
        Assert.assertEquals(true, testValue.forPoll());
        Assert.assertEquals(false, testValue.forNew());
        Assert.assertEquals(false, testValue.forNovelty());
        Assert.assertNotNull(testValue.getIds());
        Assert.assertEquals(true, testValue.hasIds());
        Assert.assertEquals(true, testValue.getCount() > 0 && testValue.hasIds());
    }

    @Test
    public void parseList() {
        List<Badge> testValue = Badge.fromJson(fromTestRawAsJsonArray("list_badge.json"));
        Assert.assertNotNull(testValue);
        Assert.assertEquals(3, testValue.size());
        Assert.assertNotNull(testValue.get(0));
        Assert.assertNotNull(testValue.get(1));
        Assert.assertNotNull(testValue.get(2));
    }
}
