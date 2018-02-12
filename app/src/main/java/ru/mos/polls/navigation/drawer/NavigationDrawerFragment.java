package ru.mos.polls.navigation.drawer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Timer;
import java.util.TimerTask;

import ru.mos.elk.ElkTextUtils;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.badge.Constants;
import ru.mos.polls.badge.controller.BadgeApiController;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.badge.model.BadgesSource;
import ru.mos.polls.badge.model.Personal;
import ru.mos.polls.badge.model.State;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.rxhttp.rxapi.config.AgApiBuilder;
import ru.mos.polls.util.AgTextUtil;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    /**
     * Action for opened profile
     */
    public static final String ACTION_NEED_OPEN_PROFILE = "ru.mos.polls.navigation.drawer.action_need_open_profile";
    /**
     * File name with preferences
     */
    private static final String PREFS = NavigationDrawerFragment.class.getName() + "_prefs";
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    /**
     * Last selected item's index
     */
    private static final String PREF_LAST_SELECTED_POSITION = "navigation_drawer_last_selected_index";
    /**
     * A pointer to the current callbacks newInstance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private NavigationMenuAdapter adapter;

    private int mCurrentSelectedPosition;
    private int mLastCheckedPosition;
    private boolean mUserLearnedDrawer;

    private LinearLayout mUserContainer;
    private ImageView mUserAvatarImageView;
    private TextView mUserNameTextView;
    private TextView mUserPhoneTextView;

    private CheckedTextView mMenuSettings;
    private CheckedTextView mMenuHelp;

    private Timer mTimerReloadFromServer;

    private BroadcastReceiver reloadFromServerBroadcastReceiver;
    private BroadcastReceiver reloadFromCacheBroadcastReceiver;
    private BroadcastReceiver reloadAvatarFromCacheBroadcastReceiver;
    private BroadcastReceiver needOpenProfileReceiver;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NavigationMenuAdapter(getActivity(), NavigationMenuItem.ITEMS_MENU);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        } else {
            mCurrentSelectedPosition = -1;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);

        reloadFromServerBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                doRealodBadges();
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(reloadFromServerBroadcastReceiver, BadgeManager.RELOAD_FROM_SERVER_INTENT_FILTER);

        reloadFromCacheBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                final State state = BadgesSource.getInstance().getState();
                reloadState(state);
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(reloadFromCacheBroadcastReceiver, BadgeManager.RELOAD_FROM_CACHE_INTENT_FILTER);

        reloadAvatarFromCacheBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Bitmap bitmap = BadgesSource.getInstance().getAvatar();
                if (bitmap != null) {
//                    BitmapHelper.getRoundedImage(mUserAvatarImageView, bitmap, bitmap.getHeight(), new BitmapHelper.RoundedTaskListener() {
//                        @Override
//                        public void onPreExecute() {
//                        }
//
//                        @Override
//                        public void onComplete(Bitmap roundedBitmap) {
//                            mUserAvatarImageView.setImageBitmap(roundedBitmap);
//                        }
//                    });
                    mUserAvatarImageView.setImageBitmap(bitmap);
                } else {
                    mUserAvatarImageView.setImageResource(R.drawable.unlogin2);
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(reloadAvatarFromCacheBroadcastReceiver, BadgeManager.RELOAD_AVATAR_FROM_CACHE_INTENT_FILTER);

        needOpenProfileReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                for (int i = 0; i < adapter.getItems().length; i++) {
                    if (adapter.getItems()[i].getId() == NavigationMenuItem.PROFILE) {
                        selectItem(i);
                        break;
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(needOpenProfileReceiver, new IntentFilter(ACTION_NEED_OPEN_PROFILE));

        mTimerReloadFromServer = new Timer("AG bages from server");
        mTimerReloadFromServer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doRealodBadges();
            }
        }, Constants.INIT_INTERVAL, Constants.RELOAD_SERVER_INTERVAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(reloadFromServerBroadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(reloadFromCacheBroadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(reloadAvatarFromCacheBroadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(needOpenProfileReceiver);
        mTimerReloadFromServer.cancel();
        mTimerReloadFromServer.purge();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    private void doRealodBadges() {
        BadgeApiController.refresh((BaseActivity) getActivity(), new BadgeApiController.BadgesListener() {
            @Override
            public void onLoaded(State state) {
                BadgesSource.getInstance().storeState(state);
                refreshUserInfo();
                doReloadUserAvatar();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void doReloadUserAvatar() {
        BadgesSource.getInstance().processAvatarUrl(new BadgesSource.UrlProcessor() {
            @Override
            public void process(@Nullable final String url) {
                if (url == null) {
                    BadgesSource.getInstance().setAvatar(null, null);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_AVATAR_FROM_CACHE));
                } else {
                    ImageLoader imageLoader = ((AGApplication) getActivity().getApplication()).getImageLoader();
                    imageLoader.loadImage(AgApiBuilder.resourceURL(url), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            mUserAvatarImageView.setImageBitmap(bitmap);
                            BadgesSource.getInstance().setAvatar(url, bitmap);
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {
                        }
                    });
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) v.findViewById(R.id.list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        mUserAvatarImageView = (ImageView) v.findViewById(R.id.userAvatar);
        mUserNameTextView = (TextView) v.findViewById(R.id.userName);
        mUserPhoneTextView = (TextView) v.findViewById(R.id.userPhone);
        mUserContainer = (LinearLayout) v.findViewById(R.id.userContainer);
        mUserContainer.setOnClickListener(v1 -> {
            for (int i = 0; i < adapter.getItems().length; i++) {
                if (adapter.getItems()[i].getId() == NavigationMenuItem.PROFILE) {
                    selectItem(i);
                    break;
                }
            }
        });
        return v;
    }

    public void refreshUserInfo() {
        AgUser agUser = new AgUser(getActivity());
        doRefreshUserInfo(agUser.getPhone(), agUser.getFirstName(), agUser.getSurname());
    }

    public void reloadState(State state) {
        if (state != null) {
            Personal personal = state.getPersonal();
            doRefreshUserInfo(personal.getPhone(), personal.getFirstName(), personal.getSurname());
            adapter.notifyDataSetChanged();
        }
    }

    private void doRefreshUserInfo(String phone, String firstName, String surname) {
        phone = AgTextUtil.getPhoneFormat(phone);
        final String name;
        if (ElkTextUtils.isEmpty(firstName) && !ElkTextUtils.isEmpty(surname)) {
            name = surname;
        } else if (!ElkTextUtils.isEmpty(firstName) && ElkTextUtils.isEmpty(surname)) {
            name = firstName;
        } else if (!ElkTextUtils.isEmpty(firstName) && !ElkTextUtils.isEmpty(surname)) {
            name = surname + " " + firstName;
        } else {
            name = null;
        }
        if (!ElkTextUtils.isEmpty(phone) && ElkTextUtils.isEmpty(name)) {
            mUserNameTextView.setText(phone);
            mUserPhoneTextView.setVisibility(View.GONE);
        } else if (ElkTextUtils.isEmpty(phone) && !ElkTextUtils.isEmpty(name)) {
            mUserPhoneTextView.setText(name);
            mUserPhoneTextView.setVisibility(View.VISIBLE);
            mUserNameTextView.setVisibility(View.GONE);
        } else if (!ElkTextUtils.isEmpty(phone) && !ElkTextUtils.isEmpty(name)) {
            mUserNameTextView.setText(name);
            mUserPhoneTextView.setVisibility(View.VISIBLE);
            mUserPhoneTextView.setText(phone);
        } else {
            mUserNameTextView.setText(R.string.app_name);
            mUserPhoneTextView.setVisibility(View.GONE);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                if (mCallbacks != null) mCallbacks.onNavigationDrawerClosed(drawerView);
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                doRealodBadges();

                if (mCallbacks != null) {
                    mCallbacks.onNavigationDrawerOpened(drawerView);
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        /**
         * С версии 1.8 при первом запуске показываем скрываем выдвигающееся меню
         * показываем главную ленту
         */
