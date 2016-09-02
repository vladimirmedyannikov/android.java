package ru.mos.polls.social.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.Statistics;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonArrayRequest;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.AGApplication;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.http.HttpUtils;
import ru.mos.polls.http.Request;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.Message;
import ru.mos.polls.social.model.Social;
import ru.mos.polls.social.model.SocialPostItem;
import ru.mos.polls.social.model.SocialPostValue;

/**
 * Инкапсулирует работу с сервисами АГ для социальных сетей:
 * 1) отправка уведомления на сс, что пост выполнен:
 * 2) привязка данных социальной сети к аккаунту АГ;
 * 3) получение данных социальных сетей, привязанных к аккаунту АГ
 * <p/>
 */
public abstract class AgSocialApiController {
    private static ProgressDialog pd;

    /**
     * Получение данных для постинга для главного экрана
     *
     * @param activity elk ActionBarActivity
     * @param listener callback для отображения списка соц сетей
     */
    public static void loadPostingData(BaseActivity activity, SocialPostValueListener listener) {
        loadPostingData(activity, -1, SocialPostValue.Type.TASK, listener);
    }

    public static void loadPostingDataForAchievement(BaseActivity activity, String achievementId, SocialPostValueListener listener) {
        loadPostingData(activity, achievementId, SocialPostValue.Type.ACHIEVEMENT, listener);
    }

    public static void loadPostingDataForNovelty(BaseActivity activity, long noveltyId, SocialPostValueListener listener) {
        loadPostingData(activity, noveltyId, SocialPostValue.Type.NOVELTY, listener);
    }

    /**
     * Получение данных для постинга о прохождении голосования
     *
     * @param activity elk ActionBarActivity
     * @param pollId   идентификатор голосования
     * @param listener callback для отображения списка соц сетей
     */
    public static void loadPostingDataForPoll(BaseActivity activity, long pollId, boolean isHearing, SocialPostValueListener listener) {
        loadPostingData(activity, pollId, isHearing ? SocialPostValue.Type.HEARING : SocialPostValue.Type.POLL, listener);
    }

    /**
     * Получение данных для постинга об отметкина мероприятии
     *
     * @param activity elk ActionBarActivity
     * @param eventId  идентификатор мероприятия, на котором отметились
     * @param listener callback для отображения списка соц сетей
     */
    public static void loadPostingDataForEvent(BaseActivity activity, long eventId, SocialPostValueListener listener) {
        loadPostingData(activity, eventId, SocialPostValue.Type.CHECK_IN, listener);
    }

