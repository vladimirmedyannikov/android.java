package ru.mos.polls.survey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.hearing.gui.activity.PguVerifyActivity;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.source.SaveListener;
import ru.mos.polls.survey.source.SurveyDataSource;
import ru.mos.polls.survey.source.WebSurveyDataSource;
import ru.mos.polls.survey.variants.ActionSurveyVariant;
import ru.mos.social.callback.PostCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.PostValue;
import ru.mos.social.model.social.Social;


public class SurveyActivity extends BaseActivity {
    public static final String EXTRA_SURVEY_ID = "extra_survey_id";
    public static final String EXTRA_IS_HEARING = "extra_is_hearing";
    public static final String EXTRA_RESULT_SURVEY_STATE = "extra_result_survey_state";
    public static final String IS_ACTIVITY_RESTARTED = "extra_is_activity_resturt";

    public static final int REQUEST_POLLS = 0;

    public static void startActivityForResult(Activity activity, long pollId, boolean isHearing) {
        Intent intent = getStartIntent(activity, pollId, isHearing);
        activity.startActivityForResult(intent, REQUEST_POLLS);
    }

    public static Intent getStartIntent(Context context, long pollId, boolean isHearing) {
        Intent intent = new Intent(context, SurveyActivity.class);
        intent.putExtra(EXTRA_SURVEY_ID, pollId);
        intent.putExtra(EXTRA_IS_HEARING, isHearing);
        return intent;
    }

    public static boolean onResult(int requestCode, int resultCode, Intent data) {
        return resultCode == RESULT_OK && requestCode == REQUEST_POLLS && data != null;
    }

    private long surveyId;
    private boolean isHearing;
    private Survey survey;
    private SurveyFragment surveyFragment;
    private SurveySummaryFragment surveySummaryFragment;
    private Fragment currentFragment;

