package ru.mos.polls.subscribes;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.subscribes.model.Channel;

/**
  * on 28.03.17 8:11.
 */

public class ChannelUnitTest extends BaseUnitTest {

    @Test
    public void parseEmail() {
        parse("subscribes_channel_email.json", Channel.CHANNEL_EMAIL);
    }

    @Test
    public void parsePush() {
        parse("subscribes_channel_push.json", Channel.CHANNEL_PUSH);
    }

    @Test
    public void parseSms() {
        parse("subscribes_channel_sms.json", Channel.CHANNEL_SMS);
    }

    @Test
    public void setEnable() {
        Channel testValue = Channel.fromJson(fromTestRawAsJson("subscribes_channel_email.json"),
                Channel.CHANNEL_EMAIL);
        Assert.assertEquals(true, testValue.isEnabled());
        testValue.setEnabled(false);
        Assert.assertEquals(false, testValue.isEnabled());
    }

    @Test
    public void addToJson() {
        Channel testValue = Channel.fromJson(fromTestRawAsJson("subscribes_channel_email.json"),
                Channel.CHANNEL_EMAIL);
        JSONObject json = new JSONObject();
        testValue.addToJson(json);
        Assert.assertEquals(true, json.has(Channel.CHANNEL_EMAIL));
    }

    private void parse(String stubFile, String channel) {
        Channel testValue = Channel.fromJson(fromTestRawAsJson(stubFile), channel);
        Assert.assertNotNull(testValue);
        Assert.assertEquals(channel,testValue.getName());
        Assert.assertEquals(true, testValue.isEnabled());
    }
}
