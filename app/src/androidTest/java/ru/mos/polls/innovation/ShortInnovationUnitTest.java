package ru.mos.polls.innovation;


import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseTest;
import ru.mos.polls.innovation.model.ShortInnovation;

/**
 */

public class ShortInnovationUnitTest extends BaseTest{
    public ShortInnovationUnitTest() {
    }

    @Test
    public void parse() {
        JSONObject jsonObject = fromTestRawAsJson("short_innovation.json");
        ShortInnovation testValue = new ShortInnovation(jsonObject);
        Assert.assertNotNull(testValue);
        Assert.assertEquals(151, testValue.getId());
        assertNotNullOrEmpty(testValue.getTitle());
        Assert.assertNotNull(testValue.getStatus());
        Assert.assertEquals("old", testValue.getStatus().toString());
        Assert.assertEquals(4.4, testValue.getFullRating());
        Assert.assertEquals(1480798800, testValue.getBeginDate());
        Assert.assertEquals(1483131600, testValue.getEndDate());
    }
}
