package ru.mos.polls.social.controller;

import android.content.Intent;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.social.auth.SocialAuthActivity;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.Social;
import ru.mos.polls.social.model.SocialPostValue;
import ru.mos.polls.social.model.TokenData;
import ru.mos.polls.social.service.PostingService;

/**
 * Инкапсулирует логику работы с социальными сетями:<br/>
 * <ul>
 * <li>проверка авторизации и запуск ее выполнения {@link #isAuthNeed(int)};</li>
 * <li>запуск сервиса выполнения постинга в социальной сети {@link #post(SocialPostValue)};</li>
 * </ul>
 */
public class SocialController {
    private static final int REQUEST_CODE_POST_IN_SOCIAL = 2;
    private static final int REQUEST_CODE_UPDATE_PROFILE = 3;

    private BaseActivity activity;
    private Social social;
    private SocialPostValue socialPostValue;
    private AgSocialApiController.SaveSocialListener saveSocialListener;
    private Action action;

    public SocialController(BaseActivity activity) {
        this.activity = activity;
    }

    public void post(final SocialPostValue socialPostValue) {
        action = Action.POST;
        if (socialPostValue != null) {
            if (isAuthNeed(socialPostValue.getSocialId())) {
                this.socialPostValue = socialPostValue;
                authForPosting(socialPostValue.getSocialId());
            } else {
                preparePost(socialPostValue);
            }
        }
    }

    public void bindSocial(Social social, AgSocialApiController.SaveSocialListener saveSocialListener) {
        action = Action.BINDING;
        if (social != null) {
            if (isAuthNeed(social.getSocialId())) {
                this.social = social;
                this.saveSocialListener = saveSocialListener;
                authForUpdating(social.getSocialId());
            } else {
                AgSocialApiController.bindSocialToAg(activity, social, saveSocialListener);
            }
        }
    }

    public void unBindSocial(Social social, AgSocialApiController.SaveSocialListener saveSocialListener) {
        if (social != null) {
            AgSocialApiController.unbindSocialFromAg(activity, social, saveSocialListener);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = false;
        if (requestCode == REQUEST_CODE_POST_IN_SOCIAL) {
            if (resultCode == BaseActivity.RESULT_OK) {
                if (socialPostValue != null) {
                    final int socialId = socialPostValue.getSocialId();
                    if (!isAuthNeed(socialId)/*!TokenData.isEmpty(activity, socialId)*/) {
                        preparePost(socialPostValue);
                        result = true;
                    }
                }

            }
        } else if (requestCode == REQUEST_CODE_UPDATE_PROFILE) {
            if (resultCode == BaseActivity.RESULT_OK) {
                if (social != null) {
                    final int socialId = social.getSocialId();
                    social = Social.fromPreference(activity, socialId);
                    if (!isAuthNeed(socialId)/*!social.getTokenData().isEmpty()*/) {
                        AgSocialApiController.bindSocialToAg(activity, social, saveSocialListener);
                        result = true;
                    } else {
                        /**
                         * авторизация не прошла
                         */
                        if (saveSocialListener != null) {
                            saveSocialListener.onError(social);
                        }
                    }
                }
            } else if (resultCode == BaseActivity.RESULT_CANCELED) {
                /**
                 * авторизация отменена
                 */
                if (saveSocialListener != null) {
                    saveSocialListener.onError(social);
                }
            }
        }
        return result;
    }

    /**
     * Проверка, нужна ли авторизация
     *
     * @param socialId идентификатор социальной сети
     * @return true - необходимые токены есть, атворизация не нужна
     */
    private boolean isAuthNeed(int socialId) {
        boolean result = false;
        if (action == Action.POST && socialId == SocialManager.SOCIAL_ID_OK) {
            result = !SocialManager.isOauthOkTokensExists(activity);
        } else {
            final boolean isNeedToken = TokenData.isEmpty(activity, socialId);
            final boolean expired = SocialManager.isExpired(activity, socialId);
            if (isNeedToken || expired) {
                result = true;
            }
        }
        return result;
    }

    private void authForPosting(int socialId) {
        authForResult(socialId, REQUEST_CODE_POST_IN_SOCIAL);
    }

    private void authForUpdating(int socialId) {
        authForResult(socialId, REQUEST_CODE_UPDATE_PROFILE);
    }

    /**
     * Запуск процесса авторизации
     *
     * @param socialId
     */
    private void authForResult(int socialId, int requestCode) {
        final Intent intent = new Intent(activity, SocialAuthActivity.class);
        intent.putExtra(SocialAuthActivity.EXTRA_SOCIAL_VALUE, socialId);
        if (socialId == SocialManager.SOCIAL_ID_OK && requestCode == REQUEST_CODE_UPDATE_PROFILE) {
            intent.putExtra(SocialAuthActivity.EXTRA_OK_MODE, SocialAuthActivity.OK_MODE_OAUTH);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Отображение диалога редактирования текста поста
     *
     * @param socialPostValue данные для постинга
     */
    private void preparePost(final SocialPostValue socialPostValue) {
        SocialUIController.EditSocialListener listener = new SocialUIController.EditSocialListener() {
            @Override
            public void onComplete(final SocialPostValue socialPostValue) {
                if (socialPostValue.forOk()) {
                    SocialUIController.postInOKByWidget(activity, socialPostValue);
                } else if (socialPostValue.forTwitter()) {
                    SocialUIController.postInTweeter(activity, socialPostValue);
                } else {
                    PostingService.start(activity, socialPostValue);
                }
            }
        };
        if (socialPostValue.forFb()) {
            SocialUIController.showWarningSocialDialogForFb(activity, socialPostValue, listener);
        } else {
            SocialUIController.showEditSocialDialog(activity, socialPostValue, listener);
        }
    }

    /**
     * Выполняемое действие
     *
     * @since 1.9.4
     */
    public enum Action {
        POST, BINDING
    }
}
