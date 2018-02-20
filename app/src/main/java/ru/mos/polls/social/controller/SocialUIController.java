package ru.mos.polls.social.controller;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.innovations.model.InnovationDetails;
import ru.mos.polls.event.model.EventRX;
import ru.mos.polls.social.model.AppPostItem;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.social.model.AppSocial;
import ru.mos.polls.social.model.Error;
import ru.mos.polls.social.model.Message;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.util.AgTextUtil;
import ru.mos.social.model.Configurator;

/**
 * Инкапсулирует работу с диалогами выбора соц сетей,
 * формирования текста для постинга,
 * отображение результатов постинга, ошибок при постинге и т.п.
 * <p/>
 */
public abstract class SocialUIController {
    public static final String ACTION_POSTING_COMPLETE = "ru.mos.poll.social.ACTION_POSING_COMPLETE";
    public static final String EXTRA_POSTED_VALUE = "extra_posted_value";
    public static final String EXTRA_POSTING_EXCEPTION = "extra_posting_exception";

    private static final String TWITTER_AUTH_EXCEPTION = "HTTP request failed, Status: 401";
    private static SocialAdapterHolder socialAdapterHolder = new SocialAdapterHolder();

    private static BroadcastReceiver postingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Exception postingException
                    = (Exception) intent.getSerializableExtra(EXTRA_POSTING_EXCEPTION);
            AppPostValue appPostValue
                    = (AppPostValue) intent.getSerializableExtra(EXTRA_POSTED_VALUE);
            showPostingResult(context, appPostValue, postingException);
        }
    };

    public static SocialAdapterHolder getCurrentSocialAdapterHolder() {
        return socialAdapterHolder;
    }

    public static void registerPostingReceiver(Context context) {
        IntentFilter filter = new IntentFilter(ACTION_POSTING_COMPLETE);
        context.registerReceiver(postingReceiver, filter);
    }

    public static void unregisterPostingReceiver(Context context) {
        try {
            if (postingReceiver != null) {
                context.unregisterReceiver(postingReceiver);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Отображение списка социальных сетей для постинга сообщения, полученного с сс АГ
     *
     * @param activity      elk ActionBarActivity
     * @param clickListener callback выбора соц сети
     */
    public static void showSocialsDialog(final BaseActivity activity, final SocialClickListener clickListener) {
        final AgSocialApiController.SocialPostValueListener listener = new AgSocialApiController.SocialPostValueListener() {
            @Override
            public void onLoaded(List<AppPostItem> socialPostItems) {
                if (socialPostItems != null && socialPostItems.size() > 0) {
                    createDialog(activity, socialPostItems, false, clickListener)
                            .show();
                }
            }
        };
        AgSocialApiController.loadPostingData(activity, listener);
    }

    public static void showSocialsDialogForNovelty(final BaseActivity activity, InnovationDetails innovationActiviti, final SocialClickListener clickListener) {
        final AgSocialApiController.SocialPostValueListener listener = new AgSocialApiController.SocialPostValueListener() {
            @Override
            public void onLoaded(List<AppPostItem> socialPostItems) {
                if (socialPostItems != null && socialPostItems.size() > 0) {
                    createDialog(activity, socialPostItems, false, clickListener)
                            .show();
                }
            }
        };
        AgSocialApiController.loadPostingDataForNovelty(activity, innovationActiviti.getId(), listener);
    }

    public static void showSocialsDialogForAchievement(final BaseActivity activity, String achievementId, final boolean isNeedCloseActivity, final SocialClickListener clickListener) {
        final AgSocialApiController.SocialPostValueListener listener = new AgSocialApiController.SocialPostValueListener() {
            @Override
            public void onLoaded(List<AppPostItem> socialPostItems) {
                if (socialPostItems != null && socialPostItems.size() > 0) {
                    createDialog(activity, socialPostItems, isNeedCloseActivity, clickListener)
                            .show();
                }
            }
        };
        AgSocialApiController.loadPostingDataForAchievement(activity, achievementId, listener);
    }

    /**
     * Отображение списка социальных сетей для постинга сообщения о прохождении голосования, полученного с сс АГ
     *
     * @param activity            elk ActionBarActivity
     * @param isNeedCloseActivity закрывать ли текущий экран, вместе диалогом
     * @param clickListener       callback выбора соц сети
     */
    public static void showSocialsDialogForPoll(final BaseActivity activity, Survey survey, final boolean isNeedCloseActivity, final SocialClickListener clickListener) {
        final AgSocialApiController.SocialPostValueListener listener = new AgSocialApiController.SocialPostValueListener() {
            @Override
            public void onLoaded(List<AppPostItem> socialPostItems) {
                if (socialPostItems != null && socialPostItems.size() > 0) {
                    createDialog(activity, socialPostItems, isNeedCloseActivity, clickListener)
                            .show();
                }
            }
        };
        AgSocialApiController.loadPostingDataForPoll(activity, survey.getId(), survey.getKind().isHearing(), listener);
    }

    /**
     * Отображение списка социальных сетей для постинга сообщения об отметки на мероприятии, полученного с сс АГ
     *
     * @param activity      elk ActionBarActivity
     * @param clickListener callback выбора соц сети
     */
    public static void showSocialsDialogForEvent(final BaseActivity activity, EventRX event, final SocialClickListener clickListener) {
        final AgSocialApiController.SocialPostValueListener listener = new AgSocialApiController.SocialPostValueListener() {
            @Override
            public void onLoaded(List<AppPostItem> socialPostItems) {
                if (socialPostItems != null && socialPostItems.size() > 0) {
                    createDialog(activity, socialPostItems, false, clickListener)
                            .show();
                }
            }
        };
        AgSocialApiController.loadPostingDataForEvent(activity, event.getCommonBody().getId(), listener);
    }

    /**
     * Отображение списка социальных сетей для постинга результатов опроса или чекина
     *
     * @param activity
     * @param appPostValue
     * @param clickListener
     */
    public static void showSocialsDialog(BaseActivity activity, AppPostValue appPostValue, boolean isNeedCancelActivity, SocialClickListener clickListener) {
        List<AppPostItem> appPostItems = AppPostItem.createItems(activity, appPostValue);
        createDialog(activity, appPostItems, isNeedCancelActivity, clickListener)
                .show();
    }

    /**
     * Отображения диалога для редактирвоания текста постинга
     *
     * @param context
     * @param appPostValue данные для постинга
     * @param listener        callback для моента окончания редактирования текста
     */
    public static void showEditSocialDialog(final Context context, final AppPostValue appPostValue, final EditSocialListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.StackedAlertDialogStyle);
        }
        AlertDialog dialog = builder.create();
        View innerView = View.inflate(context, R.layout.layout_posting_dialog, null);
        final TextView message = (TextView) innerView.findViewById(R.id.message);
        final TextView warning = (TextView) innerView.findViewById(R.id.warning);
        if (appPostValue.forTwitter() && appPostValue.isPostMuchLong()) {
            appPostValue.setText(AgTextUtil.stripLengthText(appPostValue.getText(),
                    AppPostValue.MAX_TWEET_POST_LENGTH - 3));
        }
        String post = String.format(context.getString(R.string.public_text), appPostValue.getText());
        if (!appPostValue.isEnable()) {
            post = String.format(context.getString(R.string.you_share_yet), appPostValue.getText());
            if (appPostValue.getType() == AppPostValue.Type.ACHIEVEMENT) {
                post = String.format(context.getString(R.string.you_share_yet_without), appPostValue.getText());
            }
        }
        message.setText(post);
        if ((appPostValue.forTwitter() || appPostValue.forOk()) && appPostValue.isPostMuchLong()) {
            warning.setVisibility(View.VISIBLE);
            warning.setText(appPostValue.getWarningTitle(context));
        }
        dialog.setView(innerView);
        /**
         * Скрываем кнопку продолжить, если постим для твиттера и сообщение слишком длинное
         */
        if (!((appPostValue.forTwitter() || appPostValue.forOk()) && appPostValue.isPostMuchLong())) {
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.next), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        processBeforeStatistics(appPostValue);
                        listener.onComplete(appPostValue);
                    }
                }
            });
        }
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View innerView = View.inflate(context, R.layout.layout_change_social_post_text, null);
                final EditText inputEditText = (EditText) innerView.findViewById(R.id.postText);
                final TextView symbols = (TextView) innerView.findViewById(R.id.symbols);
                int visibility = View.GONE;
                if (appPostValue.forOk() || appPostValue.forTwitter()) {
                    visibility = View.VISIBLE;
                }
                symbols.setVisibility(visibility);
                inputEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String value = String.format(context.getString(R.string.count_symbols), String.valueOf(s.toString().length()), String.valueOf(appPostValue.getMaxSymbolsInPost()));
                        symbols.setText(value);
                        int color = R.color.greyHint;
                        if (s.toString().length() > appPostValue.getMaxSymbolsInPost()) {
                            color = R.color.ag_red;
                        }
                        symbols.setTextColor(context.getResources().getColor(color));
                    }
                });
                inputEditText.setText(appPostValue.getText());
                builder.setView(innerView);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = inputEditText.getText().toString().trim();
                        hideKeyboard(inputEditText);
                        appPostValue.setText(value);
                        showEditSocialDialog(context, appPostValue, listener);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard(inputEditText);
                        showEditSocialDialog(context, appPostValue, listener);
                    }
                });
                final AlertDialog d = builder.create();
                inputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
                d.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        hideKeyboard(inputEditText);
                        showEditSocialDialog(context, appPostValue, listener);
                    }
                });
                d.setCanceledOnTouchOutside(true);
                d.show();
            }

            private void hideKeyboard(View view) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                final Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout linearLayout = (LinearLayout) button.getParent();
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            } catch (Exception ignored) {
            }
        }
    }

    private static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showWarningSocialDialogForFb(final Context context, final AppPostValue appPostValue, final EditSocialListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.StackedAlertDialogStyle);
        }
        AlertDialog dialog = builder.create();
        View innerView = View.inflate(context, R.layout.layout_posting_dialog, null);
        final TextView message = (TextView) innerView.findViewById(R.id.message);
        final TextView warning = (TextView) innerView.findViewById(R.id.warning);
        warning.setVisibility(View.GONE);
        String post = String.format(context.getString(R.string.public_text), appPostValue.getText());
        if (!appPostValue.isEnable()) {
            post = String.format(context.getString(R.string.you_share_yet), appPostValue.getText());
            if (appPostValue.getType() == AppPostValue.Type.ACHIEVEMENT) {
                post = String.format(context.getString(R.string.you_share_yet_without), appPostValue.getText());
            }
        }
        message.setText(post);

        dialog.setView(innerView);

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.next), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processBeforeStatistics(appPostValue);
                showEditSocialDialogForFb(context, appPostValue, listener);
            }
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dialog.show();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                final Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout linearLayout = (LinearLayout) button.getParent();
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            } catch (Exception ignored) {
            }
        }
    }

    public static void showEditSocialDialogForFb(final Context context, final AppPostValue appPostValue, final EditSocialListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.StackedAlertDialogStyle);
        }
        View innerView = View.inflate(context, R.layout.layout_change_social_post_text_for_fb, null);
        final EditText inputEditText = (EditText) innerView.findViewById(R.id.postText);
        final TextView hint = (TextView) innerView.findViewById(R.id.hint);
        final View attention = innerView.findViewById(R.id.attention_repeat_posting);
        attention.setVisibility(appPostValue.isEnable() ? View.GONE : View.VISIBLE);
        innerView.findViewById(R.id.symbols).setVisibility(View.GONE);
        hint.setText(appPostValue.getText());
        hint.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Copy the Text to the clipboard
                ClipboardManager manager =
                        (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setText(hint.getText());
                // Show a message:
                Toast toast = Toast.makeText(context, R.string.copy_text_success, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
                return true;
            }
        });
        builder.setView(innerView);
        builder.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideKeyboard(context, inputEditText);
            }
        });
        builder.setNegativeButton(R.string.next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = inputEditText.getText().toString().trim();
                if (TextUtils.isEmpty(value)) {
                    hideKeyboard(context, inputEditText);
                    showEditSocialDialogForFb(context, appPostValue, listener);
                    Toast.makeText(context, context.getString(R.string.empty_post_text), Toast.LENGTH_SHORT).show();
                    return;
                }
                hideKeyboard(context, inputEditText);
                appPostValue.setText(value);
                if (listener != null) {
                    processBeforeStatistics(appPostValue);
                    listener.onComplete(appPostValue);
                }

            }
        });
        final AlertDialog d = builder.create();
        inputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        d.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideKeyboard(context, inputEditText);
            }
        });
        d.setCanceledOnTouchOutside(true);
        d.show();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                final Button button = d.getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout linearLayout = (LinearLayout) button.getParent();
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            } catch (Exception ignored) {
            }
        }
    }


    public static void sendPostingResult(Context context, AppPostValue appPostValue, Exception postingException) {
        if (appPostValue.getSocialId() == AppSocial.ID_TW) {
            if (postingException != null) {
                if (postingException.getMessage().equals(TWITTER_AUTH_EXCEPTION)) {
                    Configurator.getInstance(context).getStorable().clear(AppSocial.ID_TW);
                }
            }
        }
        Intent intent = new Intent(ACTION_POSTING_COMPLETE);
        intent.putExtra(EXTRA_POSTED_VALUE, (Serializable) appPostValue);
        intent.putExtra(EXTRA_POSTING_EXCEPTION, postingException);
        context.sendBroadcast(intent);
    }

    /**
     * Обработка результатов постинга
     *
     * @param context
     * @param appPostValue  данные, которые запостили
     * @param postingException ошибка при постинге
     */
    public static void showPostingResult(Context context, AppPostValue appPostValue, Exception postingException) {
        boolean isPostingSuccess = postingException == null;
        if (isPostingSuccess) {
            showPostingSuccessDialog(context, appPostValue);
        } else {
            showPostingErrorDialog(context, appPostValue, postingException);
        }
        processAfterStatistics(appPostValue, isPostingSuccess);
    }

