package ru.mos.polls.survey.vm;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.List;

import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.common.view.VotersView;
import ru.mos.polls.databinding.FragmentSurveySummaryBinding;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.subscribes.controller.SubscribesUIController;
import ru.mos.polls.survey.SharedPreferencesSurveyManager;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.SurveySummaryFragment;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.experts.DetailsExpertsActivity;
import ru.mos.polls.survey.hearing.gui.activity.MeetingActivity;
import ru.mos.polls.survey.hearing.model.Meeting;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.source.SurveyDataSource;
import ru.mos.polls.survey.source.WebSurveyDataSourceRX;
import ru.mos.polls.survey.summary.ExpertsView;
import ru.mos.polls.survey.summary.QuestionsView;
import ru.mos.polls.survey.summary.SurveyHeader;
import ru.mos.polls.survey.summary.SurveyTitleView;
import ru.mos.polls.survey.ui.PollsSummaryFragment;
import ru.mos.polls.survey.ui.SurveyMainFragment;

public class PollsSummaryFragmentVM extends UIComponentFragmentViewModel<PollsSummaryFragment, FragmentSurveySummaryBinding> {

    View stubView;
    QuestionsView questionsView;
    ExpertsView expertsView;
    SurveyHeader surveyHeader;
    ScrollView baseContainer;
    VotersView votersView;
    Button shareButton;

    private Survey survey = null;
    private SurveySummaryFragment.Callback callback = SurveySummaryFragment.Callback.STUB;
    private boolean isHearing;

    public PollsSummaryFragmentVM(PollsSummaryFragment fragment, FragmentSurveySummaryBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentSurveySummaryBinding binding) {
        stubView = binding.stub;
        questionsView = binding.questionsView;
        expertsView = binding.experts;
        surveyHeader = binding.header;
        baseContainer = binding.baseContainer;
        votersView = binding.votesInfo;
        shareButton = binding.shareButton;
        setListeners();
        TitleHelper.setTitle(getActivity(), R.string.title_summary);

    }

    public void setListeners() {
        /**
         * панель менения экспертов
         */
        expertsView.setCallback(new ExpertsView.Callback() {
            @Override
            public void onChooseExpert(DetailsExpert detailsExpert) {
                Statistics.pollsEnterExperts(survey.getId(), 0);
                GoogleStatistics.Survey.pollsEnterExperts(survey.getId(), 0);
                DetailsExpertsActivity.startActivityByPollId(getActivity(),
                        detailsExpert,
                        survey.getId(),
                        survey.getKind().isHearing());
            }
        });
        shareButton.setOnClickListener(v -> onClickShareButton());
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return null;
    }

    @Override
    public void onStart() {
        if (survey != null) {
            refreshUI();
        }
    }

    @Override
    public void onResume() {
        renderShareButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (survey.getKind().isHearing() && MeetingActivity.isSubscribe(resultCode, requestCode)) {
            List<Meeting> meetings = survey.getMeetings();
            if (meetings != null && meetings.size() > 0) {
                survey.getMeetings().get(0).setStatus(Meeting.Status.REGISTERED);
            }
        }
    }

