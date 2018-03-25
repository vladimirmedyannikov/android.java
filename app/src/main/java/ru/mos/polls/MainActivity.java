package ru.mos.polls;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.ilich.juggler.change.Add;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.polls.about.ui.fragment.AboutAppFragment;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.common.controller.LocationController;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.electronichouse.ui.fragment.ElectronicHouseFragment;
import ru.mos.polls.event.controller.EventApiControllerRX;
import ru.mos.polls.event.gui.activity.EventActivity;
import ru.mos.polls.friend.ContactsController;
import ru.mos.polls.friend.ui.fragment.FriendsFragment;
import ru.mos.polls.geotarget.GeotargetApiControllerRX;
import ru.mos.polls.geotarget.job.GeotargetJobManager;
import ru.mos.polls.geotarget.manager.AreasManager;
import ru.mos.polls.geotarget.manager.GpsRequestPermsManager;
import ru.mos.polls.geotarget.manager.PrefsAreasManager;
import ru.mos.polls.geotarget.model.Area;
import ru.mos.polls.helpers.FunctionalHelper;
import ru.mos.polls.informer.InformerUIController;
import ru.mos.polls.innovations.state.InnovationState;
import ru.mos.polls.innovations.ui.fragment.InnovationsFragment;
import ru.mos.polls.mypoints.ui.NewMyPointsFragment;
import ru.mos.polls.navigation.actionbar.ActionBarNavigationController;
import ru.mos.polls.navigation.drawer.NavigationDrawerFragment;
import ru.mos.polls.navigation.drawer.NavigationMenuItem;
import ru.mos.polls.news.ui.NewsFragment;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.poll.ui.PollFragment;
import ru.mos.polls.profile.ProfileManagerRX;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.state.BindingSocialState;
import ru.mos.polls.profile.state.EditProfileState;
import ru.mos.polls.profile.ui.activity.AchievementActivity;
import ru.mos.polls.profile.ui.fragment.ProfileFragment;
import ru.mos.polls.quests.ProfileQuestActivity;
import ru.mos.polls.quests.controller.QuestStateController;
import ru.mos.polls.quests.controller.QuestsApiControllerRX;
import ru.mos.polls.quests.controller.SmsInviteControllerRX;
import ru.mos.polls.quests.vm.QuestsFragmentVM;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.shop.ui.WebShopFragment;
import ru.mos.polls.social.controller.SocialApiControllerRX;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.polls.survey.hearing.gui.activity.PguAuthActivity;
import ru.mos.polls.survey.state.SurveyState;
import ru.mos.polls.util.Dialogs;
import ru.mos.polls.util.PermissionsUtils;
import ru.mos.polls.util.SMSUtils;
import ru.mos.polls.webview.state.WebViewState;
import ru.mos.polls.wizardprofile.state.WizardProfileState;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;
import ru.mos.social.callback.PostCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.PostValue;
import ru.mos.social.model.social.Social;

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
    private static final String TAG_ELECTRONIC_HOUSE = "electronic_house";
    private static final String TAG_ABOUT = "my_about";
    private static final String TAG_NEWS = "news";
    private static final String TAG_POLLS = "polls";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_SUPPORT = "support";
    private static final String TAG_NOVELTY = "novelty";
    private static final String TAG_SHOP = "shop";

    private static final int GPS_PERMISSION_REQUEST = 9824;
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 987;
    public static final int REQUEST_CODE_SMS = 111;

    private SmsInviteControllerRX SmsInviteControllerRX;
    private SocialController socialController;
    private QuestStateController questStateController;
    private InformerUIController informerUIController;
    private NavigationDrawerFragment navFragment;
    private DrawerLayout drawerLayout;
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
            SmsInviteControllerRX.notifyBackEnd(intent.getStringExtra(SMSUtils.SENDING_PHONE_NUMBER));
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
        setContentView(R.layout.activity_main);
        setSupportProgressBarIndeterminateVisibility(false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        actionBarDrawerToggle.syncState();

        socialController = new SocialController(this);
        SmsInviteControllerRX = new SmsInviteControllerRX(this);
        informerUIController = new InformerUIController(this);
        questStateController = QuestStateController.getInstance();

        navFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        navFragment.selectItem(-1);

        navFragment.refreshUserInfo();

        modeAct(getIntent());
        startFromUri();
        Statistics.mainEnter();
        instance = this;
        SocialApiControllerRX.loadSocials(disposables, this, null, Progressable.STUB);

        updateGeotargetAreas();
        initGeotargetManager();

        subscribeEventsBus();
//        findFriends(); //вернуть в версии 2.5.0
    }

    @AfterPermissionGranted(CONTACTS_PERMISSION_REQUEST_CODE)
    private void findFriends() {
        if (ContactsController.Manager.isNeedUpdate(this)) {
            if (PermissionsUtils.CONTACTS.isGranted(this)) {
                ContactsController contactsController = new ContactsController(this);
                contactsController.setPhone(AgUser.getPhone(this));
                contactsController.silentFindFriends();
                ContactsController.Manager.increment(this);
            } else {
                PermissionsUtils.CONTACTS.request(this, CONTACTS_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @SuppressWarnings("VisibleForTests")
    Disposable disposable;

    private void subscribeEventsBus() { //переснести
        disposable = AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.ProfileEvents) {
                        Events.ProfileEvents action = (Events.ProfileEvents) o;
                        switch (action.getEventType()) {
                            case Events.ProfileEvents.EDIT_USER_INFO:
                                navigateTo().state(Add.newActivity(new EditProfileState(action.getUserInfoScrollCoord()), BaseActivity.class));
                                break;
                        }
                    }
                    if (o instanceof Events.APPEvents) {
                        Events.APPEvents action = (Events.APPEvents) o;
                        switch (action.getEventType()) {
                            case Events.APPEvents.UNAUTHORIZED:
                                ProfileManagerRX.afterLoggedOut(this);
                                break;
                        }
                    }
                    if (o instanceof Events.PollEvents) {
                        Events.PollEvents action = (Events.PollEvents) o;
                        switch (action.getEventType()) {
                            case Events.PollEvents.OPEN_POLL:
                                navigateTo().state(Add.newActivity(new SurveyState(action.getPoll().getId(), Kind.isHearing(action.getPoll().getKind())), BaseActivity.class));
                                break;
                        }
                    }
                    if (o instanceof Events.InnovationsEvents) {
                        Events.InnovationsEvents action = (Events.InnovationsEvents) o;
                        switch (action.getEventType()) {
                            case Events.InnovationsEvents.OPEN_INNOVATIONS:
                                navigateTo().state(Add.newActivity(new InnovationState(action.getInnovationId()), BaseActivity.class));
                                break;
                        }
                    }
                    if (o instanceof Events.NewsEvents) {
                        Events.NewsEvents action = (Events.NewsEvents) o;
                        switch (action.getEventType()) {
                            case Events.NewsEvents.OPEN_NEWS:
                                Statistics.readNews(action.getNews().getId());
                                GoogleStatistics.AGNavigation.readNews(action.getNews().getId());
                                if (action.getNews().isNeedHideTask()) {
                                    QuestsApiControllerRX.hideNews(disposables, this, action.getNews().getId(), null, Progressable.STUB);
                                }
                                navigateTo().state(Add.newActivity(new WebViewState(
                                        action.getNews().getTitle(),
                                        action.getNews().getLinkUrl(),
                                        String.valueOf(action.getNews().getId()),
                                        true,
                                        true), BaseActivity.class));
                                break;
                        }
                    }
                    if (o instanceof Events.OurAppEvents) {
                        Events.OurAppEvents action = (Events.OurAppEvents) o;
                        switch (action.getEventType()) {
                            case Events.OurAppEvents.OPEN_APPS:
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(((Events.OurAppEvents) o).getLink()));
                                this.startActivity(intent);
                                break;
                        }
                    }
                });
    }

    private void initGeotargetManager() {
        if (PermissionsUtils.GPS.isGranted(this)) {
            GeotargetJobManager geotargetJobManager = new GeotargetJobManager(this);
            geotargetJobManager.start();
        }
    }

    private void updateGeotargetAreas() {
        GeotargetApiControllerRX.OnAreasListener listener = new GeotargetApiControllerRX.OnAreasListener() {
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
        GeotargetApiControllerRX.loadAreas(disposables, listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        informerUIController.elkIsResume();
        informerUIController.process();
        LocalBroadcastManager.getInstance(this).registerReceiver(smsSuccessReceiver, new IntentFilter(SMSUtils.SUCCESS_SEND_INVITE_MESSAGE_FILTER));
        socialController.getEventController().registerCallback(postCallback);
        SocialUIController.registerPostingReceiver(this);
        if (PreviewAppActivity.isNeedPreview(this)) {
            PreviewAppActivity.start(this);
        }
        /**
         * Проверка доступности работы с местоположением
         */
        if (LocationController.isLocationNetworkProviderEnabled(this)
                || LocationController.isLocationGPSProviderEnabled(this)) {
            if (!PermissionsUtils.GPS.isGranted(this)
                    && GpsRequestPermsManager.isNeedRequestGps(this)) {
                GpsRequestPermsManager.incrementSyncTime(this);
                PermissionsUtils.GPS.request(this, GPS_PERMISSION_REQUEST);
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
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        informerUIController.elkIsPaused();
        if (informerUIController.getVersionDialog() != null) {
            try {
                informerUIController.getVersionDialog().dismiss();
            } catch (WindowManager.BadTokenException ignored) {
            }
        }
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
                SocialUIController.showSocialsDialog(disposables, this, new SocialUIController.SocialClickListener() {
                    @Override
                    public void onClick(Context context, Dialog dialog, AppPostValue appPostValue) {
                        socialController.post(appPostValue, appPostValue.getSocialId());
                    }

                    @Override
                    public void onCancel() {
                        MainActivity.this.finish();
                    }
                }, null);
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
                ru.mos.polls.quests.ui.QuestsFragment qfr = ru.mos.polls.quests.ui.QuestsFragment.instance();
                fr = qfr;
                qfr.setListener(new QuestsFragmentVM.Listener() {

                    @Override
                    public void onSurvey(long id) {
                        navigateTo().state(Add.newActivity(new SurveyState(id, false), BaseActivity.class));
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
                        navigateTo().state(Add.newActivity(new BindingSocialState(true), BaseActivity.class));
                    }

                    @Override
                    public void onUpdateEmail() {
                        EmailQuestActivity.startActivity(MainActivity.this);
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
                        if (!PermissionsUtils.SMS.isGranted(MainActivity.this)) {
                            PermissionsUtils.SMS.request(MainActivity.this, REQUEST_CODE_SMS);
                        } else {
                            SmsInviteControllerRX.process(isTask);
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
                EventApiControllerRX.CheckHasCurrentEventsListener listener = new EventApiControllerRX.CheckHasCurrentEventsListener() {
                    @Override
                    public void hasCurrentEvents(boolean hasEvents) {
                        if (!hasEvents) {
                            ActionBarNavigationController.setEventNavigationFromPast(MainActivity.this);
                        }
                    }
                };
                EventApiControllerRX.hasCurrentEvents(disposables, listener);
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
                fr = NewsFragment.newInstance();
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
                fr = InnovationsFragment.newInstance();
                tag = TAG_NOVELTY;
                Statistics.innovationsListFragment();
                GoogleStatistics.Innovation.innovationsListFragment();
                break;
            case NavigationMenuItem.SHOP:
                Statistics.shopBuy();
                GoogleStatistics.AGNavigation.shopBuy();
                fr = WebShopFragment.instance();
                callback = (WebShopFragment) fr;
                tag = TAG_SHOP;
                break;
            case NavigationMenuItem.ELECTRONIC_HOUSE:
                // TODO: 01.12.17 прикрутить статистику
                fr = ElectronicHouseFragment.newInstance();
                tag = TAG_ELECTRONIC_HOUSE;
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
//        AgDynamicFragment df = (AgDynamicFragment) getSupportFragmentManager().findFragmentByTag(TAG_NEWS);
//        if (df == null || !df.canGoBack()) {
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
//        }
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_SMS: {
                if (PermissionsUtils.SMS.isGranted(this)) {
                    SmsInviteControllerRX.process(true);
                }
            }
        }
        initGeotargetManager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            SmsInviteControllerRX.onActivityResult(requestCode, resultCode, data);
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
                navigateTo().state(Add.newActivity(new InnovationState(id), BaseActivity.class));
            }
        };

        UrlSchemeController.TaskListener taskListener = new UrlSchemeController.TaskListener() {
            @Override
            public void onUpdateSocial() {
                navigateTo().state(Add.newActivity(new BindingSocialState(true), BaseActivity.class));
            }

            @Override
            public void onPostInSocial() {
                SocialUIController.showSocialsDialog(disposables, MainActivity.this, new SocialUIController.SocialClickListener() {
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
                }, null);
            }

            @Override
            public void onInviteFriends() {
                SmsInviteControllerRX.process(true);
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
