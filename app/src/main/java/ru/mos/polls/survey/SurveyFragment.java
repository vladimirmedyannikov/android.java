package ru.mos.polls.survey;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.helpers.AppsFlyerConstants;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.quests.ProfileQuestActivity;
import ru.mos.polls.social.model.SocialPostValue;
import ru.mos.polls.subscribes.controller.SubscribesUIController;
import ru.mos.polls.survey.questions.ListSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.source.StubSurveyDataSource;
import ru.mos.polls.survey.source.SurveyDataSource;
import ru.mos.polls.survey.source.WebSurveyDataSource;
import ru.mos.polls.survey.summary.ProgressView;
import ru.mos.polls.survey.variants.SelectSurveyVariant;
import ru.mos.polls.survey.variants.SurveyVariant;
import ru.mos.polls.survey.variants.select.GorodSelectObject;
import ru.mos.polls.survey.variants.select.SelectObject;
import ru.mos.polls.survey.variants.select.ServiceSelectObject;

public class SurveyFragment extends Fragment implements SurveyActivity.Callback, SurveyButtons.CallBack {
    private static final String EXTRA_POLL_ID = "extra_poll_id";
    private static final String EXTRA_PAGE = "extra_page_index";
    private static final String EXTRA_QUESTION_ID = "extra_question_id";

    private Unbinder unbinder;
    private Survey survey;
    private SurveyDataSource surveyDataSource;

    @BindView(R.id.buttonContainer)
    SurveyButtons mSurveyButtons;
    @BindView(R.id.stubOffline)
    View stubOffline;
    @BindView(R.id.questionContainer)
    ViewGroup questionContainerViewGroup;
    @BindView(R.id.progressView)
    ProgressView progressView;

    private Callback callback = Callback.STUB;
    private SharedPreferencesSurveyManager manager;
    private ProgressDialog progressDialog;
    private long questionId;
    private long pollId;
    private boolean isHearing;
    private Intent data;
    private int requestCode;
    private SparseArray<SelectSurveyVariant> selectSurveyVariantList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        if (AGApplication.USE_STUB_SURVEY_DATASOURCE) {
            surveyDataSource = new StubSurveyDataSource(getActivity());
        } else {
            surveyDataSource = new WebSurveyDataSource((BaseActivity) getActivity());
        }
        manager = new SharedPreferencesSurveyManager(getActivity());

        setHasOptionsMenu(true);
        getVariantList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = View.inflate(getActivity(), R.layout.fragment_survey, null);
        unbinder = ButterKnife.bind(this, result);
        mSurveyButtons.setFragment(SurveyFragment.this);
        mSurveyButtons.setSurvey(survey);
        restoreFragmentState(savedInstanceState);
        return result;
    }

    /**
     * Заглушка + кнопка обновить
     */
    @OnClick(R.id.refreshSurvey)
    void refresh() {
        setRefreshButtonVisible(View.VISIBLE);
        loadSurvey(survey.getId(), questionId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TitleHelper.setTitle(getActivity(), R.string.title_survey_question);
        if (survey != null) {
            renderScreen();
        } else {
            loadSurvey(pollId, questionId);
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
    public void onPause() {
        super.onPause();
        tryToSaveScrollableState();
    }

    public SurveyFragment() {
    }

    public static SurveyFragment newInstance(Survey survey, long questionId, boolean isHearing) {
        SurveyFragment instance = new SurveyFragment();
        instance.setSurvey(survey);
        instance.setQuestionId(questionId);
        instance.isHearing = isHearing;
        return instance;
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

    private void getVariantList() {
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
        checkingForParentId();
    }

    /**
     * проверяем поле parent_id, если есть то берем из списка по ключу, где ключ id ответа
     */
    protected void checkingForParentId() {
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

    public void setSurvey(Survey survey) {
        if (survey != null) {
            pollId = survey.getId();
            this.survey = survey;
        }
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
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

    public void setCallback(Callback c) {
        if (c == null) {
            callback = Callback.STUB;
        } else {
            callback = c;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ProfileQuestActivity.onResult(requestCode, resultCode)) {
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
    }

    private void showDialogSubscribe() {
        if (survey != null) {
            SubscribesUIController subscribesUIController =
                    new SubscribesUIController((BaseActivity) getActivity());
            subscribesUIController.showSubscribeDialogForPoll(survey);
        }
    }

    @Override
    public void doNext() {
        saveAnswer();
        if (survey.processNext()) {
            renderQuestion();
        }
    }

    @Override
    public void doResult() {
        boolean active = survey.getStatus() != Survey.Status.PASSED;
        if (active) {
            if (isSurveyVerifyOk()) {
                callback.onSurveyDone(survey);
            }
        } else {
            onBackPressed();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (survey != null) {
            outState.putLong(EXTRA_POLL_ID, survey.getId());
            outState.putLong(EXTRA_QUESTION_ID, survey.getCurrentQuestionId());
            if (survey.isActive() || survey.isPassed()) {
                manager.saveCurrentPage(survey);
            } else {
                outState.putInt(EXTRA_PAGE, survey.getCurrentPageIndex());
            }
        }
    }

    public void loadSurvey(long pollId, final long questionId) {
        setRefreshButtonVisible(View.GONE);
        this.questionId = questionId;
        SurveyDataSource.LoadListener listener = new SurveyDataSource.LoadListener() {
            @Override
            public void onLoaded(Survey s) {
                survey = s;
                mSurveyButtons.setSurvey(survey);
                renderScreen();
                dismissProgress();
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
                dismissProgress();
            }

            private void dismissProgress() {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ignored) {
                }
            }
        };
        progressDialog.show();
        surveyDataSource.load(pollId, isHearing, listener);
    }

    private void restoreFragmentState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            pollId = savedInstanceState.getLong(EXTRA_POLL_ID);
            questionId = savedInstanceState.getLong(EXTRA_QUESTION_ID);
            ((SurveyActivity) getActivity()).setCallback(this);
            ((SurveyActivity) getActivity()).setCurrentFragment(this);
            setCallback(((SurveyActivity) getActivity()).getSurveyCallback());
        }
    }

    private void renderScreen() {
        survey.setListener(new Survey.Listener() {

            @Override
            public void onSurveyVariantBeforeClick(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant) {

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
        View v = survey.getView(getActivity(), this, questionId);
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
    public void onLocationUpdated() {
    }

    @Override
    public void onBackPressed() {
        interrupt();
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
            public void onPosting(SocialPostValue socialPostValue) {
            }
        };

        void onPageShowed(int pageId, int pagesCount);

        void onError();

        void onSurveyDone(Survey survey);

        void onSurveyInterrupted(Survey survey);

        void onPosting(SocialPostValue socialPostValue);
    }
}