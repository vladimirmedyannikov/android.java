package ru.mos.polls.common.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import ru.mos.polls.AgAuthActivity;
import ru.mos.polls.quests.model.quest.ProfileQuest;
import ru.mos.polls.quests.model.quest.RateAppQuest;
import ru.mos.polls.quests.model.quest.SocialQuest;
import ru.mos.polls.rxhttp.session.Session;

/**
 * Инкапсулирует обработку неявного старта экрана через url scheme
 * @since 1.9
 */
public abstract class UrlSchemeController {
    public static final String TASK_HOST = "task";
    public static final String POLLTASKS_HOST = "pollTasks";
    public static final String EVENTS_HOST = "events";
    public static final String NEWS_HOST = "news";
    public static final String NOVELTIES_HOST = "novelties";
    public static final String NEW_HOST = "new";
    public static final String OPEN_HOST = "open";
    public static final String ACHIEVEMENT_HOST = "achievement";
    public static final String INNOVATION_HOST = "novelty";
    public static final String ADVERTISEMENT = "advertisement";
    public static final String PERSONAL_WIZARD = "personalWizard";

    public static final String ACHIEVEMENT_ID = "achievement_id";
    public static final String POLL_ID = "poll_id";
    public static final String HEARING_ID = "hearing_id";
    public static final String EVENT_ID = "event_id";
    public static final String NOVELTY_ID = "novelty_id";
    public static final String TASK_ID = "task_id";
    public static final String NEWS_ID = "news_id";
    public static final String ADVERTISEMENT_ID = "id";

    public static final String LINK = "link";
    public static final String LINK_TITLE = "link_title";

    /**
     * старт через неявный интент
     * @param context
     * @param urlScheme url для запуска экрана
     */
    public static void start(Context context, String urlScheme) {
        Intent start = new Intent(Intent.ACTION_VIEW);
        start.setData(Uri.parse(urlScheme));
        context.startActivity(start);
    }

    /**
     * Обработка запуска заданий, экранов списка мероприятий, голосований и другое
     * Запуск заданий не срабатывает при старте через url scheme, так как экран уже запущен
     *
     * Но через url scheme удается открывать экраны списка голосований, мероприятий, новинок, новостей
     *
     * @param activity
     * @param screenListListener
     * @param taskListener
     */
    public static void start(Activity activity, ScreenListListener screenListListener, TaskListener taskListener) {
        if (screenListListener == null) {
            screenListListener = ScreenListListener.STUB;
        }
        if (taskListener == null) {
            taskListener = TaskListener.STUB;
        }
        final ScreenListListener finalScreenListListener = screenListListener;
        final TaskListener finalTaskListener = taskListener;
        UriListener uriListener = new UriListener() {
            @Override
            public void onDetected(Uri uri) {
                String host = uri.getHost();
                if (TASK_HOST.equalsIgnoreCase(host)) {
                    String taskId = uri.getQueryParameter(TASK_ID);
                    if (ProfileQuest.ID_UPDATE_SOCIAL.equalsIgnoreCase(taskId)) {
                        finalTaskListener.onUpdateSocial();
                    } else if (SocialQuest.ID_POST_IN_SOCIAL.equalsIgnoreCase(taskId)) {
                        finalTaskListener.onPostInSocial();
                    } else if (SocialQuest.ID_INVITE_FRIENDS.equalsIgnoreCase(taskId)) {
                        finalTaskListener.onInviteFriends();
                    } else if (ProfileQuest.ID_BIND_TO_PGU.equalsIgnoreCase(taskId)) {
                        finalTaskListener.onBindToPgu();
                    } else if (RateAppQuest.ID_RATE_THIS_APP.equalsIgnoreCase(taskId)) {
                        finalTaskListener.onRateThisApplication();
                    } else if (ProfileQuest.ID_UPDATE_EMAIL.equalsIgnoreCase(taskId)) {
                        finalTaskListener.onUpdateEmail();
                    } else {
                        finalTaskListener.onPersonalTask(taskId);
                    }
                } else if (POLLTASKS_HOST.equalsIgnoreCase(host)) {
                    finalScreenListListener.onPollTasks();
                } else if (EVENTS_HOST.equalsIgnoreCase(host)) {
                    finalScreenListListener.onEvents();
                } else if (NOVELTIES_HOST.equalsIgnoreCase(host)) {
                    finalScreenListListener.onNovelties();
                } else if (NEWS_HOST.equalsIgnoreCase(host)) {
                    finalScreenListListener.onNews();
                } else if (ACHIEVEMENT_HOST.equalsIgnoreCase(host)) {
                    String taskId = uri.getQueryParameter(ACHIEVEMENT_ID);
                    finalScreenListListener.onAchievement(taskId);
                } else if (INNOVATION_HOST.equalsIgnoreCase(host)) {
                    long taskId = Long.valueOf(uri.getQueryParameter(NOVELTY_ID));
                    finalScreenListListener.onInnovation(taskId);
                }
            }
        };
        startFromUri(activity, uriListener);
    }

