package ru.mos.polls.survey.vm;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentSurveyBinding;
import ru.mos.polls.helpers.AppsFlyerConstants;
import ru.mos.polls.profile.ui.activity.QuestActivity;
import ru.mos.polls.subscribes.controller.SubscribesUIController;
import ru.mos.polls.survey.SharedPreferencesSurveyManager;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.SurveyButtons;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.questions.ListSurveyQuestion;
import ru.mos.polls.survey.questions.RadioboxSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.source.StubSurveyDataSource;
import ru.mos.polls.survey.source.SurveyDataSource;
import ru.mos.polls.survey.source.WebSurveyDataSourceRX;
import ru.mos.polls.survey.summary.ProgressView;
import ru.mos.polls.survey.ui.SurveyFragment;
import ru.mos.polls.survey.ui.SurveyMainFragment;
import ru.mos.polls.survey.variants.SelectSurveyVariant;
import ru.mos.polls.survey.variants.SurveyVariant;
import ru.mos.polls.survey.variants.select.GorodSelectObject;
import ru.mos.polls.survey.variants.select.SelectObject;
import ru.mos.polls.survey.variants.select.ServiceSelectObject;

public class SurveyFragmentVM extends UIComponentFragmentViewModel<SurveyFragment, FragmentSurveyBinding> implements SurveyButtons.CallBack {
    private Survey survey;
    private SurveyDataSource surveyDataSource;

    SurveyButtons mSurveyButtons;
    View stubOffline;
    ViewGroup questionContainerViewGroup;
    ProgressView progressView;
    Button refresh;
    private SurveyFragment.Callback callback = SurveyFragment.Callback.STUB;
    private SharedPreferencesSurveyManager manager;
    private long questionId;
    private long pollId;
    private boolean isHearing;
    private Intent data;
    private int requestCode;
    private SparseArray<SelectSurveyVariant> selectSurveyVariantList;
    SurveyQuestion surveyQuestion;

    public SurveyFragmentVM(SurveyFragment fragment, FragmentSurveyBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with((UIComponent) (progressable = new ProgressableUIComponent()))
                .build();
    }

    @Override
    protected void initialize(FragmentSurveyBinding binding) {
        mSurveyButtons = binding.buttonContainer;
        stubOffline = binding.stubOffline;
        questionContainerViewGroup = binding.questionContainer;
        progressView = binding.progressView;
        refresh = binding.refreshSurvey;
        if (AGApplication.USE_STUB_SURVEY_DATASOURCE) {
            surveyDataSource = new StubSurveyDataSource(getActivity());
        } else {
            surveyDataSource = new WebSurveyDataSourceRX((BaseActivity) getActivity());
        }
        manager = new SharedPreferencesSurveyManager(getActivity());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        if (survey != null) {
            setViews();
        } else {
            loadSurvey(pollId, questionId);
        }
        refresh.setOnClickListener(v -> refresh());
    }

