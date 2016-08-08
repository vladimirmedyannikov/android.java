package ru.mos.polls.badge.manager;

import android.content.IntentFilter;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.badge.controller.BadgeApiController;
import ru.mos.polls.badge.model.Badge;
import ru.mos.polls.badge.model.BadgesSource;

/**
 * Инкапсулирует
 * 1) кеширование данных по бейджам,
 * 2) синхронизацию данных по бейджам с сервера {@link ru.mos.polls.badge.controller.BadgeApiController}
 *
 * @since 1.9.2
 */
public abstract class BadgeManager {

    public static final String ACTION_RELOAD_BAGES_FROM_SERVER = "RELOAD_BAGES_FROM_SERVER";
    public static final String ACTION_RELOAD_BAGES_FROM_CACHE = "RELOAD_BAGES_FROM_CACHE";
    public static final String ACTION_RELOAD_AVATAR_FROM_CACHE = "RELOAD_AVATAR_FROM_CACHE";

    public static final IntentFilter RELOAD_FROM_SERVER_INTENT_FILTER = new IntentFilter(ACTION_RELOAD_BAGES_FROM_SERVER);
    public static final IntentFilter RELOAD_FROM_CACHE_INTENT_FILTER = new IntentFilter(ACTION_RELOAD_BAGES_FROM_CACHE);
    public static final IntentFilter RELOAD_AVATAR_FROM_CACHE_INTENT_FILTER = new IntentFilter(ACTION_RELOAD_AVATAR_FROM_CACHE);

    /**
     * Помечаем идентификатор новости как прочитанный локально<br/>
     * Ранее при скроллинге экрана списка новостей  {@link ru.mos.polls.fragments.NewsDynamicFragment} полученная новость помечалась как прочитанная локально<br/>
     * С версии 1.9.6 при входе на экран списка новостей все новости помечаются как прочитанные {@link #uploadAllNewsAsReaded(BaseActivity)} на стороне сервера
     *
     * @param id идентификатор новости
     */
    public static void markNewsAsReaded(long id) {
        BadgesSource.getInstance().markNewsAsReaded(id);
    }

    /**
     * Отправляем на сервере список идентификаторов прочитанных новостей {@link #markNewsAsReaded(long)}
     *
     * @param activity elk экран {@link BaseActivity}
     */
    public static void uploadReadedNews(BaseActivity activity) {
        long[] ids = BadgesSource.getInstance().getReadedNewsIds();
        BadgeApiController.updateNews(activity, ids);
    }

    /**
     * Помечаем все новости как прочитанные на сервере
     *
     * @param elkActivity elk экран {@link BaseActivity}
     * @since 1.9.6
     */
    public static void uploadAllNewsAsReaded(BaseActivity elkActivity) {
        Badge badge = BadgesSource.getInstance().getBadgeNews();
        if (badge != null && badge.hasIds()) {
            BadgeApiController.updateNews(elkActivity, badge.getIds());
        }
    }

}
