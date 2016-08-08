package ru.mos.polls.queries;

import android.content.Context;

import com.android.volley2.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.polls.PointsManager;
import ru.mos.polls.model.PointHistory;
import ru.mos.polls.model.UserPoints;

/**
 * Инкапсулирует запрос даннх по истории начисления, списания и т.д. баллов пользвоателя
 * С весрсии 1.9.4 реализует логику получения баланса баллов пользователя {@link PointsRequest}
 */
public class PointHistoryListRequest extends PointsRequest<PointHistory[]> {
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String POINTS = "points";
    public static final String REFILL = "refill";
    public static final String ACTION = "action";

    private Context context;

    public PointHistoryListRequest(Context context, String url, JSONObject requestBody,
                                   Response.Listener<PointHistory[]> listener,
                                   Response.ErrorListener errorListener) {
        super(context, url, requestBody, listener, errorListener);
        this.context = context;
    }

    @Override
    protected PointHistory[] parseResult(JSONObject json) throws JSONException {
        super.parseResult(json);

        /**
         * Сохранение в бд информацию о баллах пользователя
         * {@link PointHistory}
         */
        JSONObject result = json.getJSONObject("result");
        final JSONArray jsonPointHistories = result.getJSONArray("history");
        final int count = jsonPointHistories.length();

        final PointHistory[] pointHistories = new PointHistory[count];
        JSONObject jsonPointHistory;
        for (int i = 0; i < count; i++) {
            jsonPointHistory = jsonPointHistories.getJSONObject(i);
            pointHistories[i] = new PointHistory(
                    jsonPointHistory.optString(TITLE),
                    jsonPointHistory.optLong(DATE) * 1000L,
                    jsonPointHistory.optInt(POINTS),
                    jsonPointHistory.optBoolean(REFILL),
                    jsonPointHistory.optString(ACTION)
            );
        }

        /**
         * Сохранение текущего баланса баллов пользователя
         * {@link UserPoints}
         */
        JSONObject jsonPoints = result.optJSONObject("status");
        UserPoints points = null;
        if (jsonPoints != null)
            points = new UserPoints(
                    jsonPoints.optInt(FREEZED_POINTS),
                    jsonPoints.optInt(SPENT_POINTS),
                    jsonPoints.optInt(ALL_POINTS),
                    jsonPoints.optInt(CURRENT_POINTS),
                    jsonPoints.optString(STATE)
            );
        PointsManager.storePoints(context, points);

        return pointHistories;
    }

}
