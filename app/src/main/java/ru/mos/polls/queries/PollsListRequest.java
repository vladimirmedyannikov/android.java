package ru.mos.polls.queries;

import android.content.Context;

import com.android.volley2.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.model.Poll;
import ru.mos.polls.model.PollGroup;


public class PollsListRequest extends PointsRequest<PollGroup[]> {

    public static final String TITLE = "title";
    public static final String ID = "id";

    private static final String PASSED = "passed";
    private static final String INTERRUPTED = "interrupted";

    public PollsListRequest(Context context, String url, JSONObject requestBody, Response.Listener<PollGroup[]> listener, Response.ErrorListener errorListener) {
        super(context, url, requestBody, listener, errorListener);
    }

    @Override
    protected PollGroup[] parseResult(JSONObject json) throws JSONException {

        JSONArray jsonPolls = json.getJSONObject("result").getJSONArray("poll_groups");
        int count = jsonPolls.length();
        PollGroup[] pollGroups = new PollGroup[count];

        for (int i = 0; i < count; i++) {
            JSONObject jsonPollGr = jsonPolls.getJSONObject(i);
            PollGroup group = new PollGroup(jsonPollGr.optString(TITLE), jsonPollGr.optInt(ID));
            JSONArray jsPolls = jsonPollGr.optJSONArray("polls");
            int pCount = jsPolls.length();
            Poll[] polls = new Poll[pCount];
            for (int j = 0; j < pCount; j++) {
                JSONObject jsp = jsPolls.optJSONObject(j);
                String mark = jsp.optString("mark");
                polls[j] = new Poll(jsp.optInt(ID), jsp.optString(TITLE), jsp.optInt("points"), parseStatus(jsp.optString("status")), jsp.optInt("questions_count"), mark);
                if (!jsp.isNull("author"))
                    polls[j].setAuthor(jsp.optString("author"));
            }
            group.setPolls(polls);
            pollGroups[i] = group;
        }

        return pollGroups;
    }

    private static int parseStatus(String strStatus) {
        if (PASSED.equals(strStatus))
            return Poll.PASSED;
        else if (INTERRUPTED.equals(strStatus))
            return Poll.INTERRUPTED;
        else
            return Poll.ACTIVE;
    }
}
