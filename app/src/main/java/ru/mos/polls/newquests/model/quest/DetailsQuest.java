package ru.mos.polls.newquests.model.quest;

import org.json.JSONObject;

public abstract class DetailsQuest extends BackQuest {

    public DetailsQuest(long innerId, JSONObject jsonObject) {
        super(innerId, jsonObject);
    }
}
