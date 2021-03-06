package ru.mos.polls.innovations.controller;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.common.model.UserStatus;
import ru.mos.polls.innovations.oldmodel.InnovationActiviti;
import ru.mos.polls.innovations.oldmodel.Rating;
import ru.mos.polls.innovations.model.ShortInnovation;
import ru.mos.polls.innovations.oldmodel.Status;

/**
 * Контроллер для работы с api городских новинок
 *
 * @since 1.9
 */
public abstract class InnovationApiController {
    /**
     * Получение списка активных новинок
     *
     * @param elkActivity
     * @param pageInfo       пагинация {@link ru.mos.polls.common.model.PageInfo}
     * @param selectListener callback {@link ru.mos.polls.innovations.controller.InnovationApiController.ShortInnovationListener}
     */
    public static void selectActive(BaseActivity elkActivity, final PageInfo pageInfo, final ShortInnovationListener selectListener) {
        select(elkActivity, Status.ACTIVE, pageInfo, selectListener);
    }

    /**
     * Прошедшие новики, в которых участвовал пользователь
     *
     * @param elkActivity
     * @param pageInfo       пагиация {@link ru.mos.polls.common.model.PageInfo}
     * @param selectListener callback {@link ru.mos.polls.innovations.controller.InnovationApiController.ShortInnovationListener}
     */
    public static void selectPassed(BaseActivity elkActivity, final PageInfo pageInfo, final ShortInnovationListener selectListener) {
        select(elkActivity, Status.PASSED, pageInfo, selectListener);
    }

    /**
     * Пропущенные новинки, в которых пользвоатель не принимал участия
     *
     * @param elkActivity
     * @param pageInfo       пагинация {@link ru.mos.polls.common.model.PageInfo}
     * @param selectListener callback {@link ru.mos.polls.innovations.controller.InnovationApiController.ShortInnovationListener}
     */
    public static void selectOld(BaseActivity elkActivity, final PageInfo pageInfo, final ShortInnovationListener selectListener) {
        select(elkActivity, Status.OLD, pageInfo, selectListener);
    }

    /**
     * Получение списк городских новинок
     *
     * @param elkActivity
     * @param status         тип новинок {@link ru.mos.polls.innovations.oldmodel.Status}
     * @param pageInfo       пагинация {@link ru.mos.polls.common.model.PageInfo}
     * @param selectListener callback {@link ru.mos.polls.innovations.controller.InnovationApiController.ShortInnovationListener}
     */
    public static void select(BaseActivity elkActivity, Status status, final PageInfo pageInfo, final ShortInnovationListener selectListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.NOVELTY, UrlManager.Methods.SELECT));
        JSONObject requestJson = new JSONObject();
        try {
            if (status != null) {
                requestJson.put("filter", status.toString());
            }
            if (pageInfo != null) {
                requestJson.put("page_number", pageInfo.getPageNumber());
                requestJson.put("count_per_page", pageInfo.getCountPerPage());
            }
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    JSONArray jsonArray = jsonObject.optJSONArray("novelties");
                    List<ShortInnovation> result = ShortInnovation.fromJsonArray(jsonArray);
                    /**
                     *  текущий статус пользоваеля
                     */
                    JSONObject userStatusJsonObject = jsonObject.optJSONObject("status");
                    UserStatus userStatus = new UserStatus(userStatusJsonObject);
                    if (selectListener != null) {
                        selectListener.onLoaded(result, userStatus, pageInfo);
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (selectListener != null) {
                    selectListener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestJson, responseListener, errorListener);
        elkActivity.addRequest(jsonObjectRequest);
    }

    /**
     * Получение детальной информации о новинке по ее краткомы описанию
     *
     * @param elkActivity
     * @param shortInnovation    объект новинки с кратким описанием {@link ShortInnovation}
     * @param innovationListener callback {@link ru.mos.polls.innovations.controller.InnovationApiController.InnovationListener}
     */
    public static void get(BaseActivity elkActivity, ShortInnovation shortInnovation, InnovationListener innovationListener) {
        long id = -1;
        if (shortInnovation != null) {
            id = shortInnovation.getId();
        }
        get(elkActivity, id, innovationListener);
    }

    /**
     * Получение детальной информации о новинке по ее идентификаторы
     *
     * @param elkActivity
     * @param noveltyId          идентификатор объекта городской новинки
     * @param innovationListener callback {@link ru.mos.polls.innovations.controller.InnovationApiController.InnovationListener}
     */
    public static void get(BaseActivity elkActivity, final long noveltyId, final InnovationListener innovationListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.NOVELTY, UrlManager.Methods.GET));
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("novelty_id", noveltyId);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    JSONObject detailsJson = jsonObject.optJSONObject("details");
                    InnovationActiviti innovationActiviti = new InnovationActiviti(detailsJson);
                    if (innovationListener != null) {
                        innovationListener.onLoaded(innovationActiviti);
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (innovationListener != null) {
                    innovationListener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestJson, responseListener, errorListener);
        elkActivity.addRequest(jsonObjectRequest);
    }

    /**
     * Отправка резултатов голосования по новинке
     *
     * @param elkActivity
     * @param innovationActiviti          объект описания новинки {@link InnovationActiviti}
     * @param rating              оценка пользователя
     * @param fillNoveltyListener callback {@link ru.mos.polls.innovations.controller.InnovationApiController.FillNoveltyListener}
     */
    public static void fill(BaseActivity elkActivity, InnovationActiviti innovationActiviti, int rating, final FillNoveltyListener fillNoveltyListener) {
        long id = -1;
        if (innovationActiviti != null) {
            id = innovationActiviti.getId();
        }
        fill(elkActivity, id, rating, fillNoveltyListener);
    }

    /**
     * Отправка резултатов голосования по новинке
     *
     * @param elkActivity
     * @param noveltyId           идентифиактор новинки
     * @param rating              оценка пользователя
     * @param fillNoveltyListener callback {@link ru.mos.polls.innovations.controller.InnovationApiController.FillNoveltyListener}
     */
    public static void fill(BaseActivity elkActivity, long noveltyId, int rating, final FillNoveltyListener fillNoveltyListener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.NOVELTY, UrlManager.Methods.FILL));
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("novelty_id", noveltyId);
            requestJson.put("user_rating", rating);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    JSONObject ratingJson = jsonObject.optJSONObject("rating");
                    Rating rating = new Rating(ratingJson);
                    QuestMessage message = new QuestMessage(jsonObject);
                    JSONObject statusJsonObject = jsonObject.optJSONObject("status");
                    final int currentPoints = statusJsonObject.optInt("current_points");
                    if (fillNoveltyListener != null) {
                        fillNoveltyListener.onSuccess(rating, message, currentPoints);
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (fillNoveltyListener != null) {
                    fillNoveltyListener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestJson, responseListener, errorListener);
        elkActivity.addRequest(jsonObjectRequest);
    }

    public interface ShortInnovationListener {
        void onLoaded(List<ShortInnovation> loadedShortInnovations, UserStatus userStatus, PageInfo pageInfo);

        void onError(VolleyError volleyError);
    }

    public interface InnovationListener {
        void onLoaded(InnovationActiviti innovationActiviti);

        void onError(VolleyError volleyError);
    }

    public interface FillNoveltyListener {
        void onSuccess(Rating rating, QuestMessage message, int allPoints);

        void onError(VolleyError volleyError);
    }
}