    private SocialController socialController;
    private Callback callback = Callback.STUB;
    private PostCallback postCallback = new PostCallback() {
        @Override
        public void postSuccess(Social social, @Nullable PostValue postValue) {
            SocialUIController.sendPostingResult(SurveyActivity.this, (AppPostValue) postValue, null);
        }

        @Override
        public void postFailure(Social social, @Nullable PostValue postValue, Exception e) {
            SocialUIController.sendPostingResult(SurveyActivity.this, (AppPostValue) postValue, e);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_survey);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (setSurveyId() && !isNeedLoading(savedInstanceState)) {
            loadSurvey();
        }
        socialController = new SocialController(this);
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

    public void setCallback(Callback callback) {
        if (callback == null) {
            this.callback = Callback.STUB;
        } else {
            this.callback = callback;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (currentFragment != null) {
                    if (currentFragment instanceof SurveyFragment) {
                        onUpPressed();
                    } else {
                        onBackPressed();
                    }
                } else {
                    onBackPressed();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    private void onUpPressed() {
        callback.onUpPressed();
    }

    @Override
    public void onBackPressed() {
        if (currentFragment != null) {
            callback.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialController.onActivityResult(requestCode, resultCode, data);
        tryToExecuteActionCallback(requestCode);
        /**
         * Вернулись с эерана авторизации в пгу
         * еще раз отпралвяем запрос на
         */
        if (PguVerifyActivity.isAuth(data)) {
            if (survey.getKind().isHearing()) {
                doFinish(survey);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_ACTIVITY_RESTARTED, true);
    }

    private void tryToExecuteActionCallback(int requestCode) {
        if (requestCode == ActionSurveyVariant.REQUEST_FILLING_LOCATION_DATA) {
            callback.onLocationUpdated();
        }
    }

    private boolean isNeedLoading(Bundle savedInstanceState) {
        boolean result = false;
        if (savedInstanceState != null) {
            result = savedInstanceState.getBoolean(IS_ACTIVITY_RESTARTED);
        }
        return result;
    }

    private void loadSurvey() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        WebSurveyDataSource surveyDataSource = new WebSurveyDataSource(this);
        surveyDataSource.load(surveyId, isHearing, new SurveyDataSource.LoadListener() {

            @Override
            public void onLoaded(Survey loadedSurvey) {
                dismissProgressDialog();
                survey = loadedSurvey;
                setFragment();
            }

            @Override
            public void onError(String message) {
                dismissProgressDialog();
            }

            private void dismissProgressDialog() {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    private boolean setSurveyId() {
        Intent intent = getIntent();
        isHearing = intent.getBooleanExtra(EXTRA_IS_HEARING, false);
        surveyId = intent.getLongExtra(EXTRA_SURVEY_ID, -1);

        if (UrlSchemeController.hasUri(this)) {
            if (UrlSchemeController.isHearing(this)) {
                isHearing = true;
            }
            UrlSchemeController.startPoll(this, new UrlSchemeController.IdListener() {
                @Override
                public void onDetectedId(Object id) {
                    surveyId = (Long) id;
                }
            });

        }

        return surveyId != -1;
    }

    private void setFragment() {
        Fragment fragment = getSummaryFragment(survey);
        if (!survey.getKind().isHearing() && isRedirectNeed(survey)) {
            /**
             * По умолчанию первый вопрос
             */
            long questionId = survey.getQuestionsOrder().get(0);
            /**
             * Выбераем первый неотвеченный вопрос для прерванного или активного опроса
             */
            if (survey.isActive() || survey.isInterrupted()) {
                SurveyQuestion surveyQuestion = survey.getFirstNotCheckedQuestion();
                if (surveyQuestion != null) {
                    questionId = surveyQuestion.getId();
                }
            }
            fragment = getSurveyFragment(survey, questionId);
        }
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        currentFragment = fragment;
        setCallback((Callback) fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private boolean isRedirectNeed(Survey survey) {
        boolean result;
        String shortHtml = survey.getTextShortHtml();
        result = shortHtml == null || TextUtils.isEmpty(shortHtml) || "null".equalsIgnoreCase(shortHtml);
        return result;
    }

    private Fragment getSummaryFragment(Survey survey) {
        surveySummaryFragment = SurveySummaryFragment.newInstance(survey, true);
        surveySummaryFragment.setCallback(getSummaryFragmentCallback());
        return surveySummaryFragment;
    }

    public SurveySummaryFragment.Callback getSummaryFragmentCallback() {
        SurveySummaryFragment.Callback callback = new SurveySummaryFragment.Callback() {
            @Override
            public void onQuestionClicked(Survey survey, long questionId) {
                SurveyActivity.this.survey = survey;
                getSurveyFragment(survey, questionId);
                replaceFragment(surveyFragment);
            }

            @Override
            public void onSurveyDone(Survey survey) {
                doFinish(survey);
            }

            @Override
            public void onSurveyInterrupted(Survey survey) {
                doInterrupt(survey);
            }

            @Override
            public void onPosting(AppPostValue socialPostValue) {
                socialController.post(socialPostValue, socialPostValue.getSocialId());
            }
        };

        return callback;
    }

    private Fragment getSurveyFragment(Survey survey, long questionId) {
        surveyFragment = SurveyFragment.newInstance(survey, questionId, isHearing);
        surveyFragment.setCallback(getSurveyCallback());
        return surveyFragment;
    }

    public SurveyFragment.Callback getSurveyCallback() {
        SurveyFragment.Callback callback = new SurveyFragment.Callback() {

            @Override
            public void onPageShowed(int pageId, int pagesCount) {
                String pagesCountValue = String.valueOf(pagesCount);
                String title = String.format(getString(R.string.survey_title), String.valueOf(pageId + 1), pagesCountValue);
                TitleHelper.setTitle(SurveyActivity.this, title);
            }

            @Override
            public void onError() {
                surveyFragment.setRefreshButtonVisible(View.VISIBLE);
            }

            @Override
            public void onSurveyDone(Survey survey) {
                doFinish(survey);
            }

            @Override
            public void onSurveyInterrupted(Survey survey) {
                SurveyActivity.this.survey = survey;
                if (survey.getKind().isHearing() || !isRedirectNeed(survey)) {
                    getSummaryFragment(survey);
                    replaceFragment(surveySummaryFragment);
                } else {
                    doInterrupt(survey);
                }
            }

            @Override
            public void onPosting(AppPostValue socialPostValue) {
                socialController.post(socialPostValue, socialPostValue.getSocialId());
            }
        };

        return callback;
    }

    private void doInterrupt(final Survey survey) {
        if ((survey.isActive() || survey.isInterrupted()) && isHasAnswer()) {
            WebSurveyDataSource surveyDataSource = new WebSurveyDataSource(SurveyActivity.this);
            surveyDataSource.save(survey,
                    new SaveListener.SaveOnInterruptListener(SurveyActivity.this, survey), true);
        } else {
            finish();
        }
    }

    private boolean isHasAnswer() {
        boolean result = false;
        List<SurveyQuestion> questionList = survey.getFilteredQuestionList();
        for (SurveyQuestion question : questionList) {
            try {
                question.verify();
                result = true;
                break;
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private void doFinish(final Survey survey) {
        if (survey.isActive() || survey.isInterrupted()) {
            boolean ok = false;
            try {
                survey.verify();
                ok = true;
            } catch (VerificationException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (ok) {
                SharedPreferencesSurveyManager manager = new SharedPreferencesSurveyManager(SurveyActivity.this);
                manager.saveCurrentPage(survey);
                WebSurveyDataSource surveyDataSource = new WebSurveyDataSource(SurveyActivity.this);
                Runnable tryFinish = new Runnable() {
                    @Override
                    public void run() {
                        if (survey.isShowPollStats()) {
                            loadSurvey();
                        } else {
                            SurveyActivity.this.finish();
                        }
                    }
                };
                surveyDataSource.save(survey, new SaveListener.SaveOnFinishListener(SurveyActivity.this, survey, socialController, tryFinish), false);
            }
        } else {
            finish();
        }
    }

    public interface Callback {
        Callback STUB = new Callback() {
            @Override
            public void onBackPressed() {
            }

            @Override
            public void onUpPressed() {

            }

            @Override
            public void onLocationUpdated() {
            }
        };

        void onBackPressed();

        void onUpPressed();

        void onLocationUpdated();
    }

}