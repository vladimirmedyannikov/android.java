package ru.mos.polls.subscribes.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Subscription {

    public static final String TYPE_AG_NEW = "ag_new";
    public static final String TYPE_AG_SPECIAL = "ag_special";
    public static final String TYPE_POLL_RESULTS = "poll_results";
    public static final String TYPE_POLL_DECISIONS = "poll_decisions";
    public static final String TYPE_POLL_EFFECTED = "poll_effected";
    public static final String TYPE_EVENT_APPROACHING = "event_approaching";
    public static final String TYPE_EVENT_NEW = "event_new";
    public static final String TYPE_NOVELTY_NEW = "novelty_new";
    public static final String TYPE_OSS = "oss";

    public static Subscription fromJson(JSONObject jsonObject) {
        final Subscription result;
        String name = jsonObject.optString("subscription_type");
        result = new Subscription(name);
        JSONObject channedlsJsonObject = jsonObject.optJSONObject("channels");
        Iterator<String> keys = channedlsJsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Channel channel = Channel.fromJson(channedlsJsonObject, key);
            result.getChannels().add(channel);
        }
        return result;
    }

    @SerializedName("subscription_type")
    private final String type;
    @SerializedName("channels")
    private List<Channel> channels = new ArrayList<Channel>();

    public Subscription(String type) {
        this.type = type;
    }

    public Subscription(String type, List<Channel> channels) {
        this.type = type;
        this.channels.addAll(channels);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public JSONObject asJson() {
        JSONObject result = new JSONObject();
        try {
            result.put("subscription_type", type);
            JSONObject channelsJsonObject = new JSONObject();
            for (Channel channel : channels) {
                channel.addToJson(channelsJsonObject);
            }
            result.put("channels", channelsJsonObject);
        } catch (JSONException e) {

        }
        return result;
    }

    public String getType() {
        return type;
    }

    public boolean isEmailChannelEnable() {
        return isChannelEnable(Channel.CHANNEL_EMAIL);
    }

    public boolean isChannelEnable(String channelName) {
        boolean result = false;
        for (Channel channel : channels) {
            if (channelName != null && channelName.equalsIgnoreCase(channel.getName())) {
                result = channel.isEnabled();
            }
        }
        return result;
    }
}
