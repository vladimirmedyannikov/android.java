package ru.mos.polls.profile.gui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.ProfileManager;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.fragments.PullableFragment;
import ru.mos.polls.helpers.FragmentHelper;
import ru.mos.polls.navigation.actionbar.ActionBarNavigationController;
import ru.mos.polls.profile.gui.fragment.location.FlatsFragment;

/**
 * Экран профиля пользователя, объяединяет в себе фрагменты
 *
 * @see ru.mos.polls.profile.gui.fragment.EmailFragment
 * @see ru.mos.polls.profile.gui.fragment.UserPersonalFragment
 * @see ru.mos.polls.profile.gui.fragment.FamilyFragment
 * @see ru.mos.polls.profile.gui.fragment.location.FlatsFragment
 * @see ru.mos.polls.profile.gui.fragment.WorkFragment
 * @see ru.mos.polls.profile.gui.fragment.ProfileBindItemsFragment
 * <p/>
 * Данные фрагменты также используются при выполнении заданий {@link ru.mos.polls.quests.ProfileQuestActivity}
 * @since 1.9
 */
public class ProfileFragment extends PullableFragment implements AbstractProfileFragment.ChangeListener {
    public static final String PREFS = "profile_prefs";
    public static final String TIME_SYNQ = "time_synq";
    public static final long INTERVAL_SYNQ = 15 * 60 * 1000;

    public static Fragment newInstance() {
        return new ProfileFragment();
    }

    private static View root;
    @BindView(R.id.save)
    Button save;
    private ProgressDialog progressDialog;
    public Unbinder unbinder;
    private EmailFragment emailFragment;
    private UserPersonalFragment userPersonalFragment;
    private FamilyFragment familyFragment;
    private FlatsFragment flatsFragment;
    private WorkFragment workFragment;
    private ProfileBindItemsFragment profileBindItemsFragment;

