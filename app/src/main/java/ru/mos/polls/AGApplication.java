package ru.mos.polls;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDexApplication;

import com.android.volley2.VolleyLog;
import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.L;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;

import java.io.File;
import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.api.Token;
import ru.mos.elk.db.UserData;
import ru.mos.elk.db.UserDataProvider;
import ru.mos.elk.push.GCMBroadcastReceiver;
import ru.mos.elk.push.PushChannel;
import ru.mos.polls.base.rxjava.RxEventBus;
import ru.mos.polls.geotarget.GeotargetApiController;
import ru.mos.polls.geotarget.manager.AreasManager;
import ru.mos.polls.geotarget.manager.PrefsAreasManager;
import ru.mos.polls.geotarget.model.Area;
import ru.mos.polls.innovations.ui.activity.InnovationActivity;
import ru.mos.polls.profile.ui.activity.AchievementActivity;
import ru.mos.polls.rxhttp.rxapi.config.AgApi;
import ru.mos.polls.rxhttp.rxapi.config.AgApiBuilder;
import ru.mos.polls.rxhttp.session.Session;
import ru.mos.polls.social.storable.AppStorable;
import ru.mos.polls.subscribes.manager.SubscribeManager;
import ru.mos.polls.survey.SharedPreferencesSurveyManager;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.social.model.Configurator;
import ru.mos.social.utils.SocialInitUtils;


public class AGApplication extends MultiDexApplication {

    /**
     * Исключительно для отладки! Нормальное значение - true
     * Если true, то после прерывания вопрос начнётся с той же страницы, на которой был прерван.
     * Если false - начнётся с первой.
     */
    public static final boolean SET_PAGE_ON_SURVEY_FILL = true;

    /**
     * Исключительно для отладки! Нормальное значение - false
     * Если true, то используется заглушка вместо запросов к веб-сервису pool/get
     * Если false, то используются нормальные сервисы.
     */
    public static final boolean USE_STUB_SURVEY_DATASOURCE = false;

    /**
     * Исключительно для отладки! Нормальное значение - false
     * Если true, то открывается уат веб-магазин
     * Если false, то открывается нормальный веб-магазин.
     */
    public static final boolean WEB_SHOP_UAT = false;//BuildConfig.DEBUG;

    /**
     * Флаг включения/отключения работы с google+
     * Если сс не поддерживает функции работы с google+ на момент релиза, значние - false
     */
    public static final boolean IS_GOOGLE_PLUS_ENABLE = false;

    /**
     * Если доступно апи соцсетей версии 0.3, то включаем его - true, елси нет
     * то отключаем - false, будет исопльзовано апи 0.2
     */
    public static final boolean IS_SOCIAL_API_V03_ENABLE = true;

    /**
     * Ограничение на постинг в соцсетях
     */
    public static final boolean IS_POSTING_LIMITATION = true;

    /**
     * С версии 1.9.6 отключены подписки по каждому голосованию и мероприятию
     * Возможно в будущем вернется
     * Чтобы включить возможность настройки подписок и их отправки, флаг нужно выставить в true
     *
     * @since 1.9.6
     */
    public static final boolean IS_LOCAL_SUBSCRIBE_ENABLE = false;

    private static Tracker tracker;
    private static GoogleAnalytics googleAnalytics;

    private static ImageLoader imageLoader;
    private File cacheDir;

    public static synchronized Tracker getTracker() {
        return tracker;
    }