    /**
     * получение id для голосования при старте через url
     * @param activity
     * @param listener
     */
    public static void startPoll(Activity activity, final IdListener listener) {
        if (isHearing(activity)) {
            getId(activity, listener, HEARING_ID);
        } else {
            getId(activity, listener, POLL_ID);
        }
    }

    public static boolean isHearing(Activity activity) {
        Uri uri = activity.getIntent().getData();
        return uri != null
                && uri.getQueryParameterNames() != null
                && uri.getQueryParameterNames().contains(HEARING_ID);
    }

    public static boolean hasUri(Activity activity) {
        Uri uri = activity.getIntent().getData();
        return uri != null;
    }

    /**
     * получение id для достижения при старте через url
     * @param activity
     * @param listener
     */
    public static void startAchievement(Activity activity, final IdListener listener) {
        getId(activity, listener, ACHIEVEMENT_ID);
    }

    /**
     * получение id для мероприятия при старте через url
     * @param activity
     * @param listener
     */
    public static void startEvent(Activity activity, final IdListener listener) {
        getId(activity, listener, EVENT_ID);
    }

    /**
     * получение id для городской новинки при старте через url
     * @param activity
     * @param listener
     */
    public static void startNovelty(Activity activity, final IdListener listener) {
        getId(activity, listener, NOVELTY_ID);
    }

    /**
     * получение ссылки и заголовка для старта экрана браузера аг через url
     * @param activity
     * @param linkListener
     */
    public static void startWebView(Activity activity, final LinkListener linkListener) {
        UriListener uriListener = new UriListener() {
            @Override
            public void onDetected(Uri uri) {
                String link = null, title = null, newsId = null;
                if (NEW_HOST.equalsIgnoreCase(uri.getHost()) || ADVERTISEMENT.equalsIgnoreCase(uri.getHost())) {
                    link = uri.getQueryParameter(LINK);
                    title = uri.getQueryParameter(LINK_TITLE);
                    newsId = uri.getQueryParameter(NEWS_ID);
                    if (newsId == null || TextUtils.isEmpty(newsId) || "null".equalsIgnoreCase(newsId)) {
                        newsId = uri.getQueryParameter(ADVERTISEMENT_ID);
                    }
                } else if (OPEN_HOST.equalsIgnoreCase(uri.getHost())) {
                    link = uri.getQueryParameter("link_url");
                }
                if (linkListener != null) {
                    linkListener.onDetected(title, link, newsId);
                }
            }
        };
        startFromUri(activity, uriListener);
    }

    public static void getId(Activity activity, final IdListener listener, final String id) {
        UriListener uriListener = new UriListener() {
            @Override
            public void onDetected(Uri uri) {
                if (listener != null) {
                    try {
                        Long longId = Long.parseLong(uri.getQueryParameter(id));
                        listener.onDetectedId(longId);
                    } catch (Exception ignored) {
                        listener.onDetectedId(uri.getQueryParameter(id));
                    }

                }
            }
        };
        startFromUri(activity, uriListener);
    }

    public static void startFromUri(Activity activity, UriListener uriListener) {
        Uri uri = activity.getIntent().getData();
        if (uri != null) {
            if (Session.get().hasSession()) {
                /**
                 * Пользователь атворизован, "вытягиваем" из параметров url
                 * нужные данные для работы экрана
                 */
                if (uriListener != null) {
                    uriListener.onDetected(uri);
                }
            } else {
                /**
                 * Если пользователь не авторизован,
                 * то прокидываем его на экран авторизации
                 */
                Intent authIntent = new Intent(activity, AgAuthActivity.class);
                authIntent.setData(uri);
                activity.startActivity(authIntent);
                activity.finish();
            }
        }
    }

    public interface UriListener {
        void onDetected(Uri uri);
    }

    public interface IdListener {
        void onDetectedId(Object id);
    }

    public interface LinkListener {
        void onDetected(String title, String link, String newsId);
    }

    public interface ScreenListListener {
        ScreenListListener STUB = new ScreenListListener() {
            @Override
            public void onNews() {
            }

            @Override
            public void onEvents() {
            }

            @Override
            public void onPollTasks() {
            }

            @Override
            public void onNovelties() {
            }

            @Override
            public void onAchievement(String id) {
            }

            @Override
            public void onInnovation(long id) {
            }
        };
        void onNews();
        void onEvents();
        void onPollTasks();
        void onNovelties();
        void onAchievement(String id);
        void onInnovation(long id);
    }

    public interface TaskListener {
        TaskListener STUB = new TaskListener() {
            @Override
            public void onUpdateSocial() {
            }

            @Override
            public void onPostInSocial() {
            }

            @Override
            public void onInviteFriends() {
            }

            @Override
            public void onBindToPgu() {
            }

            @Override
            public void onRateThisApplication() {
            }

            @Override
            public void onUpdateEmail() {
            }

            @Override
            public void onPersonalTask(String taskId) {
            }
        };
        void onUpdateSocial();
        void onPostInSocial();
        void onInviteFriends();
        void onBindToPgu();
        void onRateThisApplication();
        void onUpdateEmail();
        void onPersonalTask(String taskId);

    }
}
