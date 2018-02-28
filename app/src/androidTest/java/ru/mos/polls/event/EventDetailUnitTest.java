package ru.mos.polls.event;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.event.model.EventDetail;


public class EventDetailUnitTest extends BaseUnitTest {
    @Test
    public void parse() {
        JSONObject jsonObject = fromTestRawAsJson("event_detail.json");
        EventDetail testValue = new EventDetail(jsonObject);
        assertNotNullOrEmpty(testValue.getBody());
        assertNotNullOrEmpty(testValue.getTitle());
        Assert.assertEquals(6, testValue.getMinRowCount());
    }
}
