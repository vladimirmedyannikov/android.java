package ru.mos.polls.event;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.event.model.EventComment;

/**
 * Created by matek3022 on 12.09.17.
 */

public class EventCommentUnitTest extends BaseUnitTest {

    @Test
    public void parse() {
        JSONObject jsonObject = fromTestRawAsJson("event_comment.json");
        EventComment testValue = EventComment.fromJson(jsonObject, false);

        Assert.assertNotNull(testValue);

        Assert.assertEquals(4, testValue.getId());
        Assert.assertEquals(true, testValue.isCheckIn());

        assertNotNullOrEmpty(testValue.getTitle());
        assertNotNullOrEmpty(testValue.getAuthor());
        assertNotNullOrEmpty(testValue.getBody());
    }
}
