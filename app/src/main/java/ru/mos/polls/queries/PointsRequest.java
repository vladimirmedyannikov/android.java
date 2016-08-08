package ru.mos.polls.queries;

import android.content.Context;

import com.android.volley2.Response;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.netframework.request.GeneralRequest;
import ru.mos.polls.PointsManager;
import ru.mos.polls.model.UserPoints;

/**
 * Использует устаревший метод получения текущего баланса баллов пользователя
 * <p/>
 * Не используется с версии 1.9.4, вместо него
 * данные по текущему состоянию баланса баллов пользователя получаем {@link PointHistoryListRequest}
 * Оставим пока, возможно пригодиться
 */
@Deprecated
public class PointsRequest<T> extends GeneralRequest<T> {

    public static final String FREEZED_POINTS = "freezed_points";
    public static final String SPENT_POINTS = "spent_points";
    public static final String ALL_POINTS = "all_points";
    public static final String CURRENT_POINTS = "current_points";
    public static final String STATE = "state";

    private final Context context;
    private UserPoints points = null;

    public PointsRequest(Context context, String url, JSONObject requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(url, requestBody, listener, errorListener);
        this.context = context;
    }


    @Override
    protected T parseResult(JSONObject jsonObject) throws JSONException {
        JSONObject jsonPoints = jsonObject.getJSONObject("result").optJSONObject("status");
        if (jsonPoints != null)
            points = new UserPoints(
                    jsonPoints.optInt(FREEZED_POINTS),
                    jsonPoints.optInt(SPENT_POINTS),
                    jsonPoints.optInt(ALL_POINTS),
                    jsonPoints.optInt(CURRENT_POINTS),
                    jsonPoints.optString(STATE)
            );
        PointsManager.storePoints(context, points);

        return null;
    }

    public UserPoints getPoints() {
        return points;
    }
}
