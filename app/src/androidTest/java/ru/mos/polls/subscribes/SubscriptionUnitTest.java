package ru.mos.polls.subscribes;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;

/**
  * on 28.03.17 8:29.
 */

public class SubscriptionUnitTest extends BaseUnitTest {

    @Test
    public void parse() {
        Subscription testValue = getTestValue();
        Assert.assertNotNull(testValue);
    }

    @Test
    public void parseList() {

    }

    @Test
    public void hasChannels() {
        Subscription testValue = getTestValue();
        Assert.assertEquals(true, testValue.getChannels().size() > 0);
    }

    @Test
    public void hasType() {
        Subscription testValue = getTestValue();
        Assert.assertEquals(Subscription.TYPE_POLL_RESULTS, testValue.getType());
    }

    @Test
    public void isChannelEnable() {
        Subscription testValue = getTestValue();
        Channel channel = testValue.getChannels().get(0);
        Assert.assertEquals(true,  channel.isEnabled());
    }

    @Test
    public void changeChannels() {
        Subscription testValue = getTestValue();
        Channel channel = testValue.getChannels().get(0);
        Assert.assertEquals(true,  channel.isEnabled());
        channel.setEnabled(false);
        Assert.assertEquals(false,  channel.isEnabled());
    }

    @Test
    public void toJson() {
        Subscription testValue = getTestValue();
        JSONObject json = testValue.asJson();
        Assert.assertEquals(true, json.has("subscription_type"));
        Assert.assertEquals(true, json.has("channels"));
    }

    private Subscription getTestValue() {
        return Subscription.fromJson(fromTestRawAsJson("subscribes_subscription.json"));
    }

    public List<Subscription> getTestValues() {
        List<Subscription> result = new ArrayList<>();
        JSONArray jsonArray = fromTestRawAsJsonArray("subscribes_subscriptions.json");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                result.add(Subscription.fromJson(jsonArray.optJSONObject(i)));
            }
        }
        return result;
    }
}
