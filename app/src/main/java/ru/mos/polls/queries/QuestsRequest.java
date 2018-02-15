package ru.mos.polls.queries;

import com.android.volley2.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.netframework.request.GeneralRequest;
import ru.mos.polls.quests.model.quest.Quest;


public class QuestsRequest extends GeneralRequest<Quest[]> {

    public QuestsRequest(String url, JSONObject requestBody, Response.Listener<Quest[]> listener, Response.ErrorListener errorListener) {
        super(url, requestBody, listener, errorListener);
    }

    @Override
    protected Quest[] parseResult(JSONObject jsonObject) throws JSONException {
        JSONArray tasksJsonArray = jsonObject.getJSONObject("result").optJSONArray("tasks");
        Quest[] result = new Quest[tasksJsonArray.length()];
        for (int i = 0; i < tasksJsonArray.length(); i++) {
            JSONObject jsonTask = tasksJsonArray.optJSONObject(i);
//            Quest quest = QuestFactory.parse(i, jsonTask);
//            result[i] = quest;
        }

        return result;
    }
}