//    /**
//     * C версии 1.9.4 механизм постинга в Одноклассники изменился
//     * для публикации использовать {@see <a href="https://apiok.ru/wiki/pages/viewpage.action?pageId=83034148">Виджет для публикации на страницу пользователя из внешних приложений</a>}
//     *
//     * @param elkActivity     текущий контекст
//     * @param appPostValue данные для постинга
//     */
//    public static void postInOKByWidget(final BaseActivity elkActivity, final AppPostValue appPostValue) {
//        final String id = elkActivity.getString(R.string.ok_app_id);
//        final String key = elkActivity.getString(R.string.ok_app_key);
//        final String secret = elkActivity.getString(R.string.ok_app_secret);
//        Odnoklassniki ok = Odnoklassniki.createInstance(elkActivity, id, key);
//        ok.performPosting(
//                appPostValue.getOkAttachmentsJson().toString(),
//                true,
//                SocialManager.getOauthOkToken(elkActivity),
//                SocialManager.getOauthOkSessionSecretToken(elkActivity),
//                new OkListener() {
//
//
//                    @Override
//                    public void onSuccess(JSONObject json) {
//                        Log.d(SocialManager.POSTING_RESULT, json.toString());
//                        SocialUIController.showPostingResult(elkActivity, appPostValue, null);
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        Log.e(Error.POSTING_ERROR, error);
//                        if (!Error.Ok.OPERATION_WAS_CANCELED_BY_USER.equals(error)) {
//                            SocialUIController.showPostingErrorDialog(elkActivity, appPostValue, error);
//                        }
//                    }
//                });
//    }

