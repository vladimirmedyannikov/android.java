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

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.event.model.Event;
import ru.mos.polls.innovation.model.Innovation;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.Error;
import ru.mos.polls.social.model.Message;
import ru.mos.polls.social.model.Social;
import ru.mos.polls.social.model.SocialPostItem;
import ru.mos.polls.social.model.SocialPostValue;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.util.AgTextUtil;
import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.OkListener;

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

    private static SocialAdapterHolder socialAdapterHolder = new SocialAdapterHolder();

    private static BroadcastReceiver postingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Exception postingException
                    = (Exception) intent.getSerializableExtra(EXTRA_POSTING_EXCEPTION);
            SocialPostValue socialPostValue
                    = (SocialPostValue) intent.getSerializableExtra(EXTRA_POSTED_VALUE);
            showPostingResult(context, socialPostValue, postingException);
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
            public void onLoaded(List<SocialPostItem> socialPostItems) {
                if (socialPostItems != null && socialPostItems.size() > 0) {
                    createDialog(activity, socialPostItems, false, clickListener)
                            .show();
                }
            }
        };
        AgSocialApiController.loadPostingData(activity, listener);
    }

    public static void showSocialsDialogForNovelty(final BaseActivity activity, Innovation innovation, final SocialClickListener clickListener) {
        final AgSocialApiController.SocialPostValueListener listener = new AgSocialApiController.SocialPostValueListener() {
            @Override
            public void onLoaded(List<SocialPostItem> socialPostItems) {
                if (socialPostItems != null && socialPostItems.size() > 0) {
                    createDialog(activity, socialPostItems, false, clickListener)
                            .show();
                }
            }
        };
        AgSocialApiController.loadPostingDataForNovelty(activity, innovation.getId(), listener);
    }

    public static void showSocialsDialogForAchievement(final BaseActivity activity, String achievementId, final boolean isNeedCloseActivity, final SocialClickListener clickListener) {
        final AgSocialApiController.SocialPostValueListener listener = new AgSocialApiController.SocialPostValueListener() {
            @Override
            public void onLoaded(List<SocialPostItem> socialPostItems) {
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
            public void onLoaded(List<SocialPostItem> socialPostItems) {
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
    public static void showSocialsDialogForEvent(final BaseActivity activity, Event event, final SocialClickListener clickListener) {
        final AgSocialApiController.SocialPostValueListener listener = new AgSocialApiController.SocialPostValueListener() {
            @Override
            public void onLoaded(List<SocialPostItem> socialPostItems) {
                if (socialPostItems != null && socialPostItems.size() > 0) {
                    createDialog(activity, socialPostItems, false, clickListener)
                            .show();
                }
            }
        };
        AgSocialApiController.loadPostingDataForEvent(activity, event.getId(), listener);
    }

    /**
     * Отображение списка социальных сетей для постинга результатов опроса или чекина
     *
     * @param activity
     * @param socialPostValue
     * @param clickListener
     */
    public static void showSocialsDialog(BaseActivity activity, SocialPostValue socialPostValue, boolean isNeedCancelActivity, SocialClickListener clickListener) {
        List<SocialPostItem> socialPostItems = SocialPostItem.createItems(activity, socialPostValue);
        createDialog(activity, socialPostItems, isNeedCancelActivity, clickListener)
                .show();
    }

    /**
     * Отображения диалога для редактирвоания текста постинга
     *
     * @param context
     * @param socialPostValue данные для постинга
     * @param listener        callback для моента окончания редактирования текста
     */
    public static void showEditSocialDialog(final Context context, final SocialPostValue socialPostValue, final EditSocialListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.StackedAlertDialogStyle);
        }
        AlertDialog dialog = builder.create();
        View innerView = View.inflate(context, R.layout.layout_posting_dialog, null);
        final TextView message = (TextView) innerView.findViewById(R.id.message);
        final TextView warning = (TextView) innerView.findViewById(R.id.warning);
        String post = String.format(context.getString(R.string.public_text), socialPostValue.getText());
        if (!socialPostValue.isEnable()) {
            post = String.format(context.getString(R.string.you_share_yet), socialPostValue.getText());
        }
        message.setText(post);
        if ((socialPostValue.forTwitter() || socialPostValue.forOk()) && socialPostValue.isPostMuchLong()) {
            warning.setVisibility(View.VISIBLE);
            warning.setText(socialPostValue.getWarningTitle(context));
        }

        dialog.setView(innerView);
        /**
         * Скрываем кнопку продолжить, если постим для твиттера и сообщение слишком длинное
         */
        if (!((socialPostValue.forTwitter() || socialPostValue.forOk()) && socialPostValue.isPostMuchLong())) {
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.next), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        processBeforeStatistics(socialPostValue);
                        listener.onComplete(socialPostValue);
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
                if (socialPostValue.forOk() || socialPostValue.forTwitter()) {
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
                        String value = String.format(context.getString(R.string.count_symbols), s.toString().length(), socialPostValue.getMaxSymbolsInPost());
                        symbols.setText(value);
                        int color = R.color.greyHint;
                        if (s.toString().length() > socialPostValue.getMaxSymbolsInPost()) {
                            color = R.color.ag_red;
                        }
                        symbols.setTextColor(context.getResources().getColor(color));
                    }
                });
                inputEditText.setText(socialPostValue.getText());
                builder.setView(innerView);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = inputEditText.getText().toString().trim();
                        hideKeyboard(inputEditText);
                        socialPostValue.setText(value);
                        showEditSocialDialog(context, socialPostValue, listener);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard(inputEditText);
                        showEditSocialDialog(context, socialPostValue, listener);
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
                        showEditSocialDialog(context, socialPostValue, listener);
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

    public static void showWarningSocialDialogForFb(final Context context, final SocialPostValue socialPostValue, final EditSocialListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.StackedAlertDialogStyle);
        }
        AlertDialog dialog = builder.create();
        View innerView = View.inflate(context, R.layout.layout_posting_dialog, null);
        final TextView message = (TextView) innerView.findViewById(R.id.message);
        final TextView warning = (TextView) innerView.findViewById(R.id.warning);
        warning.setVisibility(View.GONE);
        String post = String.format(context.getString(R.string.public_text), socialPostValue.getText());
        if (!socialPostValue.isEnable()) {
            post = String.format(context.getString(R.string.you_share_yet), socialPostValue.getText());
        }
        message.setText(post);

        dialog.setView(innerView);

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.next), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processBeforeStatistics(socialPostValue);
                showEditSocialDialogForFb(context, socialPostValue, listener);
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

    public static void showEditSocialDialogForFb(final Context context, final SocialPostValue socialPostValue, final EditSocialListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.StackedAlertDialogStyle);
        }
        View innerView = View.inflate(context, R.layout.layout_change_social_post_text_for_fb, null);
        final EditText inputEditText = (EditText) innerView.findViewById(R.id.postText);
        final TextView hint = (TextView) innerView.findViewById(R.id.hint);
        innerView.findViewById(R.id.symbols).setVisibility(View.GONE);
        hint.setText(socialPostValue.getText());
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
                    showEditSocialDialogForFb(context, socialPostValue, listener);
                    Toast.makeText(context, context.getString(R.string.empty_post_text), Toast.LENGTH_SHORT).show();
                    return;
                }
                hideKeyboard(context, inputEditText);
                socialPostValue.setText(value);
                if (listener != null) {
                    processBeforeStatistics(socialPostValue);
                    listener.onComplete(socialPostValue);
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


    public static void sendPostingResult(Context context, SocialPostValue socialPostValue, Exception postingException) {
        Intent intent = new Intent(ACTION_POSTING_COMPLETE);
        intent.putExtra(EXTRA_POSTED_VALUE, socialPostValue);
        intent.putExtra(EXTRA_POSTING_EXCEPTION, postingException);
        context.sendBroadcast(intent);
    }

    /**
     * Обработка результатов постинга
     *
     * @param context
     * @param socialPostValue  данные, которые запостили
     * @param postingException ошибка при постинге
     */
    public static void showPostingResult(Context context, SocialPostValue socialPostValue, Exception postingException) {
        boolean isPostingSuccess = postingException == null;
        if (isPostingSuccess) {
            showPostingSuccessDialog(context, socialPostValue);
        } else {
            showPostingErrorDialog(context, socialPostValue, postingException);
        }
        processAfterStatistics(socialPostValue, isPostingSuccess);
    }

    /**
     * C версии 1.9.4 механизм постинга в Одноклассники изменился
     * для публикации использовать {@see <a href="https://apiok.ru/wiki/pages/viewpage.action?pageId=83034148">Виджет для публикации на страницу пользователя из внешних приложений</a>}
     *
     * @param elkActivity     текущий контекст
     * @param socialPostValue данные для постинга
     */
    public static void postInOKByWidget(final BaseActivity elkActivity, final SocialPostValue socialPostValue) {
        final String id = elkActivity.getString(R.string.ok_app_id);
        final String key = elkActivity.getString(R.string.ok_app_key);
        final String secret = elkActivity.getString(R.string.ok_app_secret);
        Odnoklassniki ok = Odnoklassniki.createInstance(elkActivity, id, key);
        ok.performPosting(
                socialPostValue.getOkAttachmentsJson().toString(),
                true,
                SocialManager.getOauthOkToken(elkActivity),
                SocialManager.getOauthOkSessionSecretToken(elkActivity),
                new OkListener() {


                    @Override
                    public void onSuccess(JSONObject json) {
                        Log.d(SocialManager.POSTING_RESULT, json.toString());
                        SocialUIController.showPostingResult(elkActivity, socialPostValue, null);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(Error.POSTING_ERROR, error);
                        if (!Error.Ok.OPERATION_WAS_CANCELED_BY_USER.equals(error)) {
                            SocialUIController.showPostingErrorDialog(elkActivity, socialPostValue, error);
                        }
                    }
                });
    }

    public static void postInTweeter(final BaseActivity baseActivity, final SocialPostValue socialPostValue) {
        try {
            TwitterCore.getInstance().getApiClient().getStatusesService().update(socialPostValue.prepareTwPost(), null, null, null, null, null, null, null, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    Log.d("TW_SUCCESS", result.data.text);
                    SocialUIController.showPostingResult(baseActivity, socialPostValue, null);
                }

                @Override
                public void failure(TwitterException e) {
                    Log.e(Error.POSTING_ERROR, e.getMessage());
                    SocialUIController.showPostingResult(baseActivity, socialPostValue, e);
                }
            });
        } catch (IllegalStateException e) {
            Log.e(Error.POSTING_ERROR, e.getMessage());
            clearAndUnbindSocial(baseActivity, SocialManager.SOCIAL_ID_TW);
            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
            builder.setMessage(R.string.error_expired_access_token)
                    .setPositiveButton(R.string.ag_ok, null).show();
        }
    }

    public static void postInVk(final BaseActivity baseActivity, final SocialPostValue socialPostValue) {
        VKParameters vkParameters = VKParameters.from(
                VKApiConst.ACCESS_TOKEN, SocialManager.getAccessToken(baseActivity, SocialManager.SOCIAL_ID_VK),
                VKApiConst.MESSAGE, socialPostValue.getText(),
                VKApiConst.ATTACHMENTS, socialPostValue.getLink());
        VKRequest request = VKApi.wall().post(vkParameters);
        VKRequest.VKRequestListener vkRequestListener = new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                Log.d(SocialManager.POSTING_RESULT, response.toString());
                SocialUIController.showPostingResult(baseActivity, socialPostValue, null);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                int errorCode = error.apiError.errorCode;
                Log.e(Error.POSTING_ERROR, error.apiError.toString());
                SocialUIController.showPostingResult(baseActivity,
                        socialPostValue,
                        new Exception(String.valueOf(errorCode)));
            }
        };
        request.executeWithListener(vkRequestListener);
    }

    /**
     * Отправка статистики
     *
     * @param socialPostValue
     */
    private static void processBeforeStatistics(SocialPostValue socialPostValue) {
        try {
            if (socialPostValue.getType() == SocialPostValue.Type.CHECK_IN) {
                Statistics.beforeSocialEventSharing(socialPostValue.getSocialName(), socialPostValue.getId().toString());
            } else if (socialPostValue.getType() == SocialPostValue.Type.POLL) {
                Statistics.beforeSocialSurveySharing(socialPostValue.getSocialName(), socialPostValue.getId().toString());
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Отправка статистики
     *
     * @param socialPostValue
     */
    private static void processAfterStatistics(SocialPostValue socialPostValue, boolean isSuccess) {
        if (socialPostValue.getType() == SocialPostValue.Type.CHECK_IN) {
            Statistics.afterSocialEventSharing(socialPostValue.getSocialName(), socialPostValue.getId().toString(), isSuccess);
        } else if (socialPostValue.getType() == SocialPostValue.Type.POLL) {
            Statistics.afterSocialSurveySharing(socialPostValue.getSocialName(), socialPostValue.getId().toString(), isSuccess);
        }
    }

    /**
     * Успешно выполнили пост
     *
     * @param context         не elk ActionBarActivity, из broadcast receiver
     * @param socialPostValue данные, которые успешно заполстили
     */
    public static void showPostingSuccessDialog(final Context context, final SocialPostValue socialPostValue) {
        if (socialPostValue.isEnable() && socialPostValue.isMustServerNotified()) {
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
            AgSocialApiController.notifyAboutPosting(context, socialPostValue, listener);
        } else {
            /**
             * если оповещать сервер АГ не требуется,
             * то просто выводим диалог , информируя пользователя, тчо пост прошел
             */
            String message = String.format(context.getString(R.string.post_added_simple));
            showSimpleDialog(context, message);
        }
    }

    public static void showPostingErrorDialog(Context context, SocialPostValue socialPostValue, String error) {
        String message = context.getString(R.string.error_in_posting);
        if (Error.Ok.SESSION_IS_EXPIRED.equalsIgnoreCase(error)) {
            message = context.getString(R.string.error_validating_access_token);
            clearAndUnbindSocial(context, socialPostValue);
        }
        showSimpleDialog(context, message);
    }

    /**
     * Показывае диалог ошибки постинга
     *
     * @param context
     * @param socialPostValue  данные, которые запостили
     * @param postingExсeption ошибка постинга
     */
    private static void showPostingErrorDialog(Context context, SocialPostValue socialPostValue, Exception postingExсeption) {
        String message = processErrorMessage(context, socialPostValue.getSocialId(), postingExсeption);
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
                errorCode = Integer.parseInt(AgTextUtil.stripNonDigits(postingException.getMessage()));
            } catch (Exception ignored) {
            }
            switch (socialId) {
                case SocialManager.SOCIAL_ID_FB:
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
                case SocialManager.SOCIAL_ID_TW:
                    switch (errorCode) {
                        case Error.Twitter.STATUS_MESSAGE_IS_A_DUPLICATE:
                            result = context.getString(R.string.status_message_is_a_duplicate);
                            break;
                    }
                    break;
                case SocialManager.SOCIAL_ID_VK:
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
                case SocialManager.SOCIAL_ID_OK:
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

    public static void clearAndUnbindSocial(Context context, SocialPostValue socialPostValue) {
        clearAndUnbindSocial(context, socialPostValue.getSocialId());
    }

    private static void clearAndUnbindSocial(Context context, int socialId) {
        SocialManager.clearAuth(context, socialId);
        if (context instanceof BaseActivity) {
            Social social = Social.fromPreference(context, socialId);
            AgSocialApiController.unbindSocialFromAg((BaseActivity) context, social, null);
        }
    }

    /**
     * Отображение диалога для информирования пользователя
     *
     * @param context
     * @param message текст сообщения для пользователя
     */
    private static void showSimpleDialog(Context context, String message) {
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
     * @param socialPostItems
     * @param clickListener
     */
    private static Dialog createDialog(final BaseActivity activity,
                                       List<SocialPostItem> socialPostItems,
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
        View view = getView(activity, result, socialPostItems, clickListener);
        result.setView(view);
        return result;
    }

    /**
     * Устанавливаем кастомный view для диалогас списка соц сетей
     *
     * @param activity
     * @param dialog          текущий диалог, к на котором отображаем список соц сетей
     * @param socialPostItems данные для постинга
     * @param clickListener   callback выбора соц сети
     * @return
     */
    private static View getView(BaseActivity activity,
                                Dialog dialog,
                                List<SocialPostItem> socialPostItems,
                                SocialClickListener clickListener) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_social_list, null);
        /**
         * Список социальных сетей
         */
        ListView socialList = (ListView) view.findViewById(R.id.socialList);
        SocialItemAdapter adapter = new SocialItemAdapter(activity, dialog, socialPostItems);
        if (socialAdapterHolder != null) {
            socialAdapterHolder
                    .setSocialAdapter(adapter)
                    .setSocialPostItems(socialPostItems);
        }
        adapter.setListener(clickListener);
        socialList.setAdapter(adapter);
        /**
         * Текст подсказки
         */
        TextView socialHint = (TextView) view.findViewById(R.id.socialHint);
        int visibility = View.GONE;
        String hint = activity.getString(R.string.social_text_hint_task);
        if (socialPostItems != null && socialPostItems.size() > 0) {
            SocialPostItem socialPostItem = socialPostItems.get(0);
            SocialPostValue socialPostValue = socialPostItem.getSocialPostValue();
            if (socialPostValue.isForPoll()) {
                hint = String.format(activity.getString(R.string.social_text_hint),
                        activity.getString(R.string.social_hint_value_for_poll));
            } else if (socialPostValue.isForHearing()) {
                hint = String.format(activity.getString(R.string.social_text_hint),
                        activity.getString(R.string.social_hint_value_for_hearing));
            } else if (socialPostValue.isForCheckIn()) {
                hint = String.format(activity.getString(R.string.social_text_hint),
                        activity.getString(R.string.social_hint_value_event));
            } else if (socialPostValue.isForAchievement()) {
                hint = activity.getString(R.string.social_hint_for_achievement);
            } else if (socialPostValue.isForNovelty()) {
                hint = String.format(activity.getString(R.string.social_text_hint),
                        activity.getString(R.string.social_hint_value_for_poll));
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
    private static class SocialItemAdapter extends ArrayAdapter<SocialPostItem> {
        private SocialClickListener listener;
        private Dialog dialog;

        public SocialItemAdapter(Context context, Dialog dialog, List<SocialPostItem> objects) {
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
        private void displayIcon(View view, SocialPostItem item) {
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            SocialPostValue value = item.getSocialPostValue();
            int resId = value.isEnable() ? item.getResourceId() : item.getResourceDisableId();
            icon.setImageResource(resId);
        }

        /**
         * Имя соцсети
         *
         * @param view
         * @param item объект пункта
         */
        private void displayTitle(View view, SocialPostItem item) {
            TextView title = (TextView) view.findViewById(R.id.social);
            title.setText(item.getTitle());
        }

        /**
         * Callback выбора соц сети
         *
         * @param view
         * @param item
         */
        private void setOnClickListener(View view, final SocialPostItem item) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        SocialPostValue socialPostValue = item.getSocialPostValue();
                        socialPostValue.setTitle(item.getTitle());
                        listener.onClick(getContext(), dialog, socialPostValue);
                    }
                }
            });
        }
    }

    /**
     * Стурктуар для хранения объектов, необходимых для обновленич состоняия списка после постинга
     * После постинга в случае оповещения сс АГ требуется обновить список,
     * заблокировав пункты меню, в которые постинг уже совершили
     */
    public static class SocialAdapterHolder {
        private List<SocialPostItem> socialPostItems;
        private ArrayAdapter socialAdapter;

        public SocialAdapterHolder setSocialPostItems(List<SocialPostItem> socialPostItems) {
            this.socialPostItems = socialPostItems;
            return this;
        }

        public SocialAdapterHolder setSocialAdapter(ArrayAdapter socialAdapter) {
            this.socialAdapter = socialAdapter;
            return this;
        }

        /**
         * Обновление списка социальных сетей
         *
         * @param changedSocialPostValue данные, которые были запощены
         */
        public void refreshSocialListView(SocialPostValue changedSocialPostValue) {
            if (socialAdapter != null && socialPostItems != null && changedSocialPostValue != null) {
                for (SocialPostItem socialPostItem : socialPostItems) {
                    if (socialPostItem.getSocialPostValue().getSocialId() == changedSocialPostValue.getSocialId()) {
                        socialPostItem.getSocialPostValue().setEnable(changedSocialPostValue.isEnable());
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
         * @param socialPostValue данные для постинга
         */
        void onClick(Context context, Dialog dialog, SocialPostValue socialPostValue);

        void onCancel();
    }

    /**
     * callback редактирования текста для постинга
     */
    public interface EditSocialListener {
        void onComplete(SocialPostValue socialPostValue);
    }
}
