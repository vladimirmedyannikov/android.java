package ru.mos.polls.innovation;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.innovation.model.ShortInnovation;
import ru.mos.polls.innovation.model.Status;

/**
 */

public class ShortInnovationUnitTest extends BaseUnitTest {

    @Test
    public void parseList() {
        JSONArray array = fromTestRawAsJsonArray("list_short_innovation.json");
        List<ShortInnovation> testValue = ShortInnovation.fromJsonArray(array);
        Assert.assertNotNull(testValue);
        Assert.assertEquals(3, testValue.size());
        Assert.assertNotNull(testValue.get(0));
        Assert.assertNotNull(testValue.get(1));
        Assert.assertNotNull(testValue.get(2));
    }

    @Test
    public void parse() {
        ShortInnovation testValue = getTestValue();
        Assert.assertNotNull(testValue);
        Assert.assertEquals(151, testValue.getId());
        assertNotNullOrEmpty(testValue.getTitle());
        Assert.assertNotNull(testValue.getStatus());
        Assert.assertEquals("old", testValue.getStatus().toString());
        Assert.assertEquals(4.4, testValue.getFullRating(), 0);
        Assert.assertEquals(1480798800000L, testValue.getBeginDate());
        Assert.assertEquals(1483131600000L, testValue.getEndDate());
    }

    @Test
    public void hasStatus() {
        ShortInnovation testValue = getTestValue();
        Assert.assertNotNull(testValue);
        Assert.assertNotNull(testValue.getStatus());
        Assert.assertEquals(true, testValue.isOld());
        Assert.assertEquals(false, testValue.isActive());
        Assert.assertEquals(false, testValue.isPassed());
    }

    @Test
    public void hasBeginDate() {
        ShortInnovation testValue = getTestValue();
        Assert.assertNotNull(testValue);
        Assert.assertEquals(true, testValue.getBeginDate() > 0);
    }

    @Test
    public void hasEndDate() {
        ShortInnovation testValue = getTestValue();
        Assert.assertNotNull(testValue);
        Assert.assertEquals(true, testValue.getEndDate() > 0);
    }

    @Test
    public void setActive() {
        ShortInnovation testValue = getTestValue();
        Assert.assertNotNull(testValue);
        testValue.setStatus(Status.ACTIVE);
        Assert.assertEquals(true, testValue.isActive());
    }

    @Test
    public void setOld() {
        ShortInnovation testValue = getTestValue();
        Assert.assertNotNull(testValue);
        testValue.setStatus(Status.OLD);
        Assert.assertEquals(true, testValue.isOld());
    }

    @Test
    public void setPassed() {
        ShortInnovation testValue = getTestValue();
        Assert.assertNotNull(testValue);
        testValue.setStatus(Status.PASSED);
        Assert.assertEquals(true, testValue.isPassed());
    }

    private ShortInnovation getTestValue() {
        JSONObject jsonObject = fromTestRawAsJson("short_innovation.json");
        return new ShortInnovation(jsonObject);
    }

}
