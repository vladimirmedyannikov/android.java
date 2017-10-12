package ru.mos.polls.profile.gui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.CustomDialogController;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.social.adapter.SocialBindAdapter;
import ru.mos.polls.social.controller.AgSocialApiController;
import ru.mos.polls.social.model.AppBindItem;
import ru.mos.polls.social.model.AppSocial;
import ru.mos.polls.social.storable.AppStorable;
import ru.mos.social.callback.AuthCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.Configurator;
import ru.mos.social.model.social.Social;

/**
 * Экран привязки аккаунтов соцсетей к аккаунту аг
 *
 * @since 1.9
 */
public class BindingSocialFragment extends Fragment {
    private static final String IS_TASK = "is_task";

    public static BindingSocialFragment newInstance(boolean isTask) {
        final BindingSocialFragment fragment = new BindingSocialFragment();
        Bundle args = new Bundle(1);
        args.putBoolean(IS_TASK, isTask);
        fragment.setArguments(args);
        return fragment;
    }

    public static BindingSocialFragment newInstance() {
        return new BindingSocialFragment();
    }

//    private ProgressDialog progressDialog;
    private Unbinder unbinder;
    private SocialController socialController;
    private SocialBindAdapter socialBindAdapter;
    private List<AppSocial> changedSocials, savedSocials;
    private ProgressableUIComponent progressableUIComponent;
    boolean isAnySocialBinded;
    @BindView(R.id.socialShareNotify)
    SwitchCompat socialShareNotify;
    @BindView(R.id.notifyContainer)
    View notifyContainer;
    private boolean isTask;
    private AuthCallback authCallback = new AuthCallback() {
        @Override
        public void authSuccess(Social social) {
            bindSocial(((AppStorable)Configurator.getInstance(getContext()).getStorable()).get(social.getId()));
        }

        @Override
        public void authFailure(Social social, Exception e) {
            showErrorAuthDialog(((AppStorable)Configurator.getInstance(getContext()).getStorable()).get(social.getId()), e);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bind_social, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressableUIComponent.onViewCreated(getContext(), view);
        findViews(view);
        refreshSocials();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        socialController.getEventController().unregisterAllCallback();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        socialController.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isQuestExecuted() {
        return isTask && !AppSocial.isEquals(savedSocials, changedSocials);
    }

    private void init() {
        progressableUIComponent = new ProgressableUIComponent();
        socialController = new SocialController((BaseActivity) getActivity());
        socialController.getEventController().registerCallback(authCallback);
        savedSocials = ((AppStorable) Configurator.getInstance(getActivity()).getStorable()).getAll();
        changedSocials = ((AppStorable) Configurator.getInstance(getActivity()).getStorable()).getAll();
        socialBindAdapter = new SocialBindAdapter(getActivity(), changedSocials);
        if (getArguments() != null) {
            isTask = getArguments().getBoolean(IS_TASK);
        }
    }

    private void findViews(View v) {
        notifyContainer.setVisibility(isTask ? View.GONE : View.VISIBLE);
        socialShareNotify.setChecked(CustomDialogController.isShareEnable(getActivity()));
        ListView gridView = ButterKnife.findById(v, R.id.list);
        gridView.setAdapter(socialBindAdapter);
        socialBindAdapter.setListener(new SocialBindAdapter.Listener() {
            @Override
            public void onBindClick(AppSocial social) {
                socialController.auth(social.getId());
            }

            @Override
            public void onCloseClick(AppSocial social) {
                showUnBindDialog(social);
            }
        });
    }

    @OnCheckedChanged(R.id.socialShareNotify)
    void socialShare(boolean isChecked) {
        socialShareNotify.setChecked(isChecked);
        CustomDialogController.setShareAbility(getActivity(), isChecked);
    }

    private void showUnBindDialog(final AppSocial social) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = String.format(getString(R.string.confirm_unbind_message),
                getString(AppBindItem.getTitle(social.getId())));
        builder.setMessage(message);
        builder.setNegativeButton(R.string.ag_no, null);
        builder.setPositiveButton(R.string.confirm_unbind_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unBindSocial(social);
            }
        });
        builder.show();
    }

    private void showErrorAuthDialog(final AppSocial social, Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = String.format(getString(R.string.confirm_social_auth_error_message),
                e.getMessage());
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ag_ok, null);
        builder.show();
    }

    private void refreshSocials() {
//        showProgress(getString(R.string.update_social_message));
        progressableUIComponent.begin();
        AgSocialApiController.LoadSocialListener listener = new AgSocialApiController.LoadSocialListener() {
            @Override
            public void onLoaded(List<AppSocial> loadedSocials) {
                changedSocials.clear();
                changedSocials.addAll(loadedSocials);
                socialBindAdapter.notifyDataSetChanged();
//                hideProgress();
                progressableUIComponent.end();
            }

            @Override
            public void onError() {
//                hideProgress();
                progressableUIComponent.end();
            }
        };
        AgSocialApiController.loadSocials((BaseActivity) getActivity(), listener);
    }

    private void bindSocial(final AppSocial social) {
//        showProgress(getString(R.string.bind_progress_message));
        progressableUIComponent.begin();
        AgSocialApiController.SaveSocialListener listener = new AgSocialApiController.SaveSocialListener() {
            @Override
            public void onSaved(final AppSocial loadedSocial, int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state, int percentFill) {
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                saveProfilePercentFill(percentFill);
                if (isTask) {
                    if (!isAnySocialBinded) {
                        AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_SOCIAL, percentFill));
                        isAnySocialBinded = true;
                    }
                    Statistics.taskSocialLogin(loadedSocial.getName());
                } else {
                    Statistics.profileSocialLogin(loadedSocial.getName());
                }
                GoogleStatistics.BindSocialFragment.taskSocialLogin(loadedSocial.getName(), isTask);
                social.copy(loadedSocial);
                social.setIsLogin(true);
                Configurator.getInstance(getActivity()).getStorable().save(social);
                for (int i = 0; i < changedSocials.size(); i++) {
                    if (changedSocials.get(i).getId() == social.getId()) {
                        changedSocials.get(i).setIsLogin(true);
                        break;
                    }
                }
                socialBindAdapter.notifyDataSetChanged();
//                hideProgress();
                progressableUIComponent.end();
            }

            @Override
            public void onError(AppSocial social) {
//                hideProgress();
                progressableUIComponent.end();
            }
        };
        AgSocialApiController.bindSocialToAg((BaseActivity) getActivity(), social, listener);
    }

    public void saveProfilePercentFill(int percentFill) {
        AgUser.setPercentFillProfile(getActivity(), percentFill);
    }

    private void unBindSocial(final AppSocial social) {
//        showProgress(getString(R.string.unbind_progress_message));
        progressableUIComponent.begin();
        AgSocialApiController.SaveSocialListener listener = new AgSocialApiController.SaveSocialListener() {
            @Override
            public void onSaved(final AppSocial loadedSocial, int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state, int percentFill) {
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                saveProfilePercentFill(percentFill);
                if (isTask) {
                    Statistics.taskSocialLogin(loadedSocial.getName());
                } else {
                    Statistics.profileSocialLogin(loadedSocial.getName());
                }
                Configurator.getInstance(getActivity()).getStorable().clear(social.getId());
                social.setIsLogin(false);
                socialBindAdapter.notifyDataSetChanged();
//                hideProgress();
                progressableUIComponent.end();
            }

            @Override
            public void onError(AppSocial social) {
//                hideProgress();
                progressableUIComponent.end();
            }
        };
        AgSocialApiController.unbindSocialFromAg((BaseActivity) getActivity(), social, listener);
    }

//    private void showProgress(String message) {
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage(message);
//        progressDialog.show();
//    }
//
//    private void hideProgress() {
//        try {
//            if (progressDialog != null) {
//                progressDialog.dismiss();
//            }
//        } catch (Exception ignored) {
//        }
//    }
}
