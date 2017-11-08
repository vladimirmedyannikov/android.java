package ru.mos.polls;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.elk.Dialogs;
import ru.mos.elk.api.API;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.ProfileManager;
import ru.mos.polls.about.ui.fragment.AboutAppFragment;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.ui.BaseActivity;
import ru.mos.polls.common.controller.LocationController;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.event.controller.EventAPIController;
import ru.mos.polls.event.gui.activity.EventActivity;
import ru.mos.polls.fragments.AgDynamicFragment;
import ru.mos.polls.fragments.NewsDynamicFragment;
import ru.mos.polls.friend.ContactsController;
import ru.mos.polls.friend.state.FriendProfileState;
import ru.mos.polls.friend.ui.fragment.FriendsFragment;
import ru.mos.polls.geotarget.GeotargetApiController;
import ru.mos.polls.geotarget.job.GeotargetJobManager;
import ru.mos.polls.geotarget.manager.AreasManager;
import ru.mos.polls.geotarget.manager.GpsRequestPermsManager;
import ru.mos.polls.geotarget.manager.PrefsAreasManager;
import ru.mos.polls.geotarget.model.Area;
import ru.mos.polls.helpers.FunctionalHelper;
import ru.mos.polls.informer.InformerUIController;
import ru.mos.polls.innovations.ui.activity.InnovationActivity;
import ru.mos.polls.innovations.ui.fragment.InnovationFragment;
import ru.mos.polls.mypoints.ui.NewMyPointsFragment;
import ru.mos.polls.navigation.actionbar.ActionBarNavigationController;
import ru.mos.polls.navigation.drawer.NavigationDrawerFragment;
import ru.mos.polls.navigation.drawer.NavigationMenuItem;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.ui.PollFragment;
import ru.mos.polls.profile.state.EditProfileState;
import ru.mos.polls.profile.state.QuestProfileState;
import ru.mos.polls.profile.ui.activity.AchievementActivity;
import ru.mos.polls.profile.ui.activity.UpdateSocialActivity;
import ru.mos.polls.profile.ui.fragment.ProfileFragment;
import ru.mos.polls.quests.ProfileQuestActivity;
import ru.mos.polls.quests.QuestsFragment;
import ru.mos.polls.quests.controller.QuestStateController;
import ru.mos.polls.quests.controller.SmsInviteController;
import ru.mos.polls.shop.WebShopFragment;
import ru.mos.polls.social.controller.AgSocialApiController;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.polls.survey.hearing.gui.activity.PguAuthActivity;
import ru.mos.polls.survey.variants.ActionSurveyVariant;
import ru.mos.polls.util.SMSUtils;
import ru.mos.polls.wizardprofile.state.WizardProfileState;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;
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
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 987;
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

    /**
     * после успешной отправки смс, сообщаем серверу (работает и для обращения с ленты и из списка друзей)
     */
    private BroadcastReceiver smsSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            smsInviteController.notifyBackEnd(intent.getStringExtra(SMSUtils.SENDING_PHONE_NUMBER));
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

    @AfterPermissionGranted(CONTACTS_PERMISSION_REQUEST_CODE)
    private void findFriends() {
        if (ContactsController.Manager.isNeedUpdate(this)) {
            if (EasyPermissions.hasPermissions(this, CONTACTS_PERMS)) {
                ContactsController contactsController = new ContactsController(this);
                contactsController.setPhone(AgUser.getPhone(this));
                contactsController.silentFindFriends();
                ContactsController.Manager.increment(this);
            } else {
                EasyPermissions.requestPermissions(this,
                        getString(R.string.permission_contacts),
                        CONTACTS_PERMISSION_REQUEST_CODE,
                        CONTACTS_PERMS);
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
                        switch (action.getEventType()) {
                            case Events.ProfileEvents.EDIT_USER_INFO:
                                navigateTo().state(Add.newActivity(new EditProfileState(VoidParams.instance()), BaseActivity.class));
                                break;
                        }
                    }
                    if (o instanceof Events.FriendEvents) {
                        Events.FriendEvents action = (Events.FriendEvents) o;
                        if (action.getId() == Events.FriendEvents.FRIEND_START_PROFILE) {
                            navigateTo().state(Add.newActivity(new FriendProfileState(action.getFriend()), BaseActivity.class));
                        }
                    }
                    if (o instanceof Events.APPEvents) {
                        Events.APPEvents action = (Events.APPEvents) o;
                        switch (action.getEventType()) {
                            case Events.APPEvents.UNAUTHORIZED:
                                ProfileManager.afterLoggedOut(this, AgAuthActivity.class, MainActivity.class);
                                break;
                        }
                    }
                    if (o instanceof Events.PollEvents) {
                        Events.PollEvents action = (Events.PollEvents) o;
                        switch (action.getEventType()) {
                            case Events.PollEvents.OPEN_POLL:
                                action.getPoll();
                                SurveyActivity.startActivityForResult(this, action.getPoll().getId(), Kind.isHearing(action.getPoll().getKind()));
                                break;
                        }
                    }
                    if (o instanceof Events.InnovationsEvents) {
                        Events.InnovationsEvents action = (Events.InnovationsEvents) o;
                        switch (action.getEventType()) {
                            case Events.InnovationsEvents.OPEN_INNOVATIONS:
                                InnovationActivity.startActivity(this, action.getInnovationId());
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
        LocalBroadcastManager.getInstance(this).registerReceiver(smsSuccessReceiver, new IntentFilter(SMSUtils.SUCCESS_SEND_INVITE_MESSAGE_FILTER));
        socialController.getEventController().registerCallback(postCallback);
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsSuccessReceiver);
        SocialUIController.unregisterPostingReceiver(this);
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
                fr = PollFragment.newInstance();
                break;
            case NavigationMenuItem.SETTINGS:
                Statistics.propertiesFragment();
                GoogleStatistics.AGNavigation.propertiesFragment();
                fr = ru.mos.polls.settings.ui.fragment.SettingsFragment.instance();
                tag = TAG_SETTINGS;
                break;
            case NavigationMenuItem.SUPPORT:
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
                    }

                    @Override
                    public void onUpdateLocation() {
                    }

                    @Override
                    public void onUpdateSocial() {
                        UpdateSocialActivity.startActivityForQuest(MainActivity.this);
                    }

                    @Override
                    public void onUpdateEmail() {
                    }

                    @Override
                    public void onUpdateExtraInfo() {
                    }

                    @Override
                    public void onUpdateFamilyInfo() {
                    }

                    @Override
                    public void onBindToPgu() {
                        PguAuthActivity.startActivityForQuest(MainActivity.this);
                    }

                    @Override
                    public void onWizardProfile(List<String> list, int percent) {
                        navigateTo().state(Add.newActivityForResult(new WizardProfileState(list, percent), BaseActivity.class, WizardProfileFragment.RESULT_CODE_START_PROFILE_FOR_INFO_PAGE));
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

                    @Override
                    public void onOther(String title, String linkUrl) {

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
                fr = ProfileFragment.newInstance(ProfileFragment.PAGE_START_PROFILE);
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
                fr = NewMyPointsFragment.newInstance();
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
                ru.mos.polls.about.vm.AboutAppFragmentVM.SocialListener socialListener = new ru.mos.polls.about.vm.AboutAppFragmentVM.SocialListener() {
                    @Override
                    public void onSocialPost(AppPostValue socialPostValue) {
                        Statistics.taskSocialSharing(socialPostValue.getSocialName());
                        GoogleStatistics.QuestsFragment.taskSocialSharing(socialPostValue.getSocialName());
                        socialController.post(socialPostValue, socialPostValue.getSocialId());
                    }
                };
                fr = AboutAppFragment.instance(socialListener);
                tag = TAG_ABOUT;
                break;
            case NavigationMenuItem.NOVELTY:
                fr = InnovationFragment.newInstance();
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
        if (data != null) {
            smsInviteController.onActivityResult(requestCode, resultCode, data);
            socialController.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == WizardProfileFragment.RESULT_CODE_START_PROFILE_FOR_INFO_PAGE) {
            navFragment.selectItem(-1);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ProfileFragment.newInstance(ProfileFragment.PAGE_INFO_PROFILE), TAG_PROFILE)
                    .commit();
        }
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
