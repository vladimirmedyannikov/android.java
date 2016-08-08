package ru.mos.polls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.appsflyer.AppsFlyerLib;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
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
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import io.fabric.sdk.android.Fabric;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.api.Token;
import ru.mos.elk.db.UserData;
import ru.mos.elk.db.UserDataProvider;
import ru.mos.elk.push.GCMBroadcastReceiver;
import ru.mos.polls.innovation.gui.activity.InnovationActivity;
import ru.mos.polls.profile.gui.activity.AchievementActivity;
import ru.mos.polls.profile.gui.fragment.ProfileFragment;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.subscribes.manager.SubscribeManager;
import ru.mos.polls.survey.SharedPreferencesSurveyManager;
import ru.mos.polls.survey.SurveyActivity;


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

    private Tracker tracker;

    private static ImageLoader imageLoader;
    private File cacheDir;

    @Deprecated
    public synchronized Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(this);
            googleAnalytics.setLocalDispatchPeriod(1800);

            tracker = googleAnalytics.newTracker("UA-52662983-3"/*R.xml.analytics_global_config*/);
            tracker.enableExceptionReporting(true);
            tracker.enableAdvertisingIdCollection(true);
            tracker.enableAutoActivityTracking(true);
        }
        return tracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, crashlyticsKit);

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

        API.registerPush(this);

        API.setBuildVersionName(BuildConfig.VERSION_NAME);

        GCMBroadcastReceiver.addAction("promo", getPromoAction());
        GCMBroadcastReceiver.addAction("share", getShareAction());
        GCMBroadcastReceiver.addAction("ag_new", getNewPollAction());
        GCMBroadcastReceiver.addAction("moscow_news", getNewsAction());
        GCMBroadcastReceiver.addAction("novelty_new", getNewNoveltyAction());
        GCMBroadcastReceiver.addAction("achievement_recd", getNewAchievementAction());
        GCMBroadcastReceiver.addAction("hearing_new", getNewHearing());
        GCMBroadcastReceiver.addAction("ag_poll_news", getAgNew());

        getContentResolver().query(UserDataProvider.getContentWithLimitUri(UserData.Cars.URI_CONTENT, 1), null, null, null, null).close(); //work around falling when query db before created

        BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Context cntx = getApplicationContext();
                PointsManager.clearPointsAndHistory(cntx);
                SocialManager.clearAll(cntx);
                SubscribeManager.clear(cntx);
                CustomDialogController.clear(cntx);
                ProfileFragment.clearSyncTime(cntx);
                new SharedPreferencesSurveyManager(cntx).removeAll();
            }
        };
        registerReceiver(logoutReceiver, new IntentFilter(BaseActivity.INTENT_LOGOUT));
        BaseActivity.addAuthRequire(MainActivity.class);

        imageLoader = ImageLoader.getInstance();
        cacheDir = StorageUtils.getCacheDirectory(this);
        imageLoader.init(getLoaderConfig());

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public GCMBroadcastReceiver.PushAction getNewHearing() {
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
        };
    }

    public GCMBroadcastReceiver.PushAction getPromoAction() {
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
        };
    }

    public GCMBroadcastReceiver.PushAction getShareAction() {
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
        };
    }

    public GCMBroadcastReceiver.PushAction getNewPollAction() {
        return new GCMBroadcastReceiver.PushAction() {
            public long pollId;

            @Override
            public void onPushRecived(Intent intent) {
                if (!"null".equals(intent.getStringExtra("poll_id"))) {
                    pollId = Long.parseLong(intent.getStringExtra("poll_id"));
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
        };
    }

    public GCMBroadcastReceiver.PushAction getNewNoveltyAction() {
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
        };
    }

    public GCMBroadcastReceiver.PushAction getNewAchievementAction() {
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
        };
    }

    public GCMBroadcastReceiver.PushAction getNewsAction() {
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
        };
    }

    public GCMBroadcastReceiver.PushAction getAgNew() {
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
        };
    }

    private int getNotificationIcon() {
        return R.drawable.ic_push_white;
    }

    private int getLargeNotificationIcon() {
        return R.drawable.nb_top_icon;
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
                .memoryCacheSize(10 * 1024 * 1024)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .discCacheSize(10 * 1024 * 1024)
                .discCacheFileCount(25)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .imageDownloader(new BaseImageDownloader(this))
                .imageDecoder(new BaseImageDecoder(true))
                .defaultDisplayImageOptions(getOptions())
                .writeDebugLogs()
                .build();
    }

    private DisplayImageOptions getOptions() {
        return new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(1000))
                .build();
    }

}
