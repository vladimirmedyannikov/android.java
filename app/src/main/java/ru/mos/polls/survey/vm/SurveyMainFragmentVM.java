package ru.mos.polls.survey.vm;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.databinding.FragmentMainSurveyBinding;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.profile.vm.PguAuthFragmentVM;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.SharedPreferencesSurveyManager;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.SurveySummaryFragment;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.source.SaveListener;
import ru.mos.polls.survey.source.SurveyDataSource;
import ru.mos.polls.survey.source.WebSurveyDataSourceRX;
import ru.mos.polls.survey.ui.BackPressedListener;
import ru.mos.polls.survey.ui.InfoSurveyFragment;
import ru.mos.polls.survey.ui.PollsSummaryFragment;
import ru.mos.polls.survey.ui.SurveyFragment;
import ru.mos.polls.survey.ui.SurveyMainFragment;
import ru.mos.polls.survey.variants.ActionSurveyVariant;
import ru.mos.social.callback.PostCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.PostValue;
import ru.mos.social.model.social.Social;

public class SurveyMainFragmentVM extends UIComponentFragmentViewModel<SurveyMainFragment, FragmentMainSurveyBinding> {
    private long surveyId;
    private boolean isHearing;
    private Survey survey;
    private SurveyFragment surveyFragment;
    private PollsSummaryFragment pollsSummaryFragment;
    private Fragment currentFragment;

    private SocialController socialController;
    private BackPressedListener backPressedListener = BackPressedListener.STUB;
    private PostCallback postCallback = new PostCallback() {
        @Override
        public void postSuccess(Social social, @Nullable PostValue postValue) {
            SocialUIController.sendPostingResult(getActivity(), (AppPostValue) postValue, null);
        }

        @Override
        public void postFailure(Social social, @Nullable PostValue postValue, Exception e) {
            SocialUIController.sendPostingResult(getActivity(), (AppPostValue) postValue, e);
        }
    };

    public SurveyMainFragmentVM(SurveyMainFragment fragment, FragmentMainSurveyBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentMainSurveyBinding binding) {

    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().with(new ProgressableUIComponent()).build();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        if (setSurveyId() && !isNeedLoading(getFragment().getSavedInstanceState())) {
            loadSurvey();
        }
        socialController = ((BaseActivity) getActivity()).getSocialController();
    }


    @Override
    public void onResume() {
        socialController.getEventController().registerCallback(postCallback);
        SocialUIController.registerPostingReceiver(getActivity());
    }

