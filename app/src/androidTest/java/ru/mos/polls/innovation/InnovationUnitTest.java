package ru.mos.polls.innovation;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.innovation.model.Innovation;

/**
 */

public class InnovationUnitTest extends BaseUnitTest {
    @Test
    public void parse() {
        JSONObject jsonObject = fromTestRawAsJson("innovation.json");
        Innovation testValue = new Innovation(jsonObject);
        Assert.assertNotNull(testValue);
        Assert.assertEquals(150, testValue.getId());
        assertNotNullOrEmpty(testValue.getTitle());
        Assert.assertNotNull(testValue.getStatus());
        Assert.assertEquals("old", testValue.getStatus().toString());
        Assert.assertEquals(4.4, testValue.getFullRating());
        Assert.assertEquals(1479762000, testValue.getBeginDate());
        Assert.assertEquals(1483131600, testValue.getEndDate());
    }
}