    /**
     * Получение данных для постинга с сс
     *
     * @param activity elk ActionBarActivity
     * @param id       идентификатор пройденного голосования
     * @param type     тип постинга
     * @param listener callback для отображения списка соц сетей
     */
    private static void loadPostingData(final BaseActivity activity, final Object id, final SocialPostValue.Type type, final SocialPostValueListener listener) {
        showProgress(activity);
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.GET_SOCIAL_INFO));
        if (AGApplication.IS_POSTING_LIMITATION) {
            url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.GET_SOCIAL_INFO));
        }
        JSONObject requestJsonObject = new JSONObject();

        try {
            if (id instanceof Long) {
                if (((Long) id) != -1 && !type.isTask()) {
                    requestJsonObject.put(type.getName(), id);
                }
            } else if (id instanceof String) {
                if (!TextUtils.isEmpty((String) id) && !type.isTask()) {
                    requestJsonObject.put(type.getName(), id);
                }
            }

        } catch (JSONException ignored) {
        }

        Session.addSession(requestJsonObject);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideProgress();
                if (jsonObject != null) {
                    List<SocialPostItem> items = SocialPostItem.createItems(activity, jsonObject, type, id);

                    if (listener != null) {
                        listener.onLoaded(items);
                    }
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onError(activity, volleyError);
            }
        };
        activity.addRequest(new JsonObjectRequest(url, requestJsonObject, responseListener, errorListener));
    }

    /**
     * Оповещение сс Аг о постинге в соцсеть
     *
     * @param context         не elk ActionBarActivity, а обычный контекст, поэтому делаем обычный запрос
     * @param socialPostValue данные, которые запостили
     * @param listener        callback
     */
    public static void notifyAboutPosting(final Context context,
                                          final SocialPostValue socialPostValue, final PostingNotifyListener listener) {
        /**
         * оповещаем сервер о постинге
         */
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.NOTIFY_SOCIAL_POSTED));

        /**
         * тело запроса
         */
        final JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("type", SocialManager.getSocialName(socialPostValue.getSocialId()));
            if (!socialPostValue.isForTask()) {
                requestJson.put(socialPostValue.getType().getName(), socialPostValue.getId());
                requestJson.put("text", socialPostValue.getText());
            }
            /**
             * передаем сессию
             */
            JSONObject authJson = new JSONObject();
            authJson.put(Session.SESSION_ID, Session.getSession(context));
            requestJson.put(Session.AUTH, authJson);
        } catch (JSONException ignored) {
        }
        /**
         * Задаем callback для обработки результатов запроса
         */
        Request.ResponseListener responseListener = new Request.ResponseListener() {
            @Override
            public void onSuccess(HttpUtils.HttpResult httpResult) {
                if (httpResult != null) {
                    if (socialPostValue != null) {
                        socialPostValue.setEnable(false);
                        SocialUIController.SocialAdapterHolder currentSocialAdapter
                                = SocialUIController.getCurrentSocialAdapterHolder();
                        if (currentSocialAdapter != null) {
                            currentSocialAdapter.refreshSocialListView(socialPostValue);
                        }
                    }
                    try {
                        JSONObject responseJson = new JSONObject(httpResult.getEnvelope());
                        /**
                         * если есть кастомное сообщение о постинге, его отображем
                         */
                        responseJson = responseJson.optJSONObject("result");
                        JSONObject JSONmessage = responseJson.optJSONObject("message");
                        if (JSONmessage != null) {
                            Message message = new Message(JSONmessage);

                            if (!message.isEmpty()) {
                                if (listener != null) {
                                    listener.onNotified(message);
                                }
                                return;
                            }
                        }
                        /**
                         * если кастомный диалог не отобразили, то показываем то, что в теге result
                         */
                        Map<String, String> stParams = new HashMap<String, String>();
                        stParams.put("name", SocialManager.getStatisticsKey(socialPostValue.getSocialId()));
                        Statistics.customEvent("social post", stParams);
                        responseJson = responseJson.getJSONObject("status");
                        int points = responseJson.optInt("current_points");
                        String pointsTitle = PointsManager.getPointUnitString(context, points);
                        String text = String.format(context.getString(R.string.post_added), points, pointsTitle);
                        if (listener != null) {
                            listener.onNotified(text);
                        }
                    } catch (JSONException ignored) {
                    }
                }
            }

            @Override
            public void onError(HttpUtils.HttpResult httpResult) {
                /**
                 * пользователю не выводим ошибку,
                 * так как ему об этом знать не надо
                 */
            }
        };
        /**
         * формируем хедеры для запроса
         */
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", "application/json");
        /**
         * Конструируем объект запроса и выпоняем его
         */
        Request.Builder builder = new Request.Builder();
        builder.setDebug(true)
                .setHeaders(headers)
                .setUrl(url)
                .setRequestType(Request.RequestType.POST)
                .setEnvelope(requestJson.toString())
                .setResponseListener(responseListener)
                .build();

    }

    /**
     * Получение сохранненых ранее соц сетей
     *
     * @param activity
     * @param listener
     */
    public static void loadSocials(final BaseActivity activity, final LoadSocialListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.PROFILE_GET_SOCIAL));
        if (AGApplication.IS_SOCIAL_API_V03_ENABLE) {
            url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.PROFILE_GET_SOCIAL));
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonResult) {
                if (jsonResult != null) {
                    List<Social> savedSocial = Social.getSavedSocials(activity);
                    List<Social> newSocials = new ArrayList<Social>();
                    add(newSocials, Social.findFbSocial(savedSocial), getFbSocial(jsonResult));
                    add(newSocials, Social.findVkSocial(savedSocial), getVkSocial(jsonResult));
                    add(newSocials, Social.findTwSocial(savedSocial), getTwSocial(jsonResult));
                    add(newSocials, Social.findOkSocial(savedSocial), getOkSocial(jsonResult));
                    /**
                     * TODO неясно для чего удаляем очищаем локальный кеш, если соцсеть не привязана в профию АГ
                     * пока оставим 11.04.2016, работа по релизу 2.0.0
                     * Если входе тетсирования не возникнет вопросов, то после релиза, удалить этот закомментированный фрагмент
                     */
//                    for (Social social : newSocials) {
//                        if (!social.isLogon()) {
//                            social.clearAuth(activity);
//                            social.reset(activity);
//                        }
//                    }
                    if (listener != null) {
                        listener.onLoaded(newSocials);
                    }
                }
            }

            private void add(List<Social> newSocials, Social savedSocial, Social newSocial) {
                if (!newSocial.isEmpty()) {
                    newSocial.save(activity);
                    newSocials.add(newSocial);
                } else {
                    savedSocial.setIsLogin(false);
                    savedSocial.save(activity);
                    newSocials.add(savedSocial);
                }
            }

            private Social getOkSocial(JSONObject jsonResult) {
                return getSocial(SocialManager.SOCIAL_NAME_OK, jsonResult);
            }

            private Social getVkSocial(JSONObject jsonResult) {
                return getSocial(SocialManager.SOCIAL_NAME_VK, jsonResult);
            }

            private Social getTwSocial(JSONObject jsonResult) {
                return getSocial(SocialManager.SOCIAL_NAME_TW, jsonResult);
            }

            private Social getFbSocial(JSONObject jsonResult) {
                return getSocial(SocialManager.SOCIAL_NAME_FB, jsonResult);
            }

            private Social getSocial(String socialName, JSONObject jsonResult) {
                return new Social(socialName, jsonResult.optJSONObject(socialName));
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onError(activity, volleyError);
                if (listener != null) {
                    listener.onError();
                }
            }
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, responseListener, errorListener);
        activity.addRequest(jsonObjectRequest);
    }

    /**
     * Отвязать указанную соцсеть от профиля аг
     *
     * @param elkActivity
     * @param social
     * @param listener
     */
    public static void unbindSocialFromAg(BaseActivity elkActivity, Social social, SaveSocialListener listener) {
        binding(elkActivity, social, false, listener);
    }

    /**
     * Привязка указанной соцсети к профилю аг
     *
     * @param elkActivity
     * @param social
     * @param listener
     */
    public static void bindSocialToAg(BaseActivity elkActivity, Social social, SaveSocialListener listener) {
        binding(elkActivity, social, true, listener);
    }


    /**
     * Привязка данных социальной сети к аккаунту АГ
     *
     * @param activity
     * @param social   данные соцсети
     * @param listener callback
     * @param forBind  признак привязки или отвязки соцсети
     */
    public static void binding(final BaseActivity activity, final Social social, final boolean forBind, final SaveSocialListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.PROFILE_UPDATE_SOCIAL));
        if (AGApplication.IS_SOCIAL_API_V03_ENABLE) {
            url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.PROFILE_UPDATE_SOCIAL));
        }

        final JSONObject jsonRequest = new JSONObject();
        try {
            JSONObject params = social.tokenDataAsJson();
            if (!forBind) {
                params = social.asKillJson();
            }
            jsonRequest.put("social", params);
        } catch (JSONException ignored) {
        }
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!forBind) {
                    listener.onSaved(social, 0, 0, 0, 0, "");
                } else {
                    if (response != null) {
                        int freezedPoints = 0, spentPoints = 0, allPoints = 0, currentPoints = 0;
                        String state = "";
                        response = response.optJSONObject("status");
                        if (response != null) {
                            freezedPoints = response.optInt("freezed_points");
                            spentPoints = response.optInt("spent_points");
                            allPoints = response.optInt("all_points");
                            currentPoints = response.optInt("current_points");
                            state = response.optString("response");
                        }
                        if (listener != null) {
                            listener.onSaved(social, freezedPoints, spentPoints, allPoints, currentPoints, state);
                        }
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError(activity, error);
                if (listener != null) {
                    listener.onError(social);
                }
            }
        };
        final JsonObjectRequest request = new JsonObjectRequest(url, jsonRequest, responseListener, errorListener);
        activity.addRequest(request);
    }

    private static void showProgress(Context context) {
        showProgress(context, null);
    }

    private static void onError(Context context, VolleyError volleyError) {
        hideProgress();
        if (volleyError != null) {
            Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void showProgress(Context context, String message) {
        pd = new ProgressDialog(context);
        if (!TextUtils.isEmpty(message) && !"null".equalsIgnoreCase(message)) {
            pd.setMessage(message);
        }
        pd.show();
    }

    private static void hideProgress() {
        try {
            if (pd != null) {
                pd.dismiss();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * callback получения данных для постинга
     */
    public interface SocialPostValueListener {
        void onLoaded(List<SocialPostItem> socialPostItems);
    }

    /**
     * callback уведомления сс АГ о постинге
     */
    public interface PostingNotifyListener {
        void onNotified(Message customMessage);

        void onNotified(String message);
    }

    /**
     * callback получения списка привязанных соцсетей к аккакнту АГ
     */
    public interface LoadSocialListener {
        void onLoaded(List<Social> socials);

        void onError();
    }

    /**
     * callback привязки данных соцсетей к аккаунту АГ
     */
    public interface SaveSocialListener {
        void onSaved(Social social, int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state);

        void onError(Social social);
    }
}
