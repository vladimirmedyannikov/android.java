package ru.mos.polls.social;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.social.model.Message;

/**
 * Created by Trunks on 08.03.2017.
 */

public class MessageUnitTest extends BaseUnitTest {

    @Test
    public void parse() {
        Message message = new Message(fromTestRawAsJson("message.json"));
        assertNotNullOrEmpty(message.getTitle());
        assertNotNullOrEmpty(message.getText());
        Assert.assertEquals(false, message.isEmpty());
    }

}
