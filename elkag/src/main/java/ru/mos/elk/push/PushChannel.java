package ru.mos.elk.push;


import android.support.annotation.StringRes;

public class PushChannel {

    private String id;

    private int channelNameId;

    public PushChannel(String id, @StringRes int channelNameId) {
        this.id = id;
        this.channelNameId = channelNameId;
    }

    public String getId() {
        return id;
    }

    @StringRes
    public int getChannelNameId() {
        return channelNameId;
    }

}