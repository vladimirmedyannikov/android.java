package ru.mos.polls.event.controller;

import android.app.ProgressDialog;
import android.util.Log;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.common.model.Message;
import ru.mos.polls.common.model.PageInfo;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.common.model.UserStatus;
import ru.mos.polls.event.model.CommentPageInfo;
import ru.mos.polls.event.model.Event;
import ru.mos.polls.event.model.EventComment;
import ru.mos.polls.event.model.Filter;

/**
 * Контроллер для работы с серверсайдом
 */
public abstract class EventAPIController {
    private static final int DEFAULT_COUNT_PER_PAGE_COMMENT = 10;
    private static final int DEFAULT_PAGE_NUMBER_COMMENT = 1;

    /**
     * Проверка наличия активных мероприятий
     *
     * @param elkActivity
     * @param listener    callback для обработки присутсвия/отсутсвия мероприятий
     */
    public static void hasCurrentEvents(BaseActivity elkActivity, final CheckHasCurrentEventsListener listener) {
        EventsListener eventsListener = new EventsListener() {
            @Override
            public void onLoad(List<Event> events, Filter filter, UserStatus userStatus, PageInfo pageInfo) {
                boolean hasEvents = events.size() != 0;
                if (listener != null) {
                    listener.hasCurrentEvents(hasEvents);
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                if (listener != null) {
                    listener.hasCurrentEvents(false);
                }
            }
        };
        loadCurrentEvents(elkActivity, null, new PageInfo(), eventsListener);
    }

    /**
     * Получение текущих мероприятий/событий
     *
     * @param elkActivity
     * @param currentPosition
     * @param pageInfo
     * @param listener
     */
    public static void loadCurrentEvents(BaseActivity elkActivity, Position currentPosition, final PageInfo pageInfo, final EventsListener listener) {
        loadEvents(elkActivity, currentPosition, Filter.CURRENT, pageInfo, listener);
    }

    /**
     * Получение посещенных мероприятий
     *
     * @param elkActivity
     * @param currentPosition
     * @param pageInfo
     * @param listener
     */
    public static void loadVisitedEvents(BaseActivity elkActivity, Position currentPosition, final PageInfo pageInfo, final EventsListener listener) {
        loadEvents(elkActivity, currentPosition, Filter.VISITED, pageInfo, listener);
    }

    /**
     * Получение пропущенных мероприятий
     *
     * @param elkActivity
     * @param currentPosition
     * @param pageInfo
     * @param listener
     */
    public static void loadPastEvents(BaseActivity elkActivity, Position currentPosition, final PageInfo pageInfo, final EventsListener listener) {
        loadEvents(elkActivity, currentPosition, Filter.PAST, pageInfo, listener);
    }

    /**
     * Получение мероприятий/событий в зависимотси от фильтра
     *
     * @param elkActivity
     * @param currentPosition текущеее положение
     * @param filter          фильтр событий/мероприятий, возможные значения CURRENT, PAST, VISITED
     * @param pageInfo        информация для пейджинга
     * @param listener        callback для обработки полученного списка
     */
    public static void loadEvents(BaseActivity elkActivity, Position currentPosition,
                                  final Filter filter, final PageInfo pageInfo, final EventsListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.EVENTS_LIST));
        JSONObject requestJson = new JSONObject();
        try {
            if (currentPosition == null) {
                currentPosition = new Position();
            }
            requestJson.put("my_lat", currentPosition.getLat());
            requestJson.put("my_lng", currentPosition.getLon());
            if (filter != null) {
                requestJson.put("filter", filter.toString());
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
                List<Event> events = new ArrayList<Event>();
                if (jsonObject != null) {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("events");
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject eventJson = jsonArray.getJSONObject(i);
                            Event event = Event.fromJsonCommon(eventJson);
                            events.add(event);
                        }
                    } catch (JSONException ignored) {
                    }
                }
                JSONObject userStatusJsonObject = jsonObject.optJSONObject("status");
                UserStatus userStatus = new UserStatus(userStatusJsonObject);
                if (listener != null) {
                    listener.onLoad(events, filter, userStatus, pageInfo);
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (listener != null) {
                    listener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestJson, responseListener, errorListener);
        elkActivity.addRequest(jsonObjectRequest);
    }