    @Override
    public void onPause() {
        socialController.getEventController().unregisterAllCallback();
        SocialUIController.unregisterPostingReceiver(getActivity());
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    public void setBackPressedListener(BackPressedListener backPressedListener) {
        if (backPressedListener == null) {
            this.backPressedListener = BackPressedListener.STUB;
        } else {
            this.backPressedListener = backPressedListener;
        }
    }

    public void onBackPressed() {
        if (currentFragment != null) {
            if (currentFragment instanceof SurveyFragment) {
                backPressedListener.onUp();
            } else {
                backPressedListener.onBack();
            }
        } else {
            backPressedListener.onBack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        tryToExecuteActionCallback(requestCode);
        /**
         * Вернулись с эерана авторизации в пгу
         * еще раз отпралвяем запрос на
         */
        if (PguAuthFragmentVM.isAuth(resultCode, requestCode, data)) {
            if (survey.getKind().isHearing()) {
                doFinish(survey);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SurveyMainFragment.IS_ACTIVITY_RESTARTED, true);
    }

    private void tryToExecuteActionCallback(int requestCode) {
        if (requestCode == ActionSurveyVariant.REQUEST_FILLING_LOCATION_DATA) {
            backPressedListener.onLocationUpdated();
        }
    }

    private boolean isNeedLoading(Bundle savedInstanceState) {
        boolean result = false;
        if (savedInstanceState != null) {
            result = savedInstanceState.getBoolean(SurveyMainFragment.IS_ACTIVITY_RESTARTED);
        }
        return result;
    }

    private void loadSurvey() {
        WebSurveyDataSourceRX webSurveyDataSourceRX = new WebSurveyDataSourceRX((BaseActivity) getActivity());
        webSurveyDataSourceRX.load(surveyId, isHearing, new SurveyDataSource.LoadListener() {
            @Override
            public void onLoaded(Survey loadedSurvey) {
                survey = loadedSurvey;
                setFragment();
            }

            @Override
            public void onError(String message) {
            }
        }, getProgressable());
    }

    private boolean setSurveyId() {
        Bundle bundle = getFragment().getArguments();
        isHearing = bundle.getBoolean(SurveyMainFragment.EXTRA_IS_HEARING, false);
        surveyId = bundle.getLong(SurveyMainFragment.EXTRA_SURVEY_ID, -1);
        if (UrlSchemeController.hasUri(getActivity())) {
            if (UrlSchemeController.isHearing(getActivity())) {
                isHearing = true;
            }
            UrlSchemeController.startPoll((BaseActivity) getActivity(), new UrlSchemeController.IdListener() {
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
        if (survey.getKind().isMKD() || survey.getKind().isInform()) {
            if (survey.isInformSurveyOk()) {
                replaceFragment(getInfoSurveyFragment(survey, survey.getQuestionsOrder().get(0)));
                return;
            }
        }
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
        setBackPressedListener((BackPressedListener) fragment);
        FragmentManager fragmentManager = getFragment().getChildFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.root, fragment)
                .commit();
    }

    private boolean isRedirectNeed(Survey survey) {
        boolean result;
        String shortHtml = survey.getTextShortHtml();
        result = shortHtml == null || TextUtils.isEmpty(shortHtml) || "null".equalsIgnoreCase(shortHtml);
        return result;
    }

    private Fragment getSummaryFragment(Survey survey) {
        pollsSummaryFragment = PollsSummaryFragment.newInstance(survey, true, getSummaryFragmentCallback());
        return pollsSummaryFragment;
    }

    public SurveySummaryFragment.Callback getSummaryFragmentCallback() {
        SurveySummaryFragment.Callback callback = new SurveySummaryFragment.Callback() {
            @Override
            public void onQuestionClicked(Survey survey, long questionId) {
                SurveyMainFragmentVM.this.survey = survey;
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

    private Fragment getInfoSurveyFragment(Survey survey, long questionId) {
        InfoSurveyFragment surveyFragment = InfoSurveyFragment.newInstance(survey, questionId);
        surveyFragment.setCallback(getSurveyCallback());
        return surveyFragment;
    }

    public SurveyFragment.Callback getSurveyCallback() {
        SurveyFragment.Callback callback = new SurveyFragment.Callback() {

            @Override
            public void onPageShowed(int pageId, int pagesCount) {
                String pagesCountValue = String.valueOf(pagesCount);
                String title = String.format(getActivity().getString(R.string.survey_title), String.valueOf(pageId + 1), pagesCountValue);
                TitleHelper.setTitle(getActivity(), title);
            }

            @Override
            public void onError() {
                surveyFragment.getViewModel().setRefreshButtonVisible(View.VISIBLE);
            }

            @Override
            public void onSurveyDone(Survey survey) {
                doFinish(survey);
            }

            @Override
            public void onSurveyInterrupted(Survey survey) {
                SurveyMainFragmentVM.this.survey = survey;
                if (survey.getKind().isHearing() || !isRedirectNeed(survey)) {
                    getSummaryFragment(survey);
                    replaceFragment(pollsSummaryFragment);
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

    public void doInterrupt(final Survey survey) {
        if ((survey.isActive() || survey.isInterrupted()) && isHasAnswer()) {
            WebSurveyDataSourceRX sourceRX = new WebSurveyDataSourceRX((BaseActivity) getActivity());
            sourceRX.save(survey,
                    new SaveListener.SaveOnInterruptListener((BaseActivity) getActivity(), survey), true, getProgressable());
        } else {
            getActivity().finish();
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
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (ok) {
                SharedPreferencesSurveyManager manager = new SharedPreferencesSurveyManager(getActivity());
                manager.saveCurrentPage(survey);
                WebSurveyDataSourceRX sourceRX = new WebSurveyDataSourceRX((BaseActivity) getActivity());
                Runnable tryFinish = new Runnable() {
                    @Override
                    public void run() {
                        if (survey.isShowPollStats()) {
                            loadSurvey();
                        } else {
                            getActivity().finish();
                        }
                    }
                };
                sourceRX.save(survey, new SaveListener.SaveOnFinishListener((BaseActivity) getActivity(), survey, socialController, tryFinish), false, getProgressable());
            }
        } else {
            getActivity().finish();
        }
    }
}