    public void setSurveyButtons() {
        mSurveyButtons.setCallBack(this);
        mSurveyButtons.setFragment(getFragment());
        mSurveyButtons.setSurvey(survey);
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_subscribe:
                showDialogSubscribe();
                break;
        }
    }

    @Override
    public void onPause() {
        tryToSaveScrollableState();
    }

    public void loadSurvey(long pollId, final long questionId) {
        progressable.begin();
        this.questionId = questionId;
        SurveyDataSource.LoadListener listener = new SurveyDataSource.LoadListener() {
            @Override
            public void onLoaded(Survey s) {
                progressable.end();
                survey = s;
                setViews();
                if (requestCode != -1 && data != null) {
                    survey.onActivityResult(requestCode, getActivity().RESULT_OK, data);
                    requestCode = -1;
                    data = null;
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                setRefreshButtonVisible(View.VISIBLE);
                progressable.end();
            }
        };
        surveyDataSource.load(pollId, isHearing, listener, progressable);
    }

    public void setViews() {
        setSurveyButtons();
        getVariantList();
        renderScreen();
    }

    void refresh() {
        setRefreshButtonVisible(View.VISIBLE);
        loadSurvey(survey.getId(), questionId);
    }

    private void renderScreen() {
        survey.setListener(new Survey.Listener() {

            @Override
            public void onSurveyVariantBeforeClick(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant) {
                SurveyFragmentVM.this.surveyQuestion = surveyQuestion;
            }

            @Override
            public void onSurveyVariantAfterClick(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant) {
                mSurveyButtons.renderButtons();
                processTitle();
                processProgress(surveyQuestion, surveyVariant);
                removeChildAnswer(surveyQuestion, surveyVariant);
            }

            @Override
            public void onSurveyVariantOnCommit(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant) {
            }

            @Override
            public void onSurveyVariantOnCancel(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant) {
            }

            @Override
            public void onRefreshSurvey() {
            }

        });
        mSurveyButtons.checkSurveyStatus(questionId, manager, callback);
        mSurveyButtons.renderButtons();
        renderQuestion();
        tryToRestoreScrollableState();
    }

    private ProgressView.Progressable questionProgressable = new ProgressView.Progressable() {
        @Override
        public boolean hasProgress(Object object) {
            boolean result = false;
            if (!survey.isOld()) {
                try {
                    ((SurveyQuestion) object).verify();
                    result = true;
                } catch (Exception ignored) {
                }
            }
            return result;
        }
    };

    /**
     * Удаляем вариант ответа {@link SurveyVariant} в вопросе {@link SurveyQuestion} с дочерним справочником {@link SelectObject}, если пользователь перевыбрал вариант ответа в текущем вопроса<br/>
     * Ищем во всех вопросах {@link Survey#getFilteredQuestionList()} вариант типа {@link SelectSurveyVariant}, у которого
     * {@link SurveyQuestion#getId()} равен {@link ServiceSelectObject#getParentId()} и удалем его.<br/>
     * Используется при выборе варианта ответа {@link ru.mos.polls.survey.Survey.Listener#onSurveyVariantAfterClick(SurveyQuestion, SurveyVariant)}
     *
     * @param currentQuestion       текущий вопрос {@link SurveyQuestion}
     * @param currentCheckedVariant вариант ответа, по которому кликнул пользователь {@link SurveyVariant}
     * @since 2.0.0
     */
    private void removeChildAnswer(SurveyQuestion currentQuestion, SurveyVariant currentCheckedVariant) {
        if (currentCheckedVariant instanceof SelectSurveyVariant) {
            for (SurveyQuestion questionIterator : survey.getFilteredQuestionList()) {
                boolean childWasFind = false;
                if (questionIterator.getId() != currentQuestion.getId()) {
                    for (SurveyVariant variantIterator : questionIterator.getVariantsList()) {
                        if (variantIterator instanceof SelectSurveyVariant) {
                            SelectObject selectObject = ((SelectSurveyVariant) variantIterator).getSelectObject();
                            if (selectObject instanceof ServiceSelectObject) {
                                String parentId = ((ServiceSelectObject) selectObject).getParentId();
                                if (parentId.equalsIgnoreCase(currentCheckedVariant.getBackId())) {
                                    questionIterator.reset();
                                    ((ServiceSelectObject) selectObject).setTitle("");
                                    manager.remove(survey.getId());
                                    manager.saveCurrentPage(survey);
                                    removeChildAnswer(questionIterator, variantIterator);
                                    renderScreen();
                                    childWasFind = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (childWasFind) {
                    break;
                }
            }
        }
    }

    private void renderQuestion() {
        mSurveyButtons.renderButtons();
        processTitle();
        displayQuestion();
        processProgress();
    }

    private void displayQuestion() {
        questionContainerViewGroup.removeAllViews();
        questionId = survey.getCurrentQuestionId();
        View v = survey.getView(getActivity(), getFragment(), questionId);
        questionContainerViewGroup.addView(v);
    }

    private boolean isSurveyVerifyOk() {
        List<SurveyQuestion> questionList = survey.getFilteredQuestionList();
        boolean isAllQuestionHasAnswer = true;
        for (SurveyQuestion question : questionList) {
            try {
                question.verify();
            } catch (VerificationException e) {
                isAllQuestionHasAnswer = false;
            }
        }
        return isAllQuestionHasAnswer;
    }

    private void processTitle() {
        List<SurveyQuestion> questionList = survey.getFilteredQuestionList();
        int total = questionList.size();
        long currentQuestionId = survey.getCurrentQuestionId();
        int currentPage = 0;
        for (SurveyQuestion question : questionList) {
            if (question.getId() == currentQuestionId) {
                break;
            }
            currentPage++;
        }
        callback.onPageShowed(currentPage, total);
    }

    private void processProgress() {
        long questionId = survey.getCurrentQuestionId();
        SurveyQuestion surveyQuestion = survey.getQuestion(questionId);
        processProgress(surveyQuestion, null);
    }

    private void processProgress(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant) {
        boolean isVerifyOk = false;
        if (!survey.isOld()) {
            try {
                surveyQuestion.verify();
                isVerifyOk = true;
            } catch (VerificationException ignored) {
            }
        }
        if (survey.getFilteredQuestionList().size() > 1) {
            int index = survey.getFilteredQuestionList().indexOf(surveyQuestion);
            progressView.setModel(new ArrayList<Object>(survey.getFilteredQuestionList()));
            progressView.setMarkerToObject(index, questionProgressable, isVerifyOk);
            if (progressView.getVisibility() == View.GONE) {
                progressView.setVisibility(View.VISIBLE);
            }
        } else {
            progressView.setVisibility(View.GONE);
        }
    }

    public void interrupt() {
        if (survey != null) {
            if (survey.getKind().isHearing()) {
                callback.onSurveyInterrupted(survey);
            } else {
                try {
                    survey.verify();
                    survey.getQuestion(survey.getCurrentQuestionId()).setPassed(true);
                } catch (VerificationException ignored) {
                }
                survey.endTiming();
                manager.saveCurrentPage(survey);
                callback.onSurveyInterrupted(survey);
            }
        }
    }

    public void interruptUp() {
        if (survey != null) {
            if (survey.getKind().isHearing()) {
                callback.onSurveyInterrupted(survey);
                ((SurveyMainFragment) getParentFragment()).getViewModel().getSummaryFragmentCallback().onSurveyInterrupted(survey);
            } else {
                try {
                    survey.verify();
                    survey.getQuestion(survey.getCurrentQuestionId()).setPassed(true);
                } catch (VerificationException ignored) {
                    ignored.printStackTrace();
                }
                survey.endTiming();
                manager.saveCurrentPage(survey);
                ((SurveyMainFragment) getParentFragment()).getViewModel().getSummaryFragmentCallback().onSurveyInterrupted(survey);
            }
        }
    }

    public void setSurvey(Survey survey) {
        if (survey != null) {
            pollId = survey.getId();
            this.survey = survey;
        }
    }

    public void setPollId(long pollId) {
        this.pollId = pollId;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public void setHearing(boolean hearing) {
        isHearing = hearing;
    }

    private void tryToSaveScrollableState() {
        if (survey != null) {
            SurveyQuestion surveyQuestion = survey.getQuestion(survey.getCurrentQuestionId());
            if (surveyQuestion != null && (surveyQuestion instanceof ListSurveyQuestion)) {
                int index = ((ListSurveyQuestion) surveyQuestion).getIndexOfScrollableView();
                int top = ((ListSurveyQuestion) surveyQuestion).getViewTopOfFirstItem();
                manager.saveScrollableIndex(survey.getId(), surveyQuestion.getId(), index);
                manager.saveScrollableTop(survey.getId(), surveyQuestion.getId(), top);
            }
        }
    }

    private void tryToRestoreScrollableState() {
        if (survey != null) {
            SurveyQuestion surveyQuestion = survey.getQuestion(survey.getCurrentQuestionId());
            if (surveyQuestion != null && (surveyQuestion instanceof ListSurveyQuestion)) {
                int index = manager.getScrollableIndex(survey.getId(), surveyQuestion.getId());
                int top = manager.getScrollableTop(survey.getId(), surveyQuestion.getId());
                ((ListSurveyQuestion) surveyQuestion).setScrollableState(index, top);
            }
        }
    }

    private void getVariantList() {
        if (survey != null) {
            List<SurveyQuestion> questionList = survey.getQuestionsList();
            selectSurveyVariantList = new SparseArray<>();
            for (SurveyQuestion question : questionList) {
                List<SurveyVariant> variantList = question.getVariantsList();
                for (SurveyVariant variant : variantList) {
                    if (variant instanceof SelectSurveyVariant) {
                        //кладем ответ в список где ключ его id
                        SelectSurveyVariant selectVariant = (SelectSurveyVariant) variant;
                        selectSurveyVariantList.append(Integer.parseInt(selectVariant.getBackId()), selectVariant);
                    }
                }
            }
        }
        checkingForParentId();
    }

    /**
     * проверяем поле parent_id, если есть то берем из списка по ключу, где ключ id ответа
     */
    public void checkingForParentId() {
        for (int i = 0; i < selectSurveyVariantList.size(); i++) {
            int key = selectSurveyVariantList.keyAt(i);
            SelectSurveyVariant sSV = selectSurveyVariantList.get(key);
            if (sSV.getObjectType().equals("gorod")) {
                GorodSelectObject selectObject = (GorodSelectObject) sSV.getSelectObject();
                if (!selectObject.getParentId().isEmpty()) {
                    String parentId = selectObject.getParentId();
                    SelectSurveyVariant currSsv = selectSurveyVariantList.get(Integer.parseInt(parentId));
                    GorodSelectObject currObject = (GorodSelectObject) currSsv.getSelectObject();
                    String idAnswer = currObject.getObjectId();
                    selectObject.setParentIdAnswer(idAnswer);
                }
            }
            if (sSV.getObjectType().equals("service")) {
                ServiceSelectObject selectObject = (ServiceSelectObject) sSV.getSelectObject();
                if (!"".equals(selectObject.getParentId())) {
                    String parentId = selectObject.getParentId();
                    SelectSurveyVariant currSsv = selectSurveyVariantList.get(Integer.parseInt(parentId));
                    if (currSsv != null) {
                        ServiceSelectObject currObject = (ServiceSelectObject) currSsv.getSelectObject();
                        long idAnswer = currObject.getId();
                        selectObject.setParentIdAnswer(String.valueOf(idAnswer));
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QuestActivity.REQUEST_ADD_FLAT && resultCode == Activity.RESULT_OK) {
            loadSurvey(pollId, questionId);
        } else {
            this.requestCode = requestCode;
            this.data = data;
            boolean b = false;
            if (survey != null) {
                b = survey.onActivityResult(requestCode, resultCode, data);
                if (resultCode == -1) {
                    manager.remove(questionId);
                }
            }
            if (!b) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        /**
         * Если вернулись со связаного справочника не выбрав вариант ответа то затираем ответ на него
         * только для RadioboxSurveyQuestion
         */
        if (requestCode == ServiceSelectObject.REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            if (surveyQuestion != null && surveyQuestion instanceof RadioboxSurveyQuestion) {
                RadioboxSurveyQuestion radioboxSurveyQuestion = (RadioboxSurveyQuestion) surveyQuestion;
                radioboxSurveyQuestion.reset();
                for (SurveyVariant surveyVariant : radioboxSurveyQuestion.getVariantsList()) {
                    removeChildAnswer(surveyQuestion, surveyVariant);
                }
                manager.remove(questionId);
                renderQuestion();
            }
        }
    }


    private void showDialogSubscribe() {
        if (survey != null) {
            SubscribesUIController subscribesUIController =
                    new SubscribesUIController((BaseActivity) getActivity());
            subscribesUIController.showSubscribeDialogForPoll(survey);
        }
    }

    /**
     * Если пользователь дал ответ на вопрос, то сохраняем его
     */
    private void saveAnswer() {
        if (survey != null) {
            if (survey.isActive()) {
                try {
                    survey.verify();
                    survey.getQuestion(survey.getCurrentQuestionId()).setPassed(true);
                    AppsFlyerLib.sendTrackingWithEvent(getActivity(), AppsFlyerConstants.QUESTION_ANSWERED, String.valueOf(survey.getCurrentQuestionId()));
                    survey.endTiming();
                    manager.saveCurrentPage(survey);
                } catch (VerificationException ignored) {
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (survey != null) {
            outState.putLong(SurveyFragment.EXTRA_POLL_ID, survey.getId());
            outState.putLong(SurveyFragment.EXTRA_QUESTION_ID, survey.getCurrentQuestionId());
            if (survey.isActive() || survey.isPassed()) {
                manager.saveCurrentPage(survey);
            } else {
                outState.putInt(SurveyFragment.EXTRA_PAGE, survey.getCurrentPageIndex());
            }
        }
    }

    /**
     * Показываем либо все ui компоненты экрана, либо заглушку
     *
     * @param visibility константное значение видимости из View для заглушки
     */
    public void setRefreshButtonVisible(int visibility) {
        int otherVisibility = visibility == View.VISIBLE ? View.GONE : View.VISIBLE;
        /**
         * Все ui компоненты экрана
         */
        questionContainerViewGroup.setVisibility(otherVisibility);
        mSurveyButtons.setVisibility(otherVisibility);
        /**
         * Заглушка
         */
        stubOffline.setVisibility(visibility);
    }

    public void restoreFragmentState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            pollId = savedInstanceState.getLong(SurveyFragment.EXTRA_POLL_ID);
            questionId = savedInstanceState.getLong(SurveyFragment.EXTRA_QUESTION_ID);
            ((SurveyMainFragment) getParentFragment()).getViewModel().setBackPressedListener(getFragment());
            ((SurveyMainFragment) getParentFragment()).getViewModel().setCurrentFragment(getFragment());
            ((SurveyMainFragment) getParentFragment()).getViewModel().getSurveyCallback();
        }
    }

    public void setCallback(SurveyFragment.Callback c) {
        if (c == null) {
            callback = SurveyFragment.Callback.STUB;
        } else {
            callback = c;
        }
    }

    @Override
    public void doPrev() {
        saveAnswer();
        int index = survey.doPrev();
        if (index >= 0) {
            survey.setCurrentPageIndex(index);
            renderQuestion();
        }
        checkingForParentId();
    }

    @Override
    public void doNext() {
        saveAnswer();
        if (survey.processNext()) {
            renderQuestion();
        }
        checkingForParentId();
    }

    @Override
    public void doResult() {
        boolean active = survey.getStatus() != Survey.Status.PASSED;
        if (active) {
            if (isSurveyVerifyOk()) {
                callback.onSurveyDone(survey);
            }
        } else {
            getFragment().onBack();
        }
    }
}