    private void initGoogleAnalytics() {
        googleAnalytics = GoogleAnalytics.getInstance(this);
        googleAnalytics.setLocalDispatchPeriod(1800);
        tracker = googleAnalytics.newTracker(/*"UA-52662983-3"*/R.xml.analytics_global_config);
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initGoogleAnalytics();
//        Crashlytics crashlyticsKit = new Crashlytics.Builder()
//                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
//                .build();
//        TwitterAuthConfig config = new TwitterAuthConfig(getString(R.string.tw_consumer_key), getString(R.string.tw_consumer_secret));
//
//        Fabric.with(this, crashlyticsKit, new TwitterCore(config));
        SocialInitUtils.init(getApplicationContext());
        AppStorable storable = new AppStorable(this);
        Configurator.getInstance(this).setStorable(storable);

        BaseActivity.setFlurryKey(getString(R.string.ag_flurry_key));
        AppsFlyerLib.setAppsFlyerKey("TvVxifM6uMLnGmiZEwifAi");

        if (BuildConfig.BUILD_TYPE.equals("customer")) {
            Token.AG.setCustomToken("ag_uat_token3");
            Token.AG.setIsCustom(true);
        } else if (BuildConfig.BUILD_TYPE.equals("release")) {
            API.setIsDebug(false);
        } else {
            API.setIsDebug(true);
        }
        API.setToken(Token.AG);

//        API.registerPush(this);

        API.setBuildVersionName(UrlManager.V250);
        VolleyLog.setIsDebug(!BuildConfig.BUILD_TYPE.equals("release"));

        pushRegister();

        getContentResolver().query(UserDataProvider.getContentWithLimitUri(UserData.Cars.URI_CONTENT, 1), null, null, null, null).close(); //work around falling when query db before created

        BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Context cntx = getApplicationContext();
                PointsManager.clearPointsAndHistory(cntx);
                Configurator.getInstance(cntx).getStorable().clearAll();
                SubscribeManager.clear(cntx);
                CustomDialogController.clear(cntx);
                new SharedPreferencesSurveyManager(cntx).removeAll();
                Intent i = new Intent(getApplicationContext(), AgAuthActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        registerReceiver(logoutReceiver, new IntentFilter(BaseActivity.INTENT_LOGOUT));
        BaseActivity.addAuthRequire(MainActivity.class);

        imageLoader = ImageLoader.getInstance();
        cacheDir = StorageUtils.getCacheDirectory(this);
        imageLoader.init(getLoaderConfig());
        L.writeDebugLogs(!BuildConfig.BUILD_TYPE.equals("release")); //отключение логгирования для imageLoader

        Session.init(getApplicationContext());
        /**
         * todo
         * Отключить, когда полность перейдем на использование {@link Session}
         * Временная синхронизация сессий между стармы и новыми фреймворками
         */
        if (ru.mos.elk.netframework.request.Session.isAuthorized(this)) {
            Session.get().setSession(ru.mos.elk.netframework.request.Session.getSession(this));
        }
//        component = DaggerAppComponent.builder().build();
        /**
         * Пока не удалось перенести инициализацию
         * {@link android.arch.persistence.room.RoomDatabase} в {@link AppComponent}
         */

        api = AgApiBuilder.build();
        bus = new RxEventBus();
    }

    public static AgApi api;
    private static RxEventBus bus;

    public static RxEventBus bus() {
        return bus;
    }

    /**
     * Метод инициализации пушей
     */
    private void pushRegister() {

        /**
         * Перед айди групп стоит цифра, т.к. идет сортировка в настройках приложения
         * Группа "другое"
         */
        String otherGroupId = "2other_group";
        int otherGroupName = R.string.push_group_other;
        createChannelGroup(getApplicationContext(), otherGroupId, otherGroupName);

        PushChannel promoChannel = new PushChannel("promo", R.string.promo_push_string, otherGroupId);
        createChannel(getApplicationContext(), promoChannel);
        GCMBroadcastReceiver.addAction("promo", getPromoAction(promoChannel));

        PushChannel shareChannel = new PushChannel("share", R.string.share_push_string, otherGroupId);
        createChannel(getApplicationContext(), shareChannel);
        GCMBroadcastReceiver.addAction("share", getShareAction(shareChannel));

        PushChannel achievementChannel = new PushChannel("achievement_recd", R.string.achievement_push_string, otherGroupId);
        createChannel(getApplicationContext(), achievementChannel);
        GCMBroadcastReceiver.addAction("achievement_recd", getNewAchievementAction(achievementChannel));

        PushChannel geotargetRemovePollChannel = new PushChannel("geotarget_area_remove", R.string.geotarget_area_remove_push_string, otherGroupId);
        createChannel(getApplicationContext(), geotargetRemovePollChannel);
        GCMBroadcastReceiver.addAction("geotarget_area_remove", removeGeotargetArea(geotargetRemovePollChannel));

        PushChannel geotargetUpdatePollChannel = new PushChannel("geotarget_areas_update", R.string.geotarget_area_update_push_string, otherGroupId);
        createChannel(getApplicationContext(), geotargetUpdatePollChannel);
        GCMBroadcastReceiver.addAction("geotarget_areas_update", updateGeotargetAreas(geotargetUpdatePollChannel));

        /**
         * Группа "Новости"
         */
        String newsGroupId = "1news_group";
        int newsGroupName = R.string.push_group_news;
        createChannelGroup(getApplicationContext(), newsGroupId, newsGroupName);

        PushChannel moscowNewsChannel = new PushChannel("moscow_news", R.string.moscow_news_push_string, newsGroupId);
        createChannel(getApplicationContext(), moscowNewsChannel);
        GCMBroadcastReceiver.addAction("moscow_news", getNewsAction(moscowNewsChannel));

        PushChannel newsPollChannel = new PushChannel("ag_poll_news", R.string.news_poll_push_string, newsGroupId);
        createChannel(getApplicationContext(), newsPollChannel);
        GCMBroadcastReceiver.addAction("ag_poll_news", getAgNew(newsPollChannel));

        /**
         * Группа "Голосования"
         */
        String pollsGroupId = "0polls_group";
        int pollsGroupName = R.string.push_group_polls;
        createChannelGroup(getApplicationContext(), pollsGroupId, pollsGroupName);

        PushChannel noveltyChannel = new PushChannel("novelty_new", R.string.novelty_push_string, pollsGroupId);
        createChannel(getApplicationContext(), noveltyChannel);
        GCMBroadcastReceiver.addAction("novelty_new", getNewNoveltyAction(noveltyChannel));

        PushChannel hearingChannel = new PushChannel("hearing_new", R.string.hearing_push_string, pollsGroupId);
        createChannel(getApplicationContext(), hearingChannel);
        GCMBroadcastReceiver.addAction("hearing_new", getNewHearing(hearingChannel));

        PushChannel pollChannel = new PushChannel("ag_new", R.string.new_poll_push_string, pollsGroupId);
        createChannel(getApplicationContext(), pollChannel);
        GCMBroadcastReceiver.addAction("ag_new", getNewPollAction(pollChannel));


    }

    /**
     * Метод, инициализирующий каналы пушей для Android 8 и выше
     *
     * @param appContext контекст приложения
     * @param channel   канал пуша для создания
     */
    private static void createChannel(Context appContext, @NonNull PushChannel channel) {
        if (appContext != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                String channelId = channel.getId();
                String channelName = appContext.getString(channel.getChannelNameId());
                String channelGroupId = channel.getGroupId();
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                /**
                 * указываем группу для уведомаления
                 */
                notificationChannel.setGroup(channelGroupId);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    /**
     * Метод, инициализирующий группы уведовмлений для Android 8 и выше
     *
     * @param appContext контекст приложения
     * @param channelGroupId айди канала
     * @param channelGroupName название канала
     */
    private static void createChannelGroup(Context appContext, String channelGroupId, @StringRes int channelGroupName) {
        if (appContext != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                /**
                 * Создаем группу
                 */
                NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(channelGroupId, appContext.getString(channelGroupName));
                notificationManager.createNotificationChannelGroup(notificationChannelGroup);
            }
        }
    }

    public GCMBroadcastReceiver.PushAction getNewHearing(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {
            public long hearingId;

            @Override
            public void onPushRecived(Intent intent) {
                hearingId = Long.parseLong(intent.getStringExtra("hearing_id"));
            }

            @Override
            public Intent getNotifyIntent() {
                Intent intent = SurveyActivity.getStartIntent(AGApplication.this, hearingId, true);
                return intent;
            }

            @Override
            public boolean isAuthRequired() {
                return true;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return false;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }
        };
    }

    public GCMBroadcastReceiver.PushAction getPromoAction(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {
            public String message;

            @Override
            public void onPushRecived(Intent intent) {
                message = intent.getStringExtra("msgBody");
            }

            @Override
            public Intent getNotifyIntent() {
                Intent intent = new Intent(AGApplication.this, MainActivity.class);
                intent.putExtra(MainActivity.MODE, MainActivity.MODE_PROMO);
                intent.putExtra(MainActivity.MESSAGE, message);
                return intent;
            }

            @Override
            public boolean isAuthRequired() {
                return false;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return false;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }
        };
    }

    public GCMBroadcastReceiver.PushAction getShareAction(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {

            @Override
            public void onPushRecived(Intent intent) {
            }

            @Override
            public Intent getNotifyIntent() {
                Intent intent = new Intent(AGApplication.this, MainActivity.class);
                intent.putExtra(MainActivity.MODE, MainActivity.MODE_SHARE);
                return intent;
            }

            @Override
            public boolean isAuthRequired() {
                return false;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return false;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }
        };
    }

    public GCMBroadcastReceiver.PushAction getNewPollAction(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {
            public long pollId;
            private String areaIds;

            @Override
            public void onPushRecived(Intent intent) {
                if (!"null".equals(intent.getStringExtra("poll_id"))) {
                    pollId = Long.parseLong(intent.getStringExtra("poll_id"));
                }
                if (!"null".equalsIgnoreCase(intent.getStringExtra("area_ids"))) {
                    areaIds = intent.getStringExtra("area_ids");
                    try {
                        JSONArray areaIdsJsonArray = new JSONArray(areaIds);
                        AreasManager areasManager = new PrefsAreasManager(getApplicationContext());
                        for (int i = 0; i < areaIdsJsonArray.length(); ++i) {
                            int id = areaIdsJsonArray.optInt(i);
                            areasManager.remove(id);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public Intent getNotifyIntent() {
                Intent intent = SurveyActivity.getStartIntent(AGApplication.this, pollId, false);
                return intent;
            }

            @Override
            public boolean isAuthRequired() {
                return true;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return pollId == 0;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }
        };
    }

    public GCMBroadcastReceiver.PushAction getNewNoveltyAction(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {
            public long id;

            @Override
            public void onPushRecived(Intent intent) {
                id = Long.parseLong(intent.getStringExtra("novelty_id"));
            }

            @Override
            public Intent getNotifyIntent() {
                Intent intent = InnovationActivity.getStartIntent(AGApplication.this, id);
                return intent;
            }

            @Override
            public boolean isAuthRequired() {
                return true;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return false;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }
        };
    }

    public GCMBroadcastReceiver.PushAction getNewAchievementAction(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {
            public String id;

            @Override
            public void onPushRecived(Intent intent) {
                id = intent.getStringExtra("achievement_id");
            }

            @Override
            public Intent getNotifyIntent() {
                return AchievementActivity.getStartActivity(AGApplication.this, id);
            }

            @Override
            public boolean isAuthRequired() {
                return true;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return false;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }
        };
    }

    public GCMBroadcastReceiver.PushAction getNewsAction(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {

            @Override
            public void onPushRecived(Intent intent) {
            }

            @Override
            public Intent getNotifyIntent() {
                Intent intent = new Intent(AGApplication.this, MainActivity.class);
                intent.putExtra(MainActivity.MODE, MainActivity.MODE_NEWS);
                return intent;
            }

            @Override
            public boolean isAuthRequired() {
                return true;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return false;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }
        };
    }

    public GCMBroadcastReceiver.PushAction getAgNew(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {
            private String id;
            private String link, linkTitle;

            @Override
            public void onPushRecived(Intent intent) {
                id = intent.getStringExtra("id");
                link = intent.getStringExtra("link");
                linkTitle = intent.getStringExtra("link_title");
            }

            @Override
            public Intent getNotifyIntent() {
                return WebViewActivity.getIntentForNew(AGApplication.this, id, linkTitle, link);
            }

            @Override
            public boolean isAuthRequired() {
                return true;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return false;
            }

            public boolean isSilent() {
                return false;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }
        };
    }

    public GCMBroadcastReceiver.PushAction removeGeotargetArea(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {
            private int id;


            @Override
            public void onPushRecived(Intent intent) {
                try {
                    id = Integer.parseInt(intent.getStringExtra("id"));
                } catch (Exception ignored) {
                }
                AreasManager areasManager = new PrefsAreasManager(getApplicationContext());
                areasManager.remove(id);
            }

            @Override
            public Intent getNotifyIntent() {
                return null;
            }

            @Override
            public boolean isAuthRequired() {
                return true;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return true;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }

        };
    }

    public GCMBroadcastReceiver.PushAction updateGeotargetAreas(PushChannel pushChannel) {
        return new GCMBroadcastReceiver.PushAction() {

            @Override
            public void onPushRecived(Intent intent) {
                AreasManager areasManager = new PrefsAreasManager(getApplicationContext());
                areasManager.clear();
                GeotargetApiController.loadAreas(getApplicationContext(), new GeotargetApiController.OnAreasListener() {
                    @Override
                    public void onLoaded(List<Area> loadedAreas) {
                        AreasManager areasManager = new PrefsAreasManager(getApplicationContext());
                        areasManager.save(loadedAreas);
                    }
                });
            }

            @Override
            public Intent getNotifyIntent() {
                return null;
            }

            @Override
            public boolean isAuthRequired() {
                return true;
            }

            @Override
            public int getSmallIcon() {
                return getNotificationIcon();
            }

            @Override
            public int getLargeIcon() {
                return getLargeNotificationIcon();
            }

            @Override
            public boolean isPushNotValid() {
                return true;
            }

            @Override
            public PushChannel getPushChanel() {
                return pushChannel;
            }

        };
    }

    private int getNotificationIcon() {
        return R.drawable.ic_push_white_v230;
    }

    private int getLargeNotificationIcon() {
        return R.drawable.nb_top_icon_v230;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    private ImageLoaderConfiguration getLoaderConfig() {
        return new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(320, 600)
                .threadPoolSize(4)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(20 * 1024 * 1024))
                .diskCacheSize(20 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .imageDownloader(new BaseImageDownloader(this))
                .imageDecoder(new BaseImageDecoder(true))
                .defaultDisplayImageOptions(getOptions())
                .build();
    }

    private DisplayImageOptions getOptions() {
        return new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(1000))
                .build();
    }

}