//        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
//        // per the navigation drawer design guidelines.
//        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
//            mDrawerLayout.openDrawer(mFragmentContainerView);
//        }

        // Defer code dependent on restoration of previous newInstance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void selectItem(int position) {
        //Save to preferences
        SharedPreferences sp = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putInt(PREF_LAST_SELECTED_POSITION, position).commit();
        //Save to field
        mCurrentSelectedPosition = position;
        final NavigationMenuItem item;
        if (position == -1) {
            item = NavigationMenuItem.ITEMS_MENU[0];
        } else {
            item = adapter.getItem(position);
        }
        selectItem(position, item);
    }

    private void selectItem(int position, NavigationMenuItem item) {
        //Callbacks & UI response
        if (mDrawerListView != null) {
            if (position == -1) {
                mLastCheckedPosition = position;
                mDrawerListView.clearChoices();
                mDrawerListView.requestLayout();
            } else {
                if (item.isCheackable()) {
                    mDrawerListView.setItemChecked(position, true);
                    mLastCheckedPosition = position;
                } else {
                    mDrawerListView.setItemChecked(position, false);
                    mDrawerListView.setItemChecked(mLastCheckedPosition, true);
                }
            }
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(item.getId());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        try {
            super.onSaveInstanceState(outState);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }


    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int id);

        /**
         * Closed when a navigation drawer is opened
         *
         * @param drawerView Drawer view that is now opened
         */
        void onNavigationDrawerOpened(View drawerView);


        /**
         * Closed when a navigation drawer is closed
         *
         * @param drawerView Drawer view that is now closed
         */
        void onNavigationDrawerClosed(View drawerView);
    }
}
