package ru.mos.polls.push;


import android.support.annotation.StringRes;

public class PushChannel {

    private String id;

    private int channelNameId;

    private String groupId;

    public PushChannel(String id, @StringRes int channelNameId, String groupId) {
        this.id = id;
        this.channelNameId = channelNameId;
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    @StringRes
    public int getChannelNameId() {
        return channelNameId;
    }

    public String getGroupId() {
        return groupId;
    }
}