    private void renderShareButton() {
        if (survey != null) {
            shareButton.setVisibility(survey.isPassed() ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Обновление пользовательского интерфейса
     */
    public void refreshUI() {
        SharedPreferencesSurveyManager manager = new SharedPreferencesSurveyManager(getActivity());
        manager.fill(survey);
        renderShareButton();
        surveyHeader.display(survey);
        if (survey.getKind().isHearing()) {
            surveyHeader.displayHearingInfo(getFragment(), survey);
        }
        surveyHeader.setStateListener(new SurveyTitleView.StateListener() {
            @Override
            public void onExpand() {
                surveyHeader.scrollToBegin(baseContainer);
            }

            @Override
            public void onCollapse() {
                surveyHeader.scrollToEnd(baseContainer);
            }
        });

        questionsView.display(survey, new QuestionsView.Callback() {
            @Override
            public void onQuestionClicked(Survey survey, long questionId) {
                if (callback != null) {
                    int pageIndex = survey.getQuestionsOrder().indexOf(questionId);
                    survey.setCurrentPageIndex(pageIndex);
                    callback.onQuestionClicked(survey, questionId);
                }
            }
        });
        expertsView.display((BaseActivity) getActivity(), survey);
        votersView.display(survey);
        stubView.setVisibility(View.GONE);
        Statistics.enterQuestion(survey.getId());
        GoogleStatistics.Survey.enterQuestion(survey.getId());
        if (survey.getKind().isHearing()) {
            TitleHelper.setTitle(getActivity(), getActivity().getString(R.string.title_hearing_survey_summary));
        }
    }

    private void loadSurvey(long surveyId) {
        progressable.begin();
        SurveyDataSource.LoadListener listener = new SurveyDataSource.LoadListener() {

            @Override
            public void onLoaded(Survey s) {
                progressable.end();
                survey = s;
                refreshUI();
            }

            @Override
            public void onError(String message) {
                progressable.end();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        };
        SurveyDataSource surveyDataSource = new WebSurveyDataSourceRX((BaseActivity) getActivity());
        surveyDataSource.load(surveyId, isHearing, listener);
    }

    void onClickShareButton() {
        SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
            @Override
            public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                socialPostValue.setId(survey.getId());
                callback.onPosting(socialPostValue);
            }

            @Override
            public void onCancel() {

            }
        };
        SocialUIController.showSocialsDialogForPoll(disposables, (BaseActivity) getActivity(), survey, false, listener, null);
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_subscribe:
                showDialogSubscribe();
                break;
        }
    }

    private void showDialogSubscribe() {
        if (survey != null) {
            SubscribesUIController subscribesUIController =
                    new SubscribesUIController((BaseActivity) getActivity());
            subscribesUIController.showSubscribeDialogForPoll(survey);
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

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    /**
     * установка отметки специального опроса
     *
     * @param isSpecial true - опрос специальный
     */
    public void setHearing(boolean isSpecial) {
        this.isHearing = isSpecial;
    }

    public void setCallback(SurveySummaryFragment.Callback callback) {
        if (callback == null) {
            this.callback = SurveySummaryFragment.Callback.STUB;
        } else {
            this.callback = callback;
        }
    }

    public void onBack() {
        if (survey == null) {
            getActivity().finish();//если сурвей нулл то просто закрыть это активити, ничего не делая или что то еще?
        }
        if (!survey.getKind().isHearing() && (survey.isPassed() || !isHasAnswer())) {
            /**
             * не отображаем диалог для пройденных опросов
             */
            getActivity().finish();
        } else if (survey.getKind().isHearing()) {
            if ((survey.isActive() || survey.isInterrupted()) && isHasAnswer()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.attantion);
                builder.setMessage(R.string.title_message_results_will_not_be_save);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.ag_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferencesSurveyManager manager = new SharedPreferencesSurveyManager(getActivity());
                        manager.remove(survey.getId());
                        getActivity().finish();
                    }
                });
                builder.show();
            } else {
                getActivity().finish();
            }
        } else {
            callback.onSurveyInterrupted(survey);
        }
    }


    public void restoreSavedState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            long surveyId = savedInstanceState.getLong(PollsSummaryFragment.EXTRA_SURVEY_ID);
            ((SurveyMainFragment)getParentFragment()).getViewModel().setBackPressedListener(getFragment());
            ((SurveyMainFragment)getParentFragment()).getViewModel().setCurrentFragment(getFragment());
            ((SurveyMainFragment)getParentFragment()).getViewModel().getSummaryFragmentCallback();
//            ((SurveyActivity) getActivity()).setBackPressedListener(getFragment());
//            ((SurveyActivity) getActivity()).setCurrentFragment(getFragment());
//            setCallback(((SurveyActivity) getActivity()).getSummaryFragmentCallback());
            loadSurvey(surveyId);
        }
    }
}
