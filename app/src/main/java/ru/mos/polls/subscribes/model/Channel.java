package ru.mos.polls.subscribes.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Channel {

    public static final String CHANNEL_SMS = "sms";
    public static final String CHANNEL_EMAIL = "email";
    public static final String CHANNEL_PUSH = "push";

    private final String name;
    private boolean enabled = false;

    public Channel(String name) {
        this.name = name;
    }

    public Channel(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addToJson(JSONObject jsonObject) {
        try {
            jsonObject.put(name, enabled);
        } catch (JSONException e) {

        }
    }

    public static Channel fromJson(JSONObject channedlsJsonObject, String key) {
        final Channel result = new Channel(key);
        try {
            boolean value = channedlsJsonObject.getBoolean(key);
            result.setEnabled(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getName() {
        return name;
    }
}