    /**
     * changed - не сохраненные данные пользователя, при изменении данных в полях экрана
     * данные заносятся в этот объект
     * saved - локально сохраненные данные пользователя, обновляются при старте экрана
     * и при выполенении обновлении данных на сервере
     */
    private AgUser changed, saved;
    private boolean isRefreshingUI;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changed = new AgUser(getActivity());
        saved = new AgUser(getActivity());
        Statistics.enterProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        /**
         * @see <a href="http://stackoverflow.com/questions/14083950/duplicate-id-tag-null-or-parent-id-with-another-fragment-for-com-google-android">
         *     для исключения Duplicate ID, tag null, or parent id with another fragment</a>
         */
        if (root != null) {
            ViewGroup parent = (ViewGroup) root.getParent();
            if (parent != null) {
                parent.removeView(root);
                dismissAll();
            }
        }
        try {
            root = inflater.inflate(R.layout.fragment_profile, container, false);
        } catch (InflateException ignored) {
        }
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        refreshUI();
        /**
         * Стартуем обновление данных профиля после некоторой задержки,
         * иначе подтормаживает зактрытие navigation drawer-а
         */
        refreshProfileAfterTimeout(1000);
    }

    @Override
    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(Response.Listener<Object> responseListener, Response.ErrorListener errorListener) {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshProfile();
            }
        };
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarNavigationController
                .destroyNavigation((BaseActivity) getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        dismissAll();
    }

    private void dismissAll() {
        try {
            dismiss(emailFragment);
            dismiss(userPersonalFragment);
            dismiss(familyFragment);
            dismiss(flatsFragment);
            dismiss(workFragment);
            dismiss(profileBindItemsFragment);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onChange(int fragmentId) {
        updateAgUser(fragmentId);
        processSaveEnable();
    }

    /**
     * Делаем кнопку активной, когда есть что-то новое
     */
    private void processSaveEnable() {
        boolean result = false;
        try {
            result = saved.isFilledAndChanged(changed, true);
        } catch (Exception ignored) {
        }
        save.setEnabled(result);
    }

    private void findViews(View v) {
        emailFragment = (EmailFragment) getChildFragmentManager().findFragmentById(R.id.emailFragment);
        userPersonalFragment = (UserPersonalFragment) getChildFragmentManager().findFragmentById(R.id.userPersonalFragment);
        familyFragment = (FamilyFragment) getChildFragmentManager().findFragmentById(R.id.familyFragment);
        userPersonalFragment.setGenderListener(familyFragment);
        flatsFragment = (FlatsFragment) getChildFragmentManager().findFragmentById(R.id.addressesFragment);
        workFragment = (WorkFragment) getChildFragmentManager().findFragmentById(R.id.workFragment);
        profileBindItemsFragment = (ProfileBindItemsFragment) getChildFragmentManager().findFragmentById(R.id.profileBindItemsFragment);

        /**
         * Прокидываем изменения в полях фрагментов
         * во фрагмент ProfileFragment
         */
        emailFragment.setChangeListener(this);
        userPersonalFragment.setChangeListener(this);
        familyFragment.setChangeListener(this);
        flatsFragment.setChangeListener(this);
        workFragment.setChangeListener(this);
    }

    private void dismiss(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @OnClick(R.id.save)
    void saveProfile() {
        startProgress();
        ProfileManager.SaveAgUserListener saveAgUserListener = new ProfileManager.SaveAgUserListener() {
            @Override
            public void onSaved(JSONObject resultJson) {
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                incrementSynqTime();
                trySaveFlats(resultJson);
                changed.save(getActivity());
                saved = new AgUser(getActivity());
                refreshUI();
                processSaveEnable();
                stopProgress();
                processResults(resultJson);
                Toast.makeText(getContext(), getText(R.string.success_personal_updated), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                stopProgress();
            }
        };
        ProfileManager.setProfile((BaseActivity) getActivity(), saved.getRequestBody(changed), saveAgUserListener);
    }

    private void refreshProfile() {
        if (isNeedRefreshProfile()) {
            setRefreshing();
            ProfileManager.AgUserListener agUserListener = new ProfileManager.AgUserListener() {
                @Override
                public void onLoaded(AgUser loadedAgUser) {
                    try {
                        changed = loadedAgUser;
                        saved = new AgUser(getActivity());
                        refreshUI();
                        getPullToRefreshLayout().setRefreshing(false);
                        stopProgress();
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    try {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        getPullToRefreshLayout().setRefreshing(false);
                    } catch (Exception ignored) {
                    }
                }
            };
            ProfileManager.getProfile((BaseActivity) getActivity(), agUserListener);
        } else {
            long minutes = 1 + (getSynqTime() - System.currentTimeMillis()) / 60000;
            Log.d("PROFILE_NOT_REFRESH", "WAIT " + String.valueOf(minutes));
            getPullToRefreshLayout().setRefreshing(false);
        }
    }

    private boolean isNeedRefreshProfile() {
        boolean result = false;
        if (getSynqTime() <= System.currentTimeMillis()) {
            incrementSynqTime();
            result = true;
        }
        return result;
    }

    private long getSynqTime() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getLong(TIME_SYNQ, System.currentTimeMillis());
    }

    private void incrementSynqTime() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putLong(TIME_SYNQ, System.currentTimeMillis() + INTERVAL_SYNQ).apply();
    }

    public static void clearSyncTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().remove(TIME_SYNQ).apply();
    }

    private void refreshProfileAfterTimeout(final long mills) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(mills);
                    refreshProfile();
                } catch (Exception ignored) {
                }
                return null;
            }
        }.execute();
    }

    private void trySaveFlats(JSONObject resultJson) {
        if (resultJson != null) {
            if (resultJson.has("flats")) {
                JSONObject flatsJson = resultJson.optJSONObject("flats");
                if (flatsJson != null) {
                    Flat flat = Flat.getRegistration(flatsJson);
                    if (flat != null && !flat.isEmpty()) {
                        changed.setRegistrationFlat(flat);
                        flat.save(getActivity());
                    }
                    flat = Flat.getResidence(flatsJson);
                    if (flat != null && !flat.isEmpty()) {
                        changed.setResidenceFlat(flat);
                        flat.save(getActivity());
                    }
                    flat = Flat.getWork(flatsJson);
                    if (flat != null && !flat.isEmpty()) {
                        changed.setWorkFlat(flat);
                        flat.save(getActivity());
                    }
                }
            }
        }
    }

    private void processResults(JSONObject resultJson) {
        QuestMessage message = new QuestMessage(resultJson);
        if (!message.isEmpty()) {
            message.show(getActivity());
        }
    }

    private void refreshUI() {
        isRefreshingUI = true;
        try {
            emailFragment.refreshUI(changed);
            userPersonalFragment.refreshUI(changed);
            familyFragment.refreshUI(changed);
            flatsFragment.refreshUI(changed);
            workFragment.refreshUI(changed);
            profileBindItemsFragment.refreshUI(changed);
        } catch (Exception ignored) {
        }
        isRefreshingUI = false;
    }

    private void updateAgUser(int fragmentId) {
        if (!isRefreshingUI) {
            switch (fragmentId) {
                case AbstractProfileFragment.EMAIL_ID:
                case AbstractProfileFragment.USER_PERSONAL_ID:
                    emailFragment.updateAgUser(changed);
                    userPersonalFragment.updateAgUser(changed);
                    break;
                case AbstractProfileFragment.FAMILY_ID:
                    familyFragment.updateAgUser(changed);
                    break;
                case AbstractProfileFragment.WORK_ID:
                    workFragment.updateAgUser(changed);
                    break;
                case AbstractProfileFragment.ADDRESS_ID:
                    flatsFragment.updateAgUser(changed);
                    break;
                case AbstractProfileFragment.BINDING_ID:
                    profileBindItemsFragment.updateAgUser(changed);
                    break;
            }
        }
    }

    protected void startProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
    }

    protected void stopProgress() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (WindowManager.BadTokenException ignored) {
        }
    }
}