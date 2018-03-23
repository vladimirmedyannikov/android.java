package ru.mos.polls.survey.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentSurveyBinding;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.vm.SurveyFragmentVM;

public class SurveyFragment extends MenuBindingFragment<SurveyFragmentVM, FragmentSurveyBinding> implements BackPressedListener {
    public static final String EXTRA_POLL_ID = "extra_poll_id";
    public static final String EXTRA_PAGE = "extra_page_index";
    public static final String EXTRA_QUESTION_ID = "extra_question_id";

    private Survey survey;
    private Callback callback = Callback.STUB;
    private long questionId;
    private long pollId;
    private boolean isHearing;

    public static SurveyFragment newInstance(Survey survey, long questionId, boolean isHearing) {
        SurveyFragment instance = new SurveyFragment();
        instance.setSurvey(survey);
        instance.setQuestionId(questionId);
        instance.isHearing = isHearing;
        return instance;
    }

    public SurveyFragment() {
    }

    @Override
    protected SurveyFragmentVM onCreateViewModel(FragmentSurveyBinding binding) {
        SurveyFragmentVM surveyFragmentVM = new SurveyFragmentVM(this, binding);
        surveyFragmentVM.setQuestionId(questionId);
        surveyFragmentVM.setSurvey(survey);
        surveyFragmentVM.setHearing(isHearing);
        surveyFragmentVM.setPollId(pollId);
        surveyFragmentVM.setCallback(callback);
        return surveyFragmentVM;
    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        getViewModel().restoreFragmentState(savedInstanceState);
    }

    public void setSurvey(Survey survey) {
        if (survey != null) {
            pollId = survey.getId();
            this.survey = survey;
        }
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    @Override
    public int getMenuResource() {
        int menu = 0;
        if (AGApplication.IS_LOCAL_SUBSCRIBE_ENABLE
                && survey != null && (survey.isActive() || survey.isInterrupted())) {
            menu = R.menu.subscribe;
        }
        return menu;
    }

    public void setCallback(Callback c) {
        if (c == null) {
            callback = Callback.STUB;
        } else {
            callback = c;
        }
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_survey;
    }


    @Override
    public void onLocationUpdated() {
    }

    @Override
    public void onBack() {
        getViewModel().interrupt();
    }

    @Override
    public void onUp() {
        getViewModel().interruptUp();
    }

    public interface Callback {

        public static final Callback STUB = new Callback() {
            @Override
            public void onPageShowed(int pageId, int pagesCount) {
            }

            @Override
            public void onError() {
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

        void onPageShowed(int pageId, int pagesCount);

        void onError();

        void onSurveyDone(Survey survey);

        void onSurveyInterrupted(Survey survey);

        void onPosting(AppPostValue appPostValue);
    }
}