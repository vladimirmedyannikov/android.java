package ru.mos.polls.profile.vm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.mos.polls.AGApplication;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.databinding.FragmentAchievementBinding;
import ru.mos.polls.profile.controller.BadgeViewController;
import ru.mos.polls.profile.controller.ProfileApiControllerRX;
import ru.mos.polls.profile.model.Achievement;
import ru.mos.polls.profile.ui.fragment.AchievementFragment;
import ru.mos.polls.quests.controller.QuestsApiControllerRX;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.social.callback.PostCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.PostValue;
import ru.mos.social.model.social.Social;

public class AchievementFragmentVM extends UIComponentFragmentViewModel<AchievementFragment, FragmentAchievementBinding>{

    private ImageView badge;
    private ProgressBar loadingProgressBadge;
    private TextView body;
    private Button share;
    private boolean isOwnAchievement;
    private String achievementId;
    private Achievement achievement;
    private ImageLoader imageLoader;
    private SocialController socialController;
    private PostCallback postCallback = new PostCallback() {
        @Override
        public void postSuccess(Social social, @Nullable PostValue postValue) {
            SocialUIController.sendPostingResult(getFragment().getContext(), (AppPostValue) postValue, null);
        }

        @Override
        public void postFailure(Social social, @Nullable PostValue postValue, Exception e) {
            SocialUIController.sendPostingResult(getFragment().getContext(), (AppPostValue) postValue, e);
        }
    };

    public AchievementFragmentVM(AchievementFragment fragment, FragmentAchievementBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentAchievementBinding binding) {
        badge = binding.badge;
        loadingProgressBadge = binding.loadingBadge;
        body = binding.body;
        share = binding.share;
        imageLoader = AGApplication.getImageLoader();
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().build();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        socialController = new SocialController(getActivity());
        isOwnAchievement = getFragment().getExtraIsOwn();
        if (getAchievement()) {
            loadAchievement();
        }
        share.setOnClickListener(v -> {
            SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
                @Override
                public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                    socialPostValue.setId(achievement.getId());
                    socialController.post(socialPostValue, socialPostValue.getSocialId());
                }

                @Override
                public void onCancel() {
                    getFragment().navigateToCloseCurrActivity();
                }
            };
            SocialUIController.showSocialsDialogForAchievement(disposables, (BaseActivity) getActivity(), achievementId, false, listener, null);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        socialController.getEventController().registerCallback(postCallback);
        SocialUIController.registerPostingReceiver(getFragment().getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        socialController.getEventController().unregisterAllCallback();
        SocialUIController.unregisterPostingReceiver(getFragment().getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialController.onActivityResult(requestCode, resultCode, data);
    }

    private boolean getAchievement() {
        achievementId = getFragment().getExtraAchievementId();
        startFromUri();
        boolean result = achievementId != null
                && !TextUtils.isEmpty(achievementId)
                && !"null".equalsIgnoreCase(achievementId);
        if (!result) {
            getFragment().navigateToCloseCurrActivity();
        }
        return result;
    }

    private void loadAchievement() {
        ProfileApiControllerRX.AchievementListener listener = new ProfileApiControllerRX.AchievementListener() {
            @Override
            public void onLoaded(Achievement achievement) {
                if (achievement.isNeedHideTask()) {
                    QuestsApiControllerRX.hideAchievement(disposables, getFragment().getContext(), achievement.getId().toString(), null, Progressable.STUB);
                }
                refreshUI(achievement);
            }

            @Override
            public void onError() {
            }
        };
        ProfileApiControllerRX.loadAchievement(disposables, achievementId, listener);
    }

    private void refreshUI(final Achievement achievement) {
        this.achievement = achievement;
        BadgeViewController.displayBadge(badge, loadingProgressBadge, achievement, imageLoader);
        body.setText(achievement.getBody());
        share.setVisibility(achievement.isNext() ? View.GONE : View.VISIBLE);
        if (!isOwnAchievement) share.setVisibility(View.GONE);
    }

    private void startFromUri() {
        UrlSchemeController.startAchievement((BaseActivity) getActivity(), new UrlSchemeController.IdListener() {
            @Override
            public void onDetectedId(Object id) {
                achievementId = (String) id;
            }
        });
    }
}
