package ru.mos.polls.event;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.event.model.Event;


public class EventUnitTest extends BaseUnitTest {
    @Test
    public void parseFull() {
        JSONObject jsonObject = fromTestRawAsJson("event.json");
        Event testValue = Event.fromJsonFull(jsonObject);

        Assert.assertNotNull(testValue);
        Assert.assertNotNull(testValue.getPosition());

        Assert.assertEquals(4, testValue.getId());
        Assert.assertEquals(300, testValue.getMaxCheckInDistance());
        Assert.assertEquals(false, testValue.isCheckIn());
        Assert.assertEquals(Event.Type.EVENT, testValue.getType());
        Assert.assertEquals(2, testValue.getDetails().size());
        Assert.assertEquals(3, testValue.getImgLinks().size());
        Assert.assertEquals(30, testValue.getPoints());

        assertNotNullOrEmpty(testValue.getTitle());
        assertNotNullOrEmpty(testValue.getStartDate());
        assertNotNullOrEmpty(testValue.getEndDate());
        assertNotNullOrEmpty(testValue.getName());
    }

    @Test
    public void parseCommon() {
        JSONObject jsonObject = fromTestRawAsJson("event_common.json");
        Event testValue = Event.fromJsonCommon(jsonObject);

        Assert.assertNotNull(testValue);
        Assert.assertNotNull(testValue.getPosition());

        Assert.assertEquals(9, testValue.getId());
        Assert.assertEquals(1024, testValue.getDistance());
        Assert.assertEquals(false, testValue.isCheckIn());
        Assert.assertEquals(10, testValue.getPoints());

        assertNotNullOrEmpty(testValue.getTitle());
        assertNotNullOrEmpty(testValue.getDescription());
        assertNotNullOrEmpty(testValue.getStartDate());
        assertNotNullOrEmpty(testValue.getEndDate());
        assertNotNullOrEmpty(testValue.getName());
    }

    @Test
    public void setterTest() {
        Event testValue = new Event();
        testValue.setChecked();
        Assert.assertEquals(true, testValue.isCheckIn());
    }
}
