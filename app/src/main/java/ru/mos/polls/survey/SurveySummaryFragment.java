package ru.mos.polls.survey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.common.view.VotersView;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.subscribes.controller.SubscribesUIController;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.experts.DetailsExpertsActivity;
import ru.mos.polls.survey.hearing.gui.activity.MeetingActivity;
import ru.mos.polls.survey.hearing.model.Meeting;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.source.SurveyDataSource;
import ru.mos.polls.survey.source.WebSurveyDataSource;
import ru.mos.polls.survey.summary.ExpertsView;
import ru.mos.polls.survey.summary.QuestionsView;
import ru.mos.polls.survey.summary.SurveyHeader;
import ru.mos.polls.survey.summary.SurveyTitleView;


public class SurveySummaryFragment extends Fragment implements SurveyActivity.Callback {
    public static final String EXTRA_SURVEY_ID = "extra_survey_id";

    public static SurveySummaryFragment newInstance(Survey survey, boolean isHearing) {
        SurveySummaryFragment instance = new SurveySummaryFragment();
        instance.setSurvey(survey);
        instance.setHearing(isHearing);
        return instance;
    }

    private Unbinder unbinder;
    private BaseActivity activity;

    @BindView(R.id.stub)
    View stubView;
    @BindView(R.id.questionsView)
    QuestionsView questionsView;
    @BindView(R.id.experts)
    ExpertsView expertsView;
    @BindView(R.id.header)
    SurveyHeader surveyHeader;
    @BindView(R.id.baseContainer)
    ScrollView baseContainer;
    @BindView(R.id.votesInfo)
    VotersView votersView;
    @BindView(R.id.shareButton)
    Button shareButton;

    private Survey survey = null;
    private Callback callback = Callback.STUB;
    private boolean isHearing;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (survey.getKind().isHearing() && MeetingActivity.isSubscribe(resultCode, requestCode)) {
            List<Meeting> meetings = survey.getMeetings();
            if (meetings != null && meetings.size() > 0) {
                survey.getMeetings().get(0).setStatus(Meeting.Status.REGISTERED);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey_summary, null);
        unbinder = ButterKnife.bind(this, view);
        restoreSavedState(savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        renderShareButton();
    }

    private void renderShareButton() {
        if (survey != null) {
            shareButton.setVisibility(survey.isPassed() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
    }

    private void findViews(View view) {
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        activity = (BaseActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        TitleHelper.setTitle(getActivity(), R.string.title_summary);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (survey != null) {
            refreshUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (survey != null)
            outState.putLong(EXTRA_SURVEY_ID, survey.getId());
    }

    @Override
    public void onBackPressed() {
        if (survey == null) {
            activity.finish();//если сурвей нулл то просто закрыть это активити, ничего не делая или что то еще?
        }
        if (!survey.getKind().isHearing() && (survey.isPassed() || !isHasAnswer())) {
            /**
             * не отображаем диалог для пройденных опросов
             */
            activity.finish();
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
                        activity.finish();
                    }
                });
                builder.show();
            } else {
                activity.finish();
            }
        } else {
            callback.onSurveyInterrupted(survey);
        }
    }

    @Override
    public void onUpPressed() {
        onBackPressed();
    }

    @Override
    public void onLocationUpdated() {
    }

    public void setCallback(Callback callback) {
        if (callback == null) {
            this.callback = Callback.STUB;
        } else {
            this.callback = callback;
        }
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

    public Survey getSurvey() {
        return survey;
    }

    /**
     * Обновление пользовательского интерфейса
     */
    protected void refreshUI() {
        SharedPreferencesSurveyManager manager = new SharedPreferencesSurveyManager(getActivity());
        manager.fill(survey);
        renderShareButton();
        surveyHeader.display(survey);
        if (survey.getKind().isHearing()) {
            surveyHeader.displayHearingInfo(this, survey);
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
            TitleHelper.setTitle(getActivity(), getString(R.string.title_hearing_survey_summary));
        }
    }

    private View.OnClickListener gotoQuestion = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (callback != null) {
                long questionId = survey.getQuestionsOrder().get(0);
                int pageIndex = 1;
                if ((survey.isActive() || survey.isInterrupted()) && isHasAnswer()) {
                    SurveyQuestion surveyQuestion = survey.getFirstNotCheckedQuestion();
                    if (surveyQuestion != null) {
                        questionId = surveyQuestion.getId();
                        pageIndex = survey.getFilteredQuestionList().indexOf(surveyQuestion);
                    }
                }
                survey.setCurrentPageIndex(pageIndex);
                callback.onQuestionClicked(survey, questionId);
            }
        }
    };

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (AGApplication.IS_LOCAL_SUBSCRIBE_ENABLE
                && survey != null && (survey.isActive() || survey.isInterrupted())) {
            inflater.inflate(R.menu.subscribe, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final boolean result;
        switch (item.getItemId()) {
            case R.id.action_subscribe:
                showDialogSubscribe();
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    private void showDialogSubscribe() {
        if (survey != null) {
            SubscribesUIController subscribesUIController =
                    new SubscribesUIController((BaseActivity) getActivity());
            subscribesUIController.showSubscribeDialogForPoll(survey);
        }
    }

    private void restoreSavedState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            long surveyId = savedInstanceState.getLong(EXTRA_SURVEY_ID);
            ((SurveyActivity) getActivity()).setCallback(this);
            ((SurveyActivity) getActivity()).setCurrentFragment(this);
            setCallback(((SurveyActivity) getActivity()).getSummaryFragmentCallback());
            loadSurvey(surveyId);
        }
    }

    private void loadSurvey(long surveyId) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        SurveyDataSource.LoadListener listener = new SurveyDataSource.LoadListener() {

            @Override
            public void onLoaded(Survey s) {
                survey = s;
                refreshUI();
                dismissProgress();
            }

            @Override
            public void onError(String message) {
                dismissProgress();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

            private void dismissProgress() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

        };
        SurveyDataSource surveyDataSource = new WebSurveyDataSource((BaseActivity) getActivity());
        surveyDataSource.load(surveyId, isHearing, listener);
    }

    @OnClick(R.id.shareButton)
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
        SocialUIController.showSocialsDialogForPoll((BaseActivity) getActivity(), survey, false, listener);
    }

    public interface Callback {
        Callback STUB = new Callback() {
            @Override
            public void onQuestionClicked(Survey survey, long questionId) {
            }

            @Override
            public void onSurveyDone(Survey survey) {
            }

            @Override
            public void onSurveyInterrupted(Survey survey) {
            }

            @Override
            public void onPosting(AppPostValue appPostValue) {
            }
        };

        void onQuestionClicked(Survey survey, long questionId);

        void onSurveyDone(Survey survey);

        void onSurveyInterrupted(Survey survey);

        void onPosting(AppPostValue appPostValue);
    }
}
