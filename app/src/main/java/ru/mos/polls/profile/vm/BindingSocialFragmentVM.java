package ru.mos.polls.profile.vm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import java.util.List;

import butterknife.Unbinder;
import ru.mos.polls.AGApplication;
import ru.mos.polls.CustomDialogController;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.FragmentBindSocialBinding;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.ui.adapter.SocialBindAdapter;
import ru.mos.polls.profile.ui.fragment.BindingSocialFragment;
import ru.mos.polls.social.controller.SocialApiControllerRX;
import ru.mos.polls.social.model.AppBindItem;
import ru.mos.polls.social.model.AppSocial;
import ru.mos.polls.social.storable.AppStorable;
import ru.mos.social.callback.AuthCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.Configurator;
import ru.mos.social.model.social.Social;

public class BindingSocialFragmentVM extends UIComponentFragmentViewModel<BindingSocialFragment, FragmentBindSocialBinding> {

    private boolean isTask;
    private SocialController socialController;
    private SocialBindAdapter socialBindAdapter;
    private List<AppSocial> changedSocials, savedSocials;
    private SwitchCompat socialShareNotify;
    private View notifyContainer;
    private SocialBindAdapter.Listener socialClickListener;
    private AuthCallback authCallback = new AuthCallback() {
        @Override
        public void authSuccess(Social social) {
            bindSocial(((AppStorable) Configurator.getInstance(getFragment().getContext()).getStorable()).get(social.getId()));
        }

        @Override
        public void authFailure(Social social, Exception e) {
            /**
             * если ошибка не связана с тем, что пользователь сам отклонил авторизацию
             */
            if (!e.getMessage().equals(SocialController.EXIT_AUTH_EXCEPTION)) {
                showErrorAuthDialog(((AppStorable) Configurator.getInstance(getFragment().getContext()).getStorable()).get(social.getId()), e);
            }
        }
    };

    public BindingSocialFragmentVM(BindingSocialFragment fragment, FragmentBindSocialBinding binding, boolean isTask) {
        super(fragment, binding);
        this.isTask = isTask;
    }

    @Override
    protected void initialize(FragmentBindSocialBinding binding) {
        socialShareNotify = binding.socialShareNotify;
        notifyContainer = binding.notifyContainer;
        notifyContainer.setVisibility(isTask ? View.GONE : View.VISIBLE);
        socialShareNotify.setChecked(CustomDialogController.isShareEnable(getActivity()));
        socialController = new SocialController(getActivity());
        savedSocials = ((AppStorable) Configurator.getInstance(getActivity()).getStorable()).getAll();
        changedSocials = ((AppStorable) Configurator.getInstance(getActivity()).getStorable()).getAll();
        socialClickListener = new SocialBindAdapter.Listener() {
            @Override
            public void onBindClick(AppSocial social) {
                socialController.auth(social.getId());
            }

            @Override
            public void onCloseClick(AppSocial social) {
                showUnBindDialog(social);
            }
        };
        socialBindAdapter = new SocialBindAdapter(changedSocials, socialClickListener);
        socialShareNotify.setOnCheckedChangeListener((compoundButton, b) -> {
            socialShareNotify.setChecked(b);
            CustomDialogController.setShareAbility(getFragment().getContext(), b);
        });
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        refreshSocials();
        socialController.getEventController().registerCallback(authCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socialController.getEventController().unregisterAllCallback();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().with(new ProgressableUIComponent())
                .with(new RecyclerUIComponent<>(socialBindAdapter)).build();
    }

    private void refreshSocials() {
        SocialApiControllerRX.LoadSocialListener listener = new SocialApiControllerRX.LoadSocialListener() {
            @Override
            public void onLoaded(List<AppSocial> loadedSocials) {
                changedSocials.clear();
                changedSocials.addAll(loadedSocials);
                socialBindAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError() {
            }
        };
        SocialApiControllerRX.loadSocials(disposables, getFragment().getContext(), listener, getComponent(ProgressableUIComponent.class));
    }

    public boolean isQuestExecuted() {
        return isTask && !AppSocial.isEquals(savedSocials, changedSocials);
    }


    private void showUnBindDialog(final AppSocial social) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = String.format(getFragment().getString(R.string.confirm_unbind_message),
                getFragment().getString(AppBindItem.getTitle(social.getId())));
        builder.setMessage(message);
        builder.setNegativeButton(R.string.ag_no, null);
        builder.setPositiveButton(R.string.confirm_unbind_ok, (dialog, which) -> unBindSocial(social));
        builder.show();
    }

    private void showErrorAuthDialog(final AppSocial social, Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = String.format(getFragment().getString(R.string.confirm_social_auth_error_message),
                e.getMessage());
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ag_ok, null);
        builder.show();
    }

    private void bindSocial(final AppSocial social) {
        SocialApiControllerRX.SaveSocialListener listener = new SocialApiControllerRX.SaveSocialListener() {
            @Override
            public void onSaved(final AppSocial loadedSocial, int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state, int percentFill) {
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                saveProfilePercentFill(percentFill);
                if (isTask) {
                    AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_SOCIAL, percentFill));
                    Statistics.taskSocialLogin(loadedSocial.getName());
                } else {
                    Statistics.profileSocialLogin(loadedSocial.getName());
                }
                GoogleStatistics.BindSocialFragment.taskSocialLogin(loadedSocial.getName(), isTask);
                social.copy(loadedSocial);
                social.setIsLogin(true);
                Configurator.getInstance(getActivity()).getStorable().save(social);
                setSocialBinding(social, true);
            }

            @Override
            public void onError(AppSocial social) {
                Configurator.getInstance(getActivity()).getStorable().clear(social.getId());
            }
        };
        SocialApiControllerRX.bindSocialToAg(disposables, getFragment().getContext(), social, listener, getComponent(ProgressableUIComponent.class));
    }

    public void saveProfilePercentFill(int percentFill) {
        AgUser.setPercentFillProfile(getActivity(), percentFill);
    }

    private void unBindSocial(final AppSocial social) {
        SocialApiControllerRX.SaveSocialListener listener = new SocialApiControllerRX.SaveSocialListener() {
            @Override
            public void onSaved(final AppSocial loadedSocial, int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state, int percentFill) {
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                saveProfilePercentFill(percentFill);
                if (isTask) {
                    AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_SOCIAL, percentFill));
                    Statistics.taskSocialLogin(loadedSocial.getName());
                } else {
                    Statistics.profileSocialLogin(loadedSocial.getName());
                }
                Configurator.getInstance(getActivity()).getStorable().clear(social.getId());
                setSocialBinding(social, false);
            }

            @Override
            public void onError(AppSocial social) {
            }
        };
        SocialApiControllerRX.unbindSocialFromAg(disposables, getFragment().getContext(), social, listener, getComponent(ProgressableUIComponent.class));
    }

    public void setSocialBinding(AppSocial social, boolean status) {
        social.setIsLogin(status);
        for (int i = 0; i < changedSocials.size(); i++) {
            if (changedSocials.get(i).getId() == social.getId()) {
                changedSocials.get(i).setIsLogin(status);
                break;
            }
        }
        socialBindAdapter.set(changedSocials);
    }
}
