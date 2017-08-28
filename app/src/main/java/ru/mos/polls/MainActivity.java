package ru.mos.polls;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ilich.juggler.change.Add;
import me.ilich.juggler.states.VoidParams;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.elk.Dialogs;
import ru.mos.elk.api.API;
import ru.mos.polls.about.AboutAppFragment;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.ui.BaseActivity;
import ru.mos.polls.common.controller.LocationController;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.event.controller.EventAPIController;
import ru.mos.polls.event.gui.activity.EventActivity;
import ru.mos.polls.fragments.AgDynamicFragment;
import ru.mos.polls.fragments.MyPointsFragment;
import ru.mos.polls.fragments.NewsDynamicFragment;
import ru.mos.polls.friend.ContactsController;
import ru.mos.polls.friend.ui.fragment.FriendsFragment;
import ru.mos.polls.geotarget.GeotargetApiController;
import ru.mos.polls.geotarget.job.GeotargetJobManager;
import ru.mos.polls.geotarget.manager.AreasManager;
import ru.mos.polls.geotarget.manager.GpsRequestPermsManager;
import ru.mos.polls.geotarget.manager.PrefsAreasManager;
import ru.mos.polls.geotarget.model.Area;
import ru.mos.polls.helpers.FunctionalHelper;
import ru.mos.polls.informer.InformerUIController;
import ru.mos.polls.innovation.gui.activity.InnovationActivity;
import ru.mos.polls.innovation.gui.fragment.ActiveInnovationsFragment;
import ru.mos.polls.navigation.actionbar.ActionBarNavigationController;
import ru.mos.polls.navigation.drawer.NavigationDrawerFragment;
import ru.mos.polls.navigation.drawer.NavigationMenuItem;
import ru.mos.polls.navigation.tab.PagerFragment;
import ru.mos.polls.newprofile.state.EditProfileState;
import ru.mos.polls.newprofile.ui.fragment.ProfileFragment;
import ru.mos.polls.profile.gui.activity.AchievementActivity;
import ru.mos.polls.profile.gui.activity.UpdateSocialActivity;
import ru.mos.polls.quests.ProfileQuestActivity;
import ru.mos.polls.quests.QuestsFragment;
import ru.mos.polls.quests.controller.QuestStateController;
import ru.mos.polls.quests.controller.SmsInviteController;
import ru.mos.polls.settings.SettingsFragment;
import ru.mos.polls.shop.WebShopFragment;
import ru.mos.polls.social.controller.AgSocialApiController;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.support.gui.SupportFragment;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.polls.survey.hearing.gui.activity.PguAuthActivity;
import ru.mos.polls.wizardprofile.state.WizardProfileState;
import ru.mos.social.callback.PostCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.PostValue;
import ru.mos.social.model.social.Social;

import static ru.mos.polls.friend.vm.FriendsFragmentVM.CONTACTS_PERMS;