    /**
     * Получение полной информации о мероприятии
     *
     * @param activity
     * @param eventId       - идентификатор мероприятрия
     * @param eventListener
     */
    public static void loadEvent(BaseActivity activity, long eventId, Position position, final EventListener eventListener) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.GET_EVENT));
        JSONObject requestJson = new JSONObject();
        Session.addSession(requestJson);
        if (position != null) {
        }
        try {
            requestJson.put("event_id", eventId);
            if (position == null) {
                position = new Position();
            }
            if (!position.isEmpty()) {
                requestJson.put("my_lat", position.getLat());
                requestJson.put("my_lng", position.getLon());
            }
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject fullJson) {
                Event event = null;
                if (fullJson != null) {
                    event = Event.fromJsonFull(fullJson);
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (eventListener != null) {
                    eventListener.onGetEvent(event);
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(activity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ignored) {
                }
                if (eventListener != null) {
                    eventListener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestJson, responseListener, errorListener);
        activity.addRequest(jsonObjectRequest);
    }

    /**
     * Выполнение чекина
     *
     * @param activity
     * @param eventId         - идентификатор мероприятния, на котором выполнен чекин
     * @param position        - текущие географические координаты пользователя
     * @param checkInListener
     */
    public static void checkIn(BaseActivity activity, long eventId, Position position, final CheckInListener checkInListener) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.CHECKIN_EVENT));
        final JSONObject requestJson = new JSONObject();
        Session.addSession(requestJson);
        try {
            if (position == null) {
                position = new Position();
            }
            requestJson.put("event_id", eventId);

            requestJson.put("position", position.asJson());
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseJson) {
                int freezedPoints = 0, spentPoints = 0, allPoints = 0, currentPoints = 0;
                String state = null;
                Message message = null;
                if (responseJson != null) {
                    JSONObject statusJson = responseJson.optJSONObject("status");
                    if (statusJson != null) {
                        freezedPoints = statusJson.optInt("freezed_points");
                        spentPoints = statusJson.optInt("spent_points");
                        allPoints = statusJson.optInt("all_points");
                        currentPoints = statusJson.optInt("current_points");
                        state = statusJson.optString("state");
                    }
                    JSONObject messageJson = responseJson.optJSONObject("messages");
                    if (messageJson != null) {
                        message = new Message(messageJson);
                    }
                }


                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (checkInListener != null) {
                    checkInListener.onChecked(freezedPoints, spentPoints, allPoints, currentPoints, state, message);
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(activity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (checkInListener != null) {
                    checkInListener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestJson, responseListener, errorListener);
        activity.addRequest(jsonObjectRequest);
    }


    public static void loadEventCommentList(BaseActivity activity, long eventId, CommentPageInfo commentPageInfo, final EventCommentListListener listener) {
        loadEventCommentList(activity, eventId, commentPageInfo.getCountPerPage(), commentPageInfo.getPageNumber(), listener);
    }

    public static void loadEventCommentList(BaseActivity activity, long eventId, final EventCommentListListener listener) {
        loadEventCommentList(activity, eventId, DEFAULT_COUNT_PER_PAGE_COMMENT, DEFAULT_PAGE_NUMBER_COMMENT, listener);
    }

    /**
     * Получение списка комментариев к указанному мероприятию
     *
     * @param activity
     * @param eventId      - идентификатор мероприятрия
     * @param countPerPage - максимальное количество комментариев в ответе, по умолчанию 10
     * @param pageNumber   - номер страницы, по умолчанию 1
     * @param listener
     */
    public static void loadEventCommentList(BaseActivity activity, long eventId, int countPerPage, int pageNumber, final EventCommentListListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.EVENT_COMMENTS_LIST));
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("event_id", eventId);
            jsonRequest.put("count_per_page", countPerPage);
            jsonRequest.put("page_number", pageNumber);
            Session.addSession(jsonRequest);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    JSONObject myCommentJson = jsonObject.optJSONObject("my_comment");
                    EventComment myComment = null;
                    if (myCommentJson != null) {
                        myComment = EventComment.fromJson(myCommentJson, true);
                    }

                    JSONObject pageInfoJson = jsonObject.optJSONObject("page_info");
                    CommentPageInfo pageInfo = null;
                    if (pageInfoJson != null) {
                        pageInfo = new CommentPageInfo(pageInfoJson);
                    }

                    List<EventComment> eventComments = new ArrayList<EventComment>();
                    JSONArray commentsArray = jsonObject.optJSONArray("comments");
                    if (commentsArray != null) {
                        for (int i = 0; i < commentsArray.length(); ++i) {
                            try {
                                JSONObject commentJson = (JSONObject) commentsArray.get(i);
                                if (commentJson != null) {
                                    EventComment eventComment = EventComment.fromJson(commentJson, false);
                                    eventComments.add(eventComment);
                                }
                            } catch (JSONException ignored) {
                            }
                        }
                    }

                    if (listener != null) {
                        listener.onGetEventCommentList(myComment, eventComments, pageInfo);
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(activity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (listener != null) {
                    listener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, jsonRequest, responseListener, errorListener);
        activity.addRequest(jsonObjectRequest);
    }

    /**
     * Удаление комментария, используем метод update с пустыми параметрами
     *
     * @param activity
     * @param eventId
     * @param listener
     */
    public static void deleteComment(BaseActivity activity, long eventId, UpdateEventCommentListener listener) {
        updateComment(activity, eventId, "", "", 0, listener);
    }

    /**
     * Добавление, изменение комментария к указаному событию
     *
     * @param activity
     * @param eventId  - идентификатор мероприятрия
     * @param title    - заголовок комментария
     * @param body     - тело комментария
     * @param rating   - райтинг пользователя
     * @param listener
     */
    public static void updateComment(BaseActivity activity, long eventId, String title, String body, int rating, final UpdateEventCommentListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.UPDATE_EVENT_COMMENT));
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("event_id", eventId);
            jsonRequest.put("title", title);
            jsonRequest.put("body", body);
            jsonRequest.put("rating", rating);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (listener != null) {
                    listener.onUpdated(true);
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(activity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (listener != null) {
                    listener.onError(volleyError);
                }
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, jsonRequest, responseListener, errorListener);
        activity.addRequest(jsonObjectRequest);
    }

    public interface EventsListener {
        void onLoad(List<Event> events, Filter filter, UserStatus userStatus, PageInfo pageInfo);

        void onError(VolleyError volleyError);
    }

    public interface EventListener {
        void onGetEvent(Event event);

        void onError(VolleyError volleyError);
    }

    public interface CheckInListener {
        void onChecked(int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state, Message message);

        void onError(VolleyError volleyError);
    }

    public interface EventCommentListListener {
        void onGetEventCommentList(EventComment myComment, List<EventComment> eventComments, CommentPageInfo commentPageInfo);

        void onError(VolleyError volleyError);
    }

    public interface UpdateEventCommentListener {
        void onUpdated(boolean isUpdated);

        void onError(VolleyError volleyError);
    }

    public interface CheckHasCurrentEventsListener {
        void hasCurrentEvents(boolean hasEvents);
    }
}