//    public static void postInTweeter(final BaseActivity baseActivity, final AppPostValue appPostValue) {
//        try {
//            TwitterCore.getInstance().getApiClient().getStatusesService().update(appPostValue.prepareTwPost(), null, null, null, null, null, null, null, new Callback<Tweet>() {
//                @Override
//                public void success(Result<Tweet> result) {
//                    Log.d("TW_SUCCESS", result.data.text);
//                    SocialUIController.showPostingResult(baseActivity, appPostValue, null);
//                }
//
//                @Override
//                public void failure(TwitterException e) {
//                    Log.e(Error.POSTING_ERROR, e.getMessage());
//                    SocialUIController.showPostingResult(baseActivity, appPostValue, new Exception(AgTextUtil.stripNonDigits(e.getMessage())));
//                }
//            });
//        } catch (Exception e) {
//            Log.e(Error.POSTING_ERROR, e.getMessage());
//            clearAndUnbindSocial(baseActivity, AppSocial.ID_TW);
//            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
//            builder.setMessage(R.string.error_expired_access_token)
//                    .setPositiveButton(R.string.ag_ok, null).show();
//        }
//    }

//    public static void postInVk(final BaseActivity baseActivity, final AppPostValue appPostValue) {
//        VKParameters vkParameters = VKParameters.from(
//                VKApiConst.ACCESS_TOKEN, SocialManager.getAccessToken(baseActivity, AppSocial.ID_VK),
//                VKApiConst.MESSAGE, appPostValue.getText(),
//                VKApiConst.ATTACHMENTS, appPostValue.getLink());
//        VKRequest request = VKApi.wall().post(vkParameters);
//        VKRequest.VKRequestListener vkRequestListener = new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//                Log.d(SocialManager.POSTING_RESULT, response.toString());
//                SocialUIController.showPostingResult(baseActivity, appPostValue, null);
//            }
//
//            @Override
//            public void onError(VKError error) {
//                super.onError(error);
//                int errorCode = error.apiError.errorCode;
//                Log.e(Error.POSTING_ERROR, error.apiError.toString());
//                SocialUIController.showPostingResult(baseActivity,
//                        appPostValue,
//                        new Exception(String.valueOf(errorCode)));
//            }
//        };
//        request.executeWithListener(vkRequestListener);
//    }

    /**
     * Отправка статистики
     *
     * @param appPostValue
     */
    private static void processBeforeStatistics(AppPostValue appPostValue) {
        try {
            if (appPostValue.getType() == AppPostValue.Type.CHECK_IN) {
                Statistics.beforeSocialEventSharing(appPostValue.getSocialName(), appPostValue.getId().toString());
                GoogleStatistics.SocialSharing.beforeSocialEventSharing(appPostValue.getSocialName(), appPostValue.getId().toString());
            } else if (appPostValue.getType() == AppPostValue.Type.POLL) {
                Statistics.beforeSocialSurveySharing(appPostValue.getSocialName(), appPostValue.getId().toString());
                GoogleStatistics.SocialSharing.beforeSocialSurveySharing(appPostValue.getSocialName(), appPostValue.getId().toString());
            } else if (appPostValue.getType() == AppPostValue.Type.NOVELTY) {
                Statistics.beforeSocialInnovationSharing(appPostValue.getSocialName(), appPostValue.getId().toString());
                GoogleStatistics.SocialSharing.beforeSocialInnovationSharing(appPostValue.getSocialName(), appPostValue.getId().toString());
            } else if (appPostValue.getType() == AppPostValue.Type.ACHIEVEMENT) {
                Statistics.beforeSocialAchivementSharing(appPostValue.getSocialName(), appPostValue.getId().toString());
                GoogleStatistics.SocialSharing.beforeSocialAchivementSharing(appPostValue.getSocialName(), appPostValue.getId().toString());
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Отправка статистики
     *
     * @param appPostValue
     */
    private static void processAfterStatistics(AppPostValue appPostValue, boolean isSuccess) {
        if (appPostValue.getType() == AppPostValue.Type.CHECK_IN) {
            Statistics.afterSocialEventSharing(appPostValue.getSocialName(), appPostValue.getId().toString(), isSuccess);
            GoogleStatistics.SocialSharing.afterSocialEventSharing(appPostValue.getSocialName(), appPostValue.getId().toString(), isSuccess);
        } else if (appPostValue.getType() == AppPostValue.Type.POLL) {
            Statistics.afterSocialSurveySharing(appPostValue.getSocialName(), appPostValue.getId().toString(), isSuccess);
            GoogleStatistics.SocialSharing.afterSocialSurveySharing(appPostValue.getSocialName(), appPostValue.getId().toString(), isSuccess);
        } else if (appPostValue.getType() == AppPostValue.Type.NOVELTY) {
            Statistics.afterSocialInnovationSharing(appPostValue.getSocialName(), appPostValue.getId().toString(), isSuccess);
            GoogleStatistics.SocialSharing.afterSocialInnovationSharing(appPostValue.getSocialName(), appPostValue.getId().toString(), isSuccess);
        } else if (appPostValue.getType() == AppPostValue.Type.ACHIEVEMENT) {
            Statistics.afterSocialAchivementSharing(appPostValue.getSocialName(), appPostValue.getId().toString(), isSuccess);
            GoogleStatistics.SocialSharing.afterSocialAchivementSharing(appPostValue.getSocialName(), appPostValue.getId().toString(), isSuccess);
        }
    }

    /**
     * Успешно выполнили пост
     *
     * @param context         не elk ActionBarActivity, из broadcast receiver
     * @param appPostValue данные, которые успешно заполстили
     */
    public static void showPostingSuccessDialog(final Context context, final AppPostValue appPostValue) {
        if (appPostValue.isEnable() && appPostValue.isMustServerNotified()) {
            AgSocialApiController.PostingNotifyListener listener = new AgSocialApiController.PostingNotifyListener() {
                @Override
                public void onNotified(Message customMessage) {
                    /**
                     * Показываем кастомный диалог, если он имеется
                     */
                    if (customMessage != null) {
                        customMessage.showCustomMessage(context);
                    }
                }

                @Override
                public void onNotified(String message) {
                    /**
                     * Если кастомного диалога нет, то показываем диалог с количеством начисленных баллов
                     */
                    showSimpleDialog(context, message);
                }
            };
            AgSocialApiController.notifyAboutPosting(context, appPostValue, listener);
        } else {
            /**
             * если оповещать сервер АГ не требуется,
             * то просто выводим диалог , информируя пользователя, тчо пост прошел
             */
            String message = String.format(context.getString(R.string.post_added_simple));
            showSimpleDialog(context, message);
        }
    }

    public static void showPostingErrorDialog(Context context, AppPostValue appPostValue, String error) {
        String message = context.getString(R.string.error_in_posting);
        if (Error.Ok.SESSION_IS_EXPIRED.equalsIgnoreCase(error)) {
            message = context.getString(R.string.error_validating_access_token);
            clearAndUnbindSocial(context, appPostValue);
        }
        showSimpleDialog(context, message);
    }

    /**
     * Показывае диалог ошибки постинга
     *
     * @param context
     * @param appPostValue  данные, которые запостили
     * @param postingExсeption ошибка постинга
     */
    private static void showPostingErrorDialog(Context context, AppPostValue appPostValue, Exception postingExсeption) {
        String message = processErrorMessage(context, appPostValue.getSocialId(), postingExсeption);
        showSimpleDialog(context, message);
    }

    /**
     * Обработка ошибок при постинге
     *
     * @param context
     * @param socialId         идентификатор социальной сети
     * @param postingException должен содержать только код ошибки
     * @return текст ошибки, показываемый пользователю
     */
    private static String processErrorMessage(Context context, int socialId, Exception postingException) {
        String result = context.getString(R.string.error_in_posting);
        if (postingException != null) {
            int errorCode = -1;
            try {
                errorCode = Integer.parseInt(postingException.getMessage());
            } catch (Exception ignored) {
                Log.e(Error.POSTING_ERROR, ignored.getMessage());
            }
            switch (socialId) {
                case AppSocial.ID_FB:
                    switch (errorCode) {
                        case Error.Facebook.DUPLICATE_STATUS_MESSAGE:
                            result = context.getString(R.string.status_message_is_a_duplicate);
                            break;
                        case Error.Facebook.REQUEST_LIMIT_REACHED:
                            result = context.getString(R.string.request_limit_reached);
                            break;
                        case Error.Facebook.ERROR_VALIDATING_ACCESS_TOKEN:
                        case Error.Facebook.ERROR_VALIDATING_ACCESS_TOKEN_AFTER_CHANGE_PASSWORD:
                            result = context.getString(R.string.error_validating_access_token);
                            clearAndUnbindSocial(context, socialId);
                            break;
                        case Error.Facebook.USER_HAS_NOT_AUTHORIZED_THE_APPLICATION:
                            result = context.getString(R.string.error_has_not_auth_the_application);
                            clearAndUnbindSocial(context, socialId);
                            break;
                        case Error.Facebook.UNKNOWN_ERROR:
                            result = context.getString(R.string.error_sharing_unknown);
                            break;
                    }
                    break;
                case AppSocial.ID_TW:
                    switch (errorCode) {
                        case Error.Twitter.STATUS_MESSAGE_IS_A_DUPLICATE:
                            result = context.getString(R.string.status_message_is_a_duplicate);
                            break;
                    }
                    break;
                case AppSocial.ID_VK:
                    switch (errorCode) {
                        case Error.Vk.ERROR_AUTH_FAILED:
                            result = context.getString(R.string.error_validating_access_token);
                            clearAndUnbindSocial(context, socialId);
                            break;
                        case Error.Vk.ERROR_TOKKEN_EXPIRED:
                            result = context.getString(R.string.error_expired_access_token);
                            clearAndUnbindSocial(context, socialId);
                            break;
                    }
                    break;
                case AppSocial.ID_OK:
                    switch (errorCode) {
                        case Error.Ok.ERROR_SESSION_EXPIRED:
                            result = context.getString(R.string.error_validating_access_token);
                            clearAndUnbindSocial(context, socialId);
                            break;
                        case Error.Ok.ERROR_PERMISSION_DENIED:
                            result = context.getString(R.string.error_permission_denied);
                            break;
                    }
                    break;
            }
        }
        return result;
    }

    public static void clearAndUnbindSocial(Context context, AppPostValue appPostValue) {
        clearAndUnbindSocial(context, appPostValue.getSocialId());
    }

    private static void clearAndUnbindSocial(Context context, int socialId) {
        Configurator.getInstance(context).getStorable().clear(socialId);
        if (context instanceof BaseActivity) {
            AppSocial social = AppSocial.fromPreference(context, socialId);
            AgSocialApiController.unbindSocialFromAg((BaseActivity) context, social, null);
        }
    }

    /**
     * Отображение диалога для информирования пользователя
     *
     * @param context
     * @param message текст сообщения для пользователя
     */
    public static void showSimpleDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    /**
     * Создание и отображение даилога списка
     *
     * @param activity
     * @param appPostItems
     * @param clickListener
     */
    private static Dialog createDialog(final BaseActivity activity,
                                       List<AppPostItem> appPostItems,
                                       final boolean isNeedCancelActivity,
                                       final SocialClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNeedCancelActivity) {
                    clickListener.onCancel();
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog result = builder.create();
        View view = getView(activity, result, appPostItems, clickListener);
        result.setView(view);
        return result;
    }

    /**
     * Устанавливаем кастомный view для диалогас списка соц сетей
     *
     * @param activity
     * @param dialog          текущий диалог, к на котором отображаем список соц сетей
     * @param appPostItems данные для постинга
     * @param clickListener   callback выбора соц сети
     * @return
     */
    private static View getView(BaseActivity activity,
                                Dialog dialog,
                                List<AppPostItem> appPostItems,
                                SocialClickListener clickListener) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_social_list, null);
        /**
         * Список социальных сетей
         */
        ListView socialList = (ListView) view.findViewById(R.id.socialList);
        SocialItemAdapter adapter = new SocialItemAdapter(activity, dialog, appPostItems);
        if (socialAdapterHolder != null) {
            socialAdapterHolder
                    .setSocialAdapter(adapter)
                    .setAppPostItems(appPostItems);
        }
        adapter.setListener(clickListener);
        socialList.setAdapter(adapter);
        /**
         * Текст подсказки
         */
        TextView socialHint = (TextView) view.findViewById(R.id.socialHint);
        int visibility = View.GONE;
        String hint = activity.getString(R.string.social_text_hint_task);
        if (appPostItems != null && appPostItems.size() > 0) {
            AppPostItem appPostItem = appPostItems.get(0);
            AppPostValue appPostValue = appPostItem.getAppPostValue();
            if (appPostValue.isForPoll()) {
                hint = String.format(activity.getString(R.string.social_text_hint),
                        activity.getString(R.string.social_hint_value_for_poll));
            } else if (appPostValue.isForHearing()) {
                hint = String.format(activity.getString(R.string.social_text_hint),
                        activity.getString(R.string.social_hint_value_for_hearing));
            } else if (appPostValue.isForCheckIn()) {
                hint = String.format(activity.getString(R.string.social_text_hint),
                        activity.getString(R.string.social_hint_value_event));
            } else if (appPostValue.isForAchievement()) {
                hint = activity.getString(R.string.social_hint_for_achievement);
            } else if (appPostValue.isForNovelty()) {
                hint = String.format(activity.getString(R.string.social_text_hint),
                        activity.getString(R.string.social_hint_value_for_novelty));
            }
            visibility = View.VISIBLE;
        }
        socialHint.setVisibility(visibility);
        socialHint.setText(hint);
        return view;
    }

    /**
     * Адаптер для списка социальных сетей
     */
    private static class SocialItemAdapter extends ArrayAdapter<AppPostItem> {
        private SocialClickListener listener;
        private Dialog dialog;

        public SocialItemAdapter(Context context, Dialog dialog, List<AppPostItem> objects) {
            super(context, R.layout.item_social, objects);
            this.dialog = dialog;
        }

        public void setListener(SocialClickListener listener) {
            this.listener = listener;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_social, null);
            }
            displayIcon(convertView, getItem(position));
            displayTitle(convertView, getItem(position));
            setOnClickListener(convertView, getItem(position));
            return convertView;
        }

        /**
         * Отображаем иконку соцсети
         *
         * @param view
         * @param item объект пункта
         */
        private void displayIcon(View view, AppPostItem item) {
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            AppPostValue value = item.getAppPostValue();
            int resId = value.isEnable() ? item.getResourceId() : item.getResourceDisableId();
            icon.setImageResource(resId);
        }

        /**
         * Имя соцсети
         *
         * @param view
         * @param item объект пункта
         */
        private void displayTitle(View view, AppPostItem item) {
            TextView title = (TextView) view.findViewById(R.id.social);
            title.setText(item.getTitle());
        }

        /**
         * Callback выбора соц сети
         *
         * @param view
         * @param item
         */
        private void setOnClickListener(View view, final AppPostItem item) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        AppPostValue appPostValue = item.getAppPostValue();
                        appPostValue.setTitle(item.getTitle());