public class MainActivity extends ToolbarAbstractActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    public static final String IS_TASK = "is_task";
    public static final String MODE = "ru.mos.polls.MODE";
    public static final int MODE_SIMPLE = 0;
    public static final int MODE_SHARE = 1;
    public static final int MODE_PROMO = 2;
    public static final int MODE_NEWS = 3;

    public static final String MESSAGE = "ru.mos.polls.BODY";

    private static final String TAG_QUESTS = "quests";
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_FREE_TIME = "free_time";
    private static final String TAG_MY_POINTS = "my_points";
    private static final String TAG_ABOUT = "my_about";
    private static final String TAG_NEWS = "news";
    private static final String TAG_POLLS = "polls";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_SUPPORT = "support";
    private static final String TAG_NOVELTY = "novelty";
    private static final String TAG_SHOP = "shop";

    private static final int GPS_PERMISSION_REQUEST = 9824;
    private static final String[] GPS_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private SmsInviteController smsInviteController;
    private SocialController socialController;
    private QuestStateController questStateController;
    private NavigationDrawerFragment navFragment;
    private DrawerLayout drawerLayout;
    /**
     * Не используется с {@since 1.9.6}
     * так как откатили android sdk {@link RuntimePermissionController}
     */
    private RuntimePermissionController runtimePermissionController;
    private boolean isGPSEnableDialogShowed;

    private Callback callback;
    private PostCallback postCallback = new PostCallback() {
        @Override
        public void postSuccess(Social social, @Nullable PostValue postValue) {
            SocialUIController.sendPostingResult(MainActivity.this, (AppPostValue) postValue, null);
        }

        @Override
        public void postFailure(Social social, @Nullable PostValue postValue, Exception e) {
            SocialUIController.sendPostingResult(MainActivity.this, (AppPostValue) postValue, e);
        }
    };

    private static Activity instance;

    public static void close() {
        if (instance != null) {
            instance.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        setSupportProgressBarIndeterminateVisibility(false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        actionBarDrawerToggle.syncState();

        socialController = new SocialController(this);
        socialController.getEventController().registerCallback(postCallback);
        smsInviteController = new SmsInviteController(this);
        questStateController = QuestStateController.getInstance();

        navFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        navFragment.selectItem(-1);

        navFragment.refreshUserInfo();

        modeAct(getIntent());
        startFromUri();
        Statistics.mainEnter();
        instance = this;
        AgSocialApiController.loadSocials(this, null);
        runtimePermissionController = new RuntimePermissionController(this);

        updateGeotargetAreas();
        initGeotargetManager();

        subscribeEventsBus();
        findFriends();
    }

    private void findFriends() {
        if (ContactsController.Manager.isNeedUpdate(this)) {
            if (EasyPermissions.hasPermissions(this, CONTACTS_PERMS)) {
                ContactsController contactsController = new ContactsController(this);
                contactsController.silentFindFriends();
                ContactsController.Manager.increment(this);
            }
        }
    }

    @SuppressWarnings("VisibleForTests")
    private void subscribeEventsBus() { //переснести
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.ProfileEvents) {
                        Events.ProfileEvents action = (Events.ProfileEvents) o;
                        switch (action.getAction()) {
                            case Events.ProfileEvents.EDIT_USER_INFO:
                                navigateTo().state(Add.newActivity(new EditProfileState(VoidParams.instance()), BaseActivity.class));
                                break;
                        }
                    }
                });
    }

    private void initGeotargetManager() {
        if (EasyPermissions.hasPermissions(this, GPS_PERMS)) {
            GeotargetJobManager geotargetJobManager = new GeotargetJobManager(this);
            geotargetJobManager.start();
        }
    }

    private void updateGeotargetAreas() {
        GeotargetApiController.OnAreasListener listener = new GeotargetApiController.OnAreasListener() {
            @Override
            public void onLoaded(List<Area> loadedAreas) {
                try {
                    AreasManager areasManager = new PrefsAreasManager(MainActivity.this);
                    areasManager.clear();
                    areasManager.save(loadedAreas);
                } catch (Exception ignored) {
                }
            }
        };
        GeotargetApiController.loadAreas(this, listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocialUIController.registerPostingReceiver(this);
        if (PreviewAppActivity.isNeedPreview(this)) {
            PreviewAppActivity.start(this);
        }
        InformerUIController.process(this);
        /**
         * Проверка доступности работы с местоположением
         */
        if (LocationController.isLocationNetworkProviderEnabled(this)
                || LocationController.isLocationGPSProviderEnabled(this)) {
            if (!EasyPermissions.hasPermissions(this, GPS_PERMS)
                    && GpsRequestPermsManager.isNeedRequestGps(this)) {
                GpsRequestPermsManager.incrementSyncTime(this);
                EasyPermissions.requestPermissions(this,
                        getString(R.string.get_permission),
                        GPS_PERMISSION_REQUEST,
                        GPS_PERMS);

            }
        } else {
            if (!isGPSEnableDialogShowed && GpsRequestPermsManager.isNeedRequestGps(this)) {
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GpsRequestPermsManager.incrementSyncTime(MainActivity.this);
                    }
                };
                LocationController.showDialogEnableLocationProvider(this, cancelListener);
                isGPSEnableDialogShowed = true;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SocialUIController.unregisterPostingReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socialController.getEventController().unregisterAllCallback();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        modeAct(intent);
    }

    private void modeAct(Intent intent) {
        int mode = intent.getIntExtra(MODE, MODE_SIMPLE);
        switch (mode) {
            case MODE_SIMPLE:
                //do nothing
                break;
            case MODE_PROMO:
                Dialogs.showAlertMessage(this, intent.getStringExtra(MESSAGE));
                break;
            case MODE_SHARE:
                navFragment.selectItem(0);//todo replace with constant
                SocialUIController.showSocialsDialog(this, new SocialUIController.SocialClickListener() {
                    @Override
                    public void onClick(Context context, Dialog dialog, AppPostValue appPostValue) {
                        socialController.post(appPostValue, appPostValue.getSocialId());
                    }

                    @Override
                    public void onCancel() {
                        MainActivity.this.finish();
                    }
                });
                break;
            case MODE_NEWS:
                navFragment.selectItem(3);//todo replace with constant
                break;
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int id) {
        Fragment fr = null;
        String tag = null;
        callback = null;
        switch (id) {
            case NavigationMenuItem.POLLS:
                Statistics.enterAllPolls();
                GoogleStatistics.QuestsFragment.enterAllPolls();
                /**
                 * с версии 1.9.2 исопльзуем навигацию через табы
                 */
                fr = PagerFragment.Polls.newInstance();
                break;
            case NavigationMenuItem.SETTINGS:
                Statistics.propertiesFragment();
                GoogleStatistics.AGNavigation.propertiesFragment();
                fr = SettingsFragment.newInstance();
                tag = TAG_SETTINGS;
                break;
            case NavigationMenuItem.SUPPORT:
                fr = SupportFragment.newInstance();
                tag = TAG_SUPPORT;
                break;
            case NavigationMenuItem.QUESTS:
                QuestsFragment qfr = new QuestsFragment();
                fr = qfr;
                qfr.setListener(new QuestsFragment.Listener() {

                    @Override
                    public void onSurvey(long id) {
                        Intent intent = SurveyActivity.getStartIntent(MainActivity.this, id, false);
                        startActivity(intent);
                    }

                    @Override
                    public void onAllSurveys() {
                    }

                    @Override
                    public void onUpdatePersonal() {
                        ProfileQuestActivity.startUpdatePersonal(MainActivity.this);
                    }

                    @Override
                    public void onUpdateLocation() {
                        ProfileQuestActivity.startUpdateLocation(MainActivity.this);
                    }

                    @Override
                    public void onUpdateSocial() {
                        UpdateSocialActivity.startActivityForQuest(MainActivity.this);
                    }

                    @Override
                    public void onUpdateEmail() {
                        EmailQuestActivity.startActivity(MainActivity.this);
                    }

                    @Override
                    public void onUpdateExtraInfo() {
                        ProfileQuestActivity.startUpdateExtraInfo(MainActivity.this);
                    }

                    @Override
                    public void onUpdateFamilyInfo() {
                        ProfileQuestActivity.startUpdateFamilyInfo(MainActivity.this);
                    }

                    @Override
                    public void onBindToPgu() {
                        PguAuthActivity.startActivityForQuest(MainActivity.this);
                    }

                    @Override
                    public void onRateThisApplication(String appId) {
                        FunctionalHelper.startGooglePlay(MainActivity.this, appId);
                    }

                    @Override
                    public void onInviteFriends(boolean isTask) {
                        if (!runtimePermissionController.hasSmsSend()) {
                            runtimePermissionController.requestSmsSend();
                        } else {
                            smsInviteController.process(isTask);
                        }
                    }

                    @Override
                    public void onSocialPost(AppPostValue appPostValue) {
                        Statistics.taskSocialSharing(appPostValue.getSocialName());
                        GoogleStatistics.QuestsFragment.taskSocialSharing(appPostValue.getSocialName());
                        socialController.post(appPostValue, appPostValue.getSocialId());
                    }

                    @Override
                    public void onNews(String title, String linkUrl) {
                    }

                    @Override
                    public void onEvent(long eventId) {
                        EventActivity.startActivity(MainActivity.this, eventId);
                    }


                    /**
                     * TODO убрать!!!! пока повесил сюда вызова визарда в галвное ленте при клик в QuestFragment
                     */
                    @Override
                    public void onOther(String title, String linkUrl) {
                        navigateTo().state(Add.newActivity(new WizardProfileState(), BaseActivity.class));
                    }

                    @Override
                    public void onResults(String title, String linkUrl) {
                    }
                });
                tag = TAG_QUESTS;
                break;
            case NavigationMenuItem.PROFILE:
                /**
                 * с версии 1.9.2 исопльзуем навигацию через табы
                 */
                fr = PagerFragment.Profile.newInstance();
                fr = ProfileFragment.newInstance();
                tag = TAG_PROFILE;
                break;
            case NavigationMenuItem.FRIENDS:
                fr = FriendsFragment.instance();
                tag = TAG_PROFILE;
                break;
            case NavigationMenuItem.MY_FREE_TIME:
                Statistics.enterEvents();
                GoogleStatistics.Events.enterEvents();
                ActionBarNavigationController.setEventNavigation(this);
                /**
                 * Проверяем есть ли текущие мерпориятяи,
                 * если их нет, то перебрасываем на список прошедших
                 */
                EventAPIController.CheckHasCurrentEventsListener listener = new EventAPIController.CheckHasCurrentEventsListener() {
                    @Override
                    public void hasCurrentEvents(boolean hasEvents) {
                        if (!hasEvents) {
                            ActionBarNavigationController.setEventNavigationFromPast(MainActivity.this);
                        }
                    }
                };
                EventAPIController.hasCurrentEvents(this, listener);
                fr = null;
                break;
            case NavigationMenuItem.MY_POINTS:
                fr = MyPointsFragment.newInstance();
//                fr = NewMyPointsFragment.newInstance();
                tag = TAG_MY_POINTS;
                break;
            case NavigationMenuItem.NEWS:
                /**
                 * вызываем Statistics.enterNews() тут, так как AgDynamicFragment,
                 * так как можно исопльзовать не только для новостей
                 */
                Statistics.enterNews();
                GoogleStatistics.AGNavigation.enterNews();
                fr = NewsDynamicFragment.newInstance(getString(R.string.mainmenu_news), "", API.getURL(UrlManager.url(UrlManager.Controller.NEWS, UrlManager.Methods.GET)));
                tag = TAG_NEWS;
                break;
            case NavigationMenuItem.ABOUT:
                AboutAppFragment.SocialListener socialListener = new AboutAppFragment.SocialListener() {
                    @Override
                    public void onSocialPost(AppPostValue socialPostValue) {
                        Statistics.taskSocialSharing(socialPostValue.getSocialName());
                        GoogleStatistics.QuestsFragment.taskSocialSharing(socialPostValue.getSocialName());
                        socialController.post(socialPostValue, socialPostValue.getSocialId());
                    }
                };
                fr = AboutAppFragment.newInstance(socialListener);
                tag = TAG_ABOUT;
                break;
            case NavigationMenuItem.NOVELTY:
//                ActionBarNavigationController.setNoveltyNavigation(this);
                fr = new ActiveInnovationsFragment();
                tag = TAG_NOVELTY;
                Statistics.innovationsListFragment();
                GoogleStatistics.Innovation.innovationsListFragment();
                break;
            case NavigationMenuItem.SHOP:
                Statistics.shopBuy();
                GoogleStatistics.AGNavigation.shopBuy();
                fr = WebShopFragment.newInstance();
                callback = (WebShopFragment) fr;
                tag = TAG_SHOP;
                break;
        }
        if (fr != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fr, tag)
                    .commit();
        }
    }

    private boolean isFirstBack = true;

    @Override
    public void onBackPressed() {
        AgDynamicFragment df = (AgDynamicFragment) getSupportFragmentManager().findFragmentByTag(TAG_NEWS);
        if (df == null || !df.canGoBack()) {
            if (isFirstBack) {
                isFirstBack = false;
                Toast.makeText(this, R.string.one_more_back_to_exit, Toast.LENGTH_SHORT).show();
                new Thread("BackThread") {
                    @Override
                    public void run() {
                        try {
                            sleep(2000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isFirstBack = true;
                    }
                }.start();
            } else {
                isFirstBack = true;
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = super.onKeyDown(keyCode, event);
        if (callback != null) {
            result = callback.onKeyDown(keyCode, event);
        }
        return result;
    }

    @Override
    public void onNavigationDrawerOpened(View drawerView) {
        AbstractActivity.hideSoftInput(this, drawerView);
    }

    @Override
    public void onNavigationDrawerClosed(View drawerView) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (runtimePermissionController.smsReceivePermissionGranted(requestCode, grantResults)) {
            smsInviteController.process(true);
        }
        initGeotargetManager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (smsInviteController.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        socialController.onActivityResult(requestCode, resultCode, data);
    }

    private void startFromUri() {
        UrlSchemeController.ScreenListListener screenListListener = new UrlSchemeController.ScreenListListener() {
            @Override
            public void onNews() {
                navFragment.selectItem(3);
            }

            @Override
            public void onEvents() {
            }

            @Override
            public void onPollTasks() {
                navFragment.selectItem(1);
            }

            @Override
            public void onNovelties() {
                navFragment.selectItem(2);
            }

            @Override
            public void onAchievement(String id) {
                AchievementActivity.getStartActivity(MainActivity.this, id);
            }

            @Override
            public void onInnovation(long id) {
                InnovationActivity.startActivity(MainActivity.this, id);
            }
        };

        UrlSchemeController.TaskListener taskListener = new UrlSchemeController.TaskListener() {
            @Override
            public void onUpdateSocial() {
                startActivity(new Intent(MainActivity.this, UpdateSocialActivity.class).putExtra(IS_TASK, true));
            }

            @Override
            public void onPostInSocial() {
                SocialUIController.showSocialsDialog(MainActivity.this, new SocialUIController.SocialClickListener() {
                    @Override
                    public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                        Statistics.taskSocialSharing(socialPostValue.getSocialName());
                        GoogleStatistics.QuestsFragment.taskSocialSharing(socialPostValue.getSocialName());
                        socialController.post(socialPostValue, socialPostValue.getSocialId());
                    }

                    @Override
                    public void onCancel() {
                        MainActivity.this.finish();
                    }
                });
            }

            @Override
            public void onInviteFriends() {
                smsInviteController.process(true);
            }

            @Override
            public void onBindToPgu() {
                PguAuthActivity.startActivity(MainActivity.this);
            }

            @Override
            public void onRateThisApplication() {
                FunctionalHelper.startGooglePlay(MainActivity.this);
            }

            @Override
            public void onUpdateEmail() {
                EmailQuestActivity.startActivity(MainActivity.this);
            }

            @Override
            public void onPersonalTask(String taskId) {
                ProfileQuestActivity.startActivity(MainActivity.this, taskId);
            }
        };
        UrlSchemeController.start(this, screenListListener, taskListener);
    }

    public interface Callback {
        boolean onKeyDown(int keyCode, KeyEvent event);
    }
}
