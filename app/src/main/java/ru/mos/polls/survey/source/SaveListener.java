package ru.mos.polls.survey.source;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import org.json.JSONObject;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.AGApplication;
import ru.mos.polls.CustomDialogController;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.common.model.Message;
import ru.mos.polls.social.controller.SocialController;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.SocialPostValue;
import ru.mos.polls.subscribes.controller.SubscribesAPIController;
import ru.mos.polls.survey.SharedPreferencesSurveyManager;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.polls.survey.hearing.controller.PguUIController;


public abstract class SaveListener implements SurveyDataSource.SaveListener {

    protected final BaseActivity activity;
    protected ProgressDialog progressDialog;

    protected Survey survey;

    public SaveListener(BaseActivity activity, Survey survey) {
        this.activity = activity;
        this.survey = survey;
    }

    protected void startProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void dismissProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception ignored) {
        }
    }

    public static class SaveOnFinishListener extends SaveListener {

        protected SocialController socialController;
        private Runnable tryFinish;

        public SaveOnFinishListener(BaseActivity activity, Survey survey, SocialController socialController, Runnable tryFinish) {
            super(activity, survey);
            this.socialController = socialController;
            startProgressDialog();
            this.tryFinish = tryFinish;
        }

        private void finishOrShowVoters() {
            tryFinish.run();
        }

        @Override
        public void onSaved(int price, int currentPoints, final SocialPostValue questResultPostValue) {
            if (survey.isInterrupted()) {
                Statistics.pollsInterruptedToPassed();
            }
            SharedPreferencesSurveyManager manager = new SharedPreferencesSurveyManager(activity);
            manager.remove(survey.getId());
            if (AGApplication.IS_LOCAL_SUBSCRIBE_ENABLE) {
                sendSubscribes(price, currentPoints, questResultPostValue);
            } else {
                dismissProgressDialog();
                showResults(price, currentPoints, questResultPostValue);
            }
        }

        @Override
        public void onPguAuthError(String message) {
            dismissProgressDialog();
            PguUIController.hearingSubscribe(activity, survey.getId(), survey.getMeetings().get(0).getId());
        }

        @Override
        public void onError(int code, String message) {
            dismissProgressDialog();
                PguUIController.hearingErrorProcess(activity, code, message);
        }

        @Override
        public void onNoDataToSave() {
            dismissProgressDialog();
            activity.finish();
        }

        private void sendSubscribes(final int price, final int currentPoints, final SocialPostValue questResultPostValue) {
            SubscribesAPIController subscribesAPIController = new SubscribesAPIController();

            subscribesAPIController.saveSubscribes(activity, survey.getId(), survey.getKind().isHearing(), new SubscribesAPIController.SaveListener() {
                @Override
                public void onSaved(JSONObject jsonObject) {
                    dismissProgressDialog();
                    showResults(price, currentPoints, questResultPostValue);
                }

                @Override
                public void onError(VolleyError volleyError) {
                    Toast.makeText(activity, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    dismissProgressDialog();
                    showResults(price, currentPoints, questResultPostValue);
                }
            });
        }

        private void showResults(int price, int currentPoints, final SocialPostValue questResultPostValue) {
            Message message = survey.getMessage();
            if (message != null && !message.isEmpty()) {
                message.showCustomMessage(activity, questResultPostValue, SocialPostValue.Type.POLL, survey.getId(), new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * Возникают некоторые проблемы при попытке перейти по url схеме {@link ru.mos.polls.common.controller.UrlSchemeController}
                         * получить при этом статистику по голосованию {@link Survey#votersCount} и {@link ru.mos.polls.survey.questions.SurveyQuestion#votersCount}, {@link ru.mos.polls.survey.questions.SurveyQuestion#votersVariantsCount}
                         */
                        finishOrShowVoters();
                    }
                });
            } else {

                if (CustomDialogController.isShareEnable(activity)) {
                    CustomDialogController.ActionListener listener = new CustomDialogController.ActionListener() {
                        @Override
                        public void onYes(Dialog dialog) {
                            SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
                                @Override
                                public void onClick(Context context, Dialog dialog, SocialPostValue socialPostValue) {
                                    socialPostValue.setId(survey.getId());
                                    if (socialController != null) {
                                        socialController.post(socialPostValue);
                                    }
                                }

                                @Override
                                public void onCancel() {
                                    finishOrShowVoters();
                                }
                            };
                            SocialUIController.showSocialsDialogForPoll(activity, survey, true, listener);
                        }

                        @Override
                        public void onCancel(Dialog dialog) {
                            try {
                                dialog.dismiss();
                            } catch (Exception ignored) {
                            }
                            LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                            setResult();
                            finishOrShowVoters();
                        }

                        @Override
                        public void onDisable(Dialog dialog) {
                            try {
                                dialog.dismiss();
                            } catch (Exception ignored) {
                            }
                            LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                            setResult();
                            finishOrShowVoters();
                        }
                    };
                    String messageForPoll = PointsManager.getMessageForPoll(activity, price, currentPoints);
                    CustomDialogController.showShareDialog(activity, messageForPoll, listener);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(PointsManager.getMessage(activity, price, currentPoints));
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.ag_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishOrShowVoters();
                        }
                    });
                    builder.show();
                }
            }
            setResult();
        }

        /**
         * Так как голосование прервано
         * возвращаем его id, чтобы поставить
         * отметну "закончите голосование"
         */
        private void setResult() {
            Intent result = new Intent();
            result.putExtra(SurveyActivity.EXTRA_SURVEY_ID, survey.getId());
            result.putExtra(SurveyActivity.EXTRA_RESULT_SURVEY_STATE, ru.mos.polls.poll.model.Poll.Status.PASSED);
            activity.setResult(Activity.RESULT_OK, result);
        }

    }


    public static class SaveOnInterruptListener extends SaveListener {

        public SaveOnInterruptListener(BaseActivity activity, Survey survey) {
            super(activity, survey);
            startProgressDialog();
        }

        @Override
        public void onSaved(int price, int currentPoints, SocialPostValue socialPostValue) {
            dismissProgressDialog();
            saveCurrentPage();

            Statistics.pollsInterrupted(survey.getId(), true);
            /**
             * Так как голосование прервано
             * возвращаем его id, чтобы поставить
             * отметну "закончите голосование"
             */
            Intent result = new Intent();
            result.putExtra(SurveyActivity.EXTRA_SURVEY_ID, survey.getId());
            result.putExtra(SurveyActivity.EXTRA_RESULT_SURVEY_STATE, ru.mos.polls.poll.model.Poll.Status.INTERRUPTED);
            activity.setResult(Activity.RESULT_OK, result);

            activity.finish();
        }

        @Override
        public void onPguAuthError(String message) {
            dismissProgressDialog();
            activity.setResult(Activity.RESULT_CANCELED);
            activity.finish();
        }

        @Override
        public void onError(int code, String message) {
            dismissProgressDialog();
            if (survey == null || survey.getStatus() == Survey.Status.PASSED) { //если опрос пройден, то не пытаемся сохранить его, просто выходим
                activity.finish();
                return;
            }
            saveCurrentPage();
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            Statistics.pollsInterrupted(survey.getId(), false);
            activity.finish();
        }

        @Override
        public void onNoDataToSave() {
            dismissProgressDialog();
            saveCurrentPage();
            activity.finish();
        }

        private void saveCurrentPage() {
            SharedPreferencesSurveyManager manager = new SharedPreferencesSurveyManager(activity);
            manager.saveCurrentPage(survey);
        }
    }
}