//                        if (appPostValue.isEnable()) {
                            processPosting(appPostValue);
//                        } else {
//                            GuiUtils.displayYesOrNotDialog(getContext(),
//                                    String.format(getContext().getString(R.string.repeated_posting_message), appPostValue.getTypeNameForPostingRepeat(), AppSocial.getNormalSocialName(appPostValue.getSocialId())),
//                                    (dialog1, which) -> processPosting(appPostValue), null);
//                        }
                    }
                }
            });
        }

        private void processPosting(AppPostValue appPostValue) {
            EditSocialListener editSocialListener = appPostValue1 -> listener.onClick(getContext(), dialog, appPostValue1);
            if (appPostValue.getSocialId() == AppSocial.ID_FB) {
                showEditSocialDialogForFb(getContext(), appPostValue, editSocialListener);
            } else {
                showEditSocialDialog(getContext(), appPostValue, editSocialListener);
            }
        }
    }

    /**
     * Стурктуар для хранения объектов, необходимых для обновленич состоняия списка после постинга
     * После постинга в случае оповещения сс АГ требуется обновить список,
     * заблокировав пункты меню, в которые постинг уже совершили
     */
    public static class SocialAdapterHolder {
        private List<AppPostItem> appPostItems;
        private ArrayAdapter socialAdapter;

        public SocialAdapterHolder setAppPostItems(List<AppPostItem> appPostItems) {
            this.appPostItems = appPostItems;
            return this;
        }

        public SocialAdapterHolder setSocialAdapter(ArrayAdapter socialAdapter) {
            this.socialAdapter = socialAdapter;
            return this;
        }

        /**
         * Обновление списка социальных сетей
         *
         * @param changedAppPostValue данные, которые были запощены
         */
        public void refreshSocialListView(AppPostValue changedAppPostValue) {
            if (socialAdapter != null && appPostItems != null && changedAppPostValue != null) {
                for (AppPostItem appPostItem : appPostItems) {
                    if (appPostItem.getAppPostValue().getSocialId() == changedAppPostValue.getSocialId()) {
                        appPostItem.getAppPostValue().setEnable(changedAppPostValue.isEnable());
                    }
                }
                socialAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * callback выбора соц сети
     */
    public interface SocialClickListener {
        /**
         * выбор соцсети
         *
         * @param context         текущий контекст
         * @param dialog          текущий даилог (требуется, чтобы закрыть или что-то сделать с диалогом)
         * @param appPostValue данные для постинга
         */
        void onClick(Context context, Dialog dialog, AppPostValue appPostValue);

        void onCancel();
    }

    /**
     * callback редактирования текста для постинга
     */
    public interface EditSocialListener {
        void onComplete(AppPostValue appPostValue);
    }
}
