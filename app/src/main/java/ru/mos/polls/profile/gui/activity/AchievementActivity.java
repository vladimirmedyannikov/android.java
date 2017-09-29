package ru.mos.polls.profile.gui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.VolleyError;
import com.android.volley2.toolbox.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.profile.controller.BadgeViewController;
import ru.mos.polls.profile.controller.ProfileApiController;
import ru.mos.polls.profile.model.Achievement;
import ru.mos.polls.quests.controller.QuestsApiController;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.social.callback.PostCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.PostValue;
import ru.mos.social.model.social.Social;


public class AchievementActivity extends ToolbarAbstractActivity {
    private static final String EXTRA_ACHIEVEMENT_ID = "extra_achievement";

    public static void startActivity(Context context, Achievement achievement) {
        startActivity(context, achievement.getId());
    }

    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, AchievementActivity.class);
        intent.putExtra(EXTRA_ACHIEVEMENT_ID, id);
        context.startActivity(intent);
    }

    public static Intent getStartActivity(Context context, String id) {
        Intent result = new Intent(context, AchievementActivity.class);
        result.putExtra(EXTRA_ACHIEVEMENT_ID, id);
        return result;
    }

    @BindView(R.id.badge)
    ImageView badge;
    @BindView(R.id.loadingBadge)
    ProgressBar loadingProgressBadge;
    @BindView(R.id.body)
    TextView body;
    @BindView(R.id.share)
    Button share;
    private String achievementId;
    private Achievement achievement;
    private ImageLoader imageLoader;
    private SocialController socialController;
    private PostCallback postCallback = new PostCallback() {
        @Override
        public void postSuccess(Social social, @Nullable PostValue postValue) {
            SocialUIController.sendPostingResult(AchievementActivity.this, (AppPostValue) postValue, null);
        }

        @Override
        public void postFailure(Social social, @Nullable PostValue postValue, Exception e) {
            SocialUIController.sendPostingResult(AchievementActivity.this, (AppPostValue) postValue, e);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        ButterKnife.bind(this);
        imageLoader = createImageLoader();
        socialController = new SocialController(this);
        if (getAchievement()) {
            loadAchievement();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        socialController.getEventController().registerCallback(postCallback);
        SocialUIController.registerPostingReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        socialController.getEventController().unregisterAllCallback();
        SocialUIController.unregisterPostingReceiver(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialController.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.share)
    void share() {
        SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
            @Override
            public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                socialPostValue.setId(achievement.getId());
                socialController.post(socialPostValue, socialPostValue.getSocialId());
            }

            @Override
            public void onCancel() {
                AchievementActivity.this.finish();
            }
        };
        SocialUIController.showSocialsDialogForAchievement(AchievementActivity.this, achievementId, false, listener);
    }

    private boolean getAchievement() {
        achievementId = getIntent().getStringExtra(EXTRA_ACHIEVEMENT_ID);
        startFromUri();
        boolean result = achievementId != null
                && !TextUtils.isEmpty(achievementId)
                && !"null".equalsIgnoreCase(achievementId);
        if (!result) {
            finish();
        }
        return result;
    }

    private void loadAchievement() {
        ProfileApiController.AchievementListener listener = new ProfileApiController.AchievementListener() {
            @Override
            public void onLoaded(Achievement achievement) {
                if (achievement.isNeedHideTask()) {
                    QuestsApiController.hideAchievement(AchievementActivity.this, achievement.getId().toString(), null);
                }
                refreshUI(achievement);
            }

            @Override
            public void onError(VolleyError volleyError) {
                if (volleyError != null) {
                    Toast.makeText(AchievementActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        ProfileApiController.loadAchievement(this, achievementId, listener);
    }

    private void refreshUI(final Achievement achievement) {
        this.achievement = achievement;
        BadgeViewController.displayBadge(badge, loadingProgressBadge, achievement, imageLoader);
        body.setText(achievement.getBody());
        share.setVisibility(achievement.isNext() ? View.GONE : View.VISIBLE);
    }

    private void startFromUri() {
        UrlSchemeController.startAchievement(this, new UrlSchemeController.IdListener() {
            @Override
            public void onDetectedId(Object id) {
                achievementId = (String) id;
            }
        });
    }

}
