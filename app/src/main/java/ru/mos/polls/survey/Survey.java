package ru.mos.polls.survey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.common.model.Message;
import ru.mos.polls.common.view.VotersView;
import ru.mos.polls.model.Information;
import ru.mos.polls.poll.model.Kind;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.experts.DetailsExpertsActivity;
import ru.mos.polls.survey.filter.Filter;
import ru.mos.polls.survey.hearing.model.Exposition;
import ru.mos.polls.survey.hearing.model.Meeting;
import ru.mos.polls.survey.questions.ListViewSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.status.ActiveStatusProcessor;
import ru.mos.polls.survey.status.OldStatusProcessor;
import ru.mos.polls.survey.status.PassedContinuesStatusProcessor;
import ru.mos.polls.survey.status.PassedEndedStatusProcessor;
import ru.mos.polls.survey.summary.ExpertsView;
import ru.mos.polls.survey.summary.SurveyHeader;
import ru.mos.polls.survey.summary.SurveyTitleView;
import ru.mos.polls.survey.variants.SurveyVariant;
import ru.mos.polls.survey.variants.select.IntentExtraProcessor;

/**
 * Весь опрос. Включает в себя вопросы.
 */
public class Survey implements Serializable {

    /**
     * Айдишник опроса
     */
    private final long id;

    /**
     * Индекс текущей страницы в questionsOrder
     */
    private int currentPageIndex = -1;

    /**
     * Айдишник вопроса - вопрос
     */
    private final Map<Long, SurveyQuestion> questions = new HashMap<Long, SurveyQuestion>();

    /**
     * Порядок следования вопросов. Для поддержки допущения что айдишники могут как угодно идти.
     */
    private final List<Long> questionsOrder = new ArrayList<Long>();

    /**
     * Ключ - айдишник фильтра
     */
    private Map<Long, Filter> filters = new HashMap<Long, Filter>();

    /**
     * Стасут опроса - активный, завершгённый и тп.
     */
    private Status status;

    /**
     * Мнение экспертов. Может быть null
     */


    /**
     * Информация об опросе содержит ссылку для перехода н страницу с информацией и заголовок браузера
     */
    private Information information = new Information();

    /**
     * Кастомное сообщение для опроса, если этот объект не пустой,
     * то выводим после отправки опроса диалог с данными из этого объекта
     */
    private Message message;

    private List<DetailsExpert> detailsExperts;

    private Listener listener = Listener.STUB;
    private String title;
    private String textFullHtml;
    private String textShortHtml;
    /**
     * с версии 1.8,  отметка специального голосования
     * перешла сюда
     */
    private Kind kind;
    private int points;

    /**
     * с версии 1.8
     */
    private long beginDate;
    private long endDate;
    /**
     * c версии 1.9
     */
    private long passedDate;
    /**
     * с версии 1.8
     * этот параметр в случае kind=hearing/hearing_preview должен указывать на то что пользователь
     * дал согласие на учатие/посещение. Когда голосование перерождается из hearing_preview в hearing,
     * его значение сбрасывается в false. Для других kind параметр может быть равен null или вовсе отсутствовать.
     */
    private boolean isHearingChecked;
    private int votersCount = 0;
    /**
     * Показывать ли проценты промежуточных результатов
     *
     * @since 1.9.2
     */
    private boolean showPollStats = false;

    /**
     * Список экспозиций публичного слушания
     *
     * @since 2.0
     */
    private List<Exposition> expositions;
    /**
     * Список собраний публичного слушания
     *
     * @since 2.0
     */
    private List<Meeting> meetings;
    /**
     * Строка, тип публичного слушания (из справочника, содержащего типы ПС: ГПЗУ,
     * земельного участка Проект межевания, Генеральный план и т.д.)
     *
     * @since 2.0
     */
    private String hearingType;

    public Survey(long id, Status status, List<SurveyQuestion> l) {
        this.id = id;
        this.status = status;
        for (SurveyQuestion question : l) {
            long questionId = question.getId();
            questionsOrder.add(questionId);
            questions.put(questionId, question);
        }
    }

    public void setListener(final Listener listener) {
        if (listener == null) {
            this.listener = Listener.STUB;
        } else {
            this.listener = listener;
        }
        for (final SurveyQuestion question : questions.values()) {
            question.setListener(new SurveyQuestion.Listener() {

                @Override
                public void onBeforeClick(SurveyVariant surveyVariant) {
                    listener.onSurveyVariantBeforeClick(question, surveyVariant);
                }

                @Override
                public void onAfterClick(SurveyVariant surveyVariant) {
                    listener.onSurveyVariantAfterClick(question, surveyVariant);
                }

                @Override
                public void onCommmit(SurveyVariant surveyVariant) {
                    listener.onSurveyVariantOnCommit(question, surveyVariant);
                }

                @Override
                public void onCancel(SurveyVariant surveyVariant) {
                    listener.onSurveyVariantOnCancel(question, surveyVariant);
                }

                @Override
                public void refreshSurvey() {
                    listener.onRefreshSurvey();
                }

            });
        }
    }

    /**
     * Возвращает айдишник опроса
     *
     * @return
     */
    public long getId() {
        return id;
    }

    public Map<Long, Filter> getFilters() {
        return filters;
    }

    public void setCurrentPageIndex(int page) {
        if (currentPageIndex != -1) {
            endTiming();
        }
        currentPageIndex = page;
        startTiming();
    }

    /**
     * Возвращает все вопросы в нужном порядке.
     *
     * @return
     */
    public List<SurveyQuestion> getQuestionsList() {
        List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
        for (Long questionId : questionsOrder) {
            SurveyQuestion question = questions.get(questionId);
            result.add(question);
        }
        return result;
    }

    /**
     * Возвращает опросы в соответствии с фильтрами.
     *
     * @return
     */
    public List<SurveyQuestion> getFilteredQuestionList() {
        List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
        for (Long questionId : questionsOrder) {
            SurveyQuestion question = questions.get(questionId);
            Filter filter = question.getFilter(this);
            if (filter.isSuitable(this)) {
                result.add(question);
            } else {
                question.reset();
            }
        }
        return result;
    }

    public int doPrev() {
        int result = questionsOrder.indexOf(getCurrentQuestionId());
        int position = result;
        for (int i = position - 1; i >= 0; --i) {
            SurveyQuestion question = questions.get(questionsOrder.get(i));
            Filter filter = question.getFilter(this);
            if (filter.isSuitable(this)) {
                result = i;
                break;
            }
        }
        return result;

    }

    public String getFormattedTitle(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        String date;
        String result = null;
        if (isActive() || isInterrupted()) {
            if (points > 0) {
                result = PointsManager.getSuitableString(context, R.array.survey_points_pluse, points);
                result = String.format(result, points);
            } else {
                return "";
            }
        } else if (isPassed()) {
            date = sdf.format(passedDate);
            if (points > 0) {
                result = PointsManager.getSuitableString(context, R.array.poll_passed_points, points);
                result = String.format(result, points);
                result += ", " + date;
            } else {
                result = String.format(context.getString(R.string.title_passed_polls_with_zero_points), date);
            }
        } else if (isOld()) {
            date = sdf.format(endDate);
            result = String.format(context.getString(R.string.title_old_polls), date);
        }

        return result;
    }

    public int getColorForTitle() {
        int result = R.color.greenText;
        if (isPassed()) {
            result = R.color.ag_red;
        } else if (isOld()) {
            result = R.color.greyHint;
        }

        return result;
    }

    public View getView(final Activity context, Fragment fragment, final long questionId) {
        if (context == null) {
            throw new NullPointerException("context");
        }
        final StatusProcessor statusProcessor;
        if (isPassed()) {
            if (isEnded()) {
                statusProcessor = new PassedEndedStatusProcessor(isShowPollStats());
            } else {
                statusProcessor = new PassedContinuesStatusProcessor(isShowPollStats());
            }
        } else if (isOld()) {
            statusProcessor = new OldStatusProcessor();
        } else {
            statusProcessor = new ActiveStatusProcessor();
        }

        final SurveyQuestion question = questions.get(questionId);
        View result = View.inflate(context, R.layout.survey_question, null);

        final View questionHeaderView = View.inflate(context, R.layout.survey_question_header, null);
        displayExperts((BaseActivity) context, questionHeaderView, question);
        displayVotesInfo(questionHeaderView, question);
        displayTitleAnswers(context, questionHeaderView);

        SurveyQuestion.ViewFactory viewFactory = new SurveyQuestion.ViewFactory() {

            @Override
            public View getHeaderView(Context context) {
                return questionHeaderView;
            }

            @Override
            public View getFooterView(Context context) {
                return null;
            }

        };

        final ViewGroup questionsViewGroup = (ViewGroup) result.findViewById(R.id.questionsContainer);

        final View questionView = question.getView(context, fragment, statusProcessor, viewFactory);
        displayHeader(questionView, questionHeaderView, question);
        questionsViewGroup.addView(questionView);
        statusProcessor.process(questionsViewGroup);
        statusProcessor.process(result);
        return result;
    }

    private void displayTitleAnswers(Context context, View questionHeaderView) {
        TextView titleAnswers = (TextView) questionHeaderView.findViewById(R.id.titleAnswers);
        switch (status) {
            case OLD:
                titleAnswers.setText(context.getString(R.string.title_answer));
                break;
            case PASSED:
                titleAnswers.setText(isShowPollStats() ?
                        context.getString(R.string.title_answers_result) : context.getString(R.string.title_answer_passed));
                break;
        }
    }

    private void displayExperts(final BaseActivity activity, View questionHeaderView, final SurveyQuestion surveyQuestion) {
        ExpertsView expertsView = (ExpertsView) questionHeaderView.findViewById(R.id.experts);
        expertsView.display(surveyQuestion);
        expertsView.setCallback(new ExpertsView.Callback() {
            @Override
            public void onChooseExpert(DetailsExpert detailsExpert) {
                Statistics.pollsEnterExperts(id, surveyQuestion.getId());
                GoogleStatistics.Survey.pollsEnterExperts(id, surveyQuestion.getId());
                DetailsExpertsActivity.startActivityByQuestionId(activity,
                        detailsExpert,
                        surveyQuestion.getId(),
                        getKind().isHearing());
            }
        });
    }

    private void displayVotesInfo(View questionHeaderView, SurveyQuestion surveyQuestion) {
        VotersView votersView = (VotersView) questionHeaderView.findViewById(R.id.votesInfo);
        votersView.display(this, surveyQuestion);
    }

    private void displayHeader(View questionView, View questionHeaderView, final SurveyQuestion question) {
        TextView infoTextView = (TextView) questionHeaderView.findViewById(R.id.info);
        infoTextView.setText(question.getHint());
        if (TextUtils.isEmpty(question.getHint())) {
            infoTextView.setVisibility(View.GONE);
        }
        final SurveyHeader header = (SurveyHeader) questionHeaderView.findViewById(R.id.header);
        header.display(this, question);
        header.setStateListener(new SurveyTitleView.StateListener() {
            @Override
            public void onExpand() {
                if (question instanceof ListViewSurveyQuestion) {
                    header.scrollToBegin(((ListViewSurveyQuestion) question).getListView());
                }
            }

            @Override
            public void onCollapse() {
                if (question instanceof ListViewSurveyQuestion) {
                    header.scrollToEnd(((ListViewSurveyQuestion) question).getListView());
                }
            }
        });
    }

    public Status getStatus() {
        return status;
    }

    public boolean isActive() {
        return hasStatus(Status.ACTIVE);
    }

    public boolean isPassed() {
        return hasStatus(Status.PASSED);
    }

    public boolean isInterrupted() {
        return hasStatus(Status.INTERRUPTED);
    }

    public boolean isOld() {
        return hasStatus(Status.OLD);
    }

    public boolean isEnded() {
        long current = System.currentTimeMillis();
        boolean b = current > endDate;
        return b;
    }

    private boolean hasStatus(Status status) {
        return this.status == status;
    }

    public long getCurrentQuestionId() {
        long result = 0;
        try {
            result = questionsOrder.get(currentPageIndex);
        } catch (Exception ignored) {
        }
        return result;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public int getTotalPages() {
        return questions.size();
    }

    public boolean hasAnswers() {
        boolean result = false;
        List<SurveyQuestion> questionList = getFilteredQuestionList();
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

    public SurveyQuestion getFirstNotCheckedQuestion() {
        SurveyQuestion result = getFilteredQuestionList().get(0);
        List<SurveyQuestion> questionList = getFilteredQuestionList();
        for (SurveyQuestion question : questionList) {
            try {
                question.verify();
            } catch (Exception ignored) {
                result = question;
                break;
            }
        }
        return result;
    }

    /**
     * Проверяет, есть ли следующий опрос
     *
     * @return true если есть ещё страницы
     * false если страниц больше нет
     */
    public boolean checkNext() {
        return doNext(false);
    }

    /**
     * Пытается переключиться к следующий странице опроса.
     *
     * @return true если есть ещё страницы
     * false если страниц больше нет
     */
    public boolean processNext() {
        return doNext(true);
    }

    /**
     * @param changePage Изменить ли текущую страницу
     * @return
     */
    private boolean doNext(boolean changePage) {
        final boolean result;
        int total = questionsOrder.size();
        if (total - 1 == currentPageIndex || currentPageIndex == -1) {
            result = false;
        } else {
            long currentQuestionId = questionsOrder.get(0);
            try {
                currentQuestionId = questionsOrder.get(currentPageIndex);
            } catch (IndexOutOfBoundsException ignored) {
            }
            SurveyQuestion currentQuestion = getQuestion(currentQuestionId);
            boolean hasSuitable = false;
            for (int i = currentPageIndex + 1; i < total; i++) { //перебираем все вопросы начиная со следующего
                long questionId = questionsOrder.get(i);
                final Filter filter = getQuestion(questionId).getFilter(this);
                boolean suitable = filter.isSuitable(this);
                if (suitable) {
                    if (changePage) {
                        setCurrentPageIndex(i);
                    }
                    hasSuitable = true;
                    break;
                }
            }
            result = hasSuitable;
        }
        return result;
    }

    /**
     * Проверяет корректность ответа на вопрос:
     * 1) заполнено ли необходимое количество элемнетов
     * 2) правильное ли значение в каждом из кастомных элементов
     */
    public void verify() throws VerificationException {
        SurveyQuestion q = questions.get(getCurrentQuestionId());
        if (q != null) {
            q.verify();
        } else {
            throw new VerificationException("Не найден вопрос в голосовании");
        }
    }

    /**
     * Возвращает массив ответов.
     *
     * @return
     */
    public JSONArray getAnswersJson() {
        JSONArray result = new JSONArray();
        for (long questionId : questionsOrder) {
            SurveyQuestion surveyQuestion = questions.get(questionId);
            JSONObject answerJsonObject = surveyQuestion.getAnswerJson();
            result.put(answerJsonObject);
        }
        return result;
    }

    /**
     * Возвращает массив ответов, подходящих под фильтр.
     * (то есть не обязательно все)
     *
     * @return
     */
    public JSONArray getPrepareAnswersJson() {
        JSONArray result = new JSONArray();
        for (SurveyQuestion surveyQuestion : getFilteredQuestionList()) {
            JSONObject answerJsonObject = surveyQuestion.getPrepareAnswerJson();
            result.put(answerJsonObject);
        }
        return result;
    }

    /**
     * Загружает ответы из джисона, который сохранен локально
     * с помощью SharedPreferenceSurveyManager
     *
     * @param jsonArray
     */
    public void loadAnswersJsonLocal(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            long id = jsonObject.optLong("id"); //айдишник вопроса в опросе
            JSONArray answerJsonArray = jsonObject.optJSONArray("answer");
            SurveyQuestion surveyQuestion = questions.get(id);
            surveyQuestion.loadAllAnswerJson(answerJsonArray);
            try {
                surveyQuestion.verify();
                surveyQuestion.setPassed(true);
            } catch (VerificationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Загружает ответы из джисона, полученного с сервера
     *
     * @param jsonArray
     */
    public void loadAnswersJsonServer(JSONArray jsonArray) {
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                long id = jsonObject.optLong("id"); //айдишник вопроса в опросе
                JSONArray answerJsonArray = jsonObject.optJSONArray("answer");
                SurveyQuestion surveyQuestion = questions.get(id);
                surveyQuestion.loadAnswerJson(answerJsonArray);
                try {
                    surveyQuestion.verify();
                    surveyQuestion.setPassed(true);
                } catch (VerificationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void filterAdd(Filter filter) {
        if (filter != null) {
            filters.put(filter.getId(), filter);
        }
    }

    public SurveyQuestion getQuestion(long questionId) {
        SurveyQuestion result = null;
        if (questions.containsKey(questionId)) {
            result = questions.get(questionId);
        }
        return result;
    }

    public List<Long> getQuestionsOrder() {
        return questionsOrder;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO частично вынести наверх, во фрагмент. Заюзать requestCode - он будет один для всех опросов и их внутренностей.
        final boolean result;
        if (data == null) {
            result = false;
        } else {
            IntentExtraProcessor intentExtraProcessor = new IntentExtraProcessor();
            result = intentExtraProcessor.processResult(id, questions, requestCode, resultCode, data);
        }
        return result;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTextFullHtml(String textFullHtml) {
        this.textFullHtml = textFullHtml;
    }

    public void setTextShortHtml(String textShortHtml) {
        this.textShortHtml = textShortHtml;
    }

    public String getTitle() {
        return title;
    }

    public String getTextFullHtml() {
        return textFullHtml;
    }

    public String getTextShortHtml() {
        return textShortHtml;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public boolean isFinished() {
        List<SurveyQuestion> questionList = getFilteredQuestionList();
        boolean result = questionList.get(questionList.size() - 1).isPassed();
        return result;
    }

    public void setVotersCount(int votersCount) {
        this.votersCount = votersCount;
    }

    public void setShowPollStats(boolean showPollStats) {
        this.showPollStats = showPollStats;
    }

    public boolean isShowPollStats() {
        return showPollStats;
    }

    public int getVotersCount() {
        return votersCount;
    }

    public List<Exposition> getExpositions() {
        return expositions;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public String getHearingType() {
        return hearingType;
    }

    public void setExpositions(List<Exposition> expositions) {
        this.expositions = expositions;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    public void setHearingType(String hearingType) {
        this.hearingType = hearingType;
    }

    public enum Status {
        PASSED,
        ACTIVE,
        INTERRUPTED,
        OLD;

        public static Status parse(String status) {
            final Status result;
            if ("active".equals(status)) {
                result = ACTIVE;
            } else if ("passed".equals(status)) {
                result = PASSED;
            } else if ("interrupted".equals(status)) {
                result = INTERRUPTED;
            } else if ("old".equals(status)) {
                result = OLD;
            } else {
                result = null; //по падению будем отслеживать новые статусы
            }
            return result;
        }

    }

    public void setInformation(Information information) {
        this.information = information;
    }

    public Information getInformation() {
        return information;
    }


    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }


    public void setDetailsExperts(List<DetailsExpert> detailsExperts) {
        this.detailsExperts = detailsExperts;
    }


    public List<DetailsExpert> getDetailsExperts() {
        return detailsExperts;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setPassedDate(long passedDate) {
        this.passedDate = passedDate;
    }

    public long getPassedDate() {
        return passedDate;
    }

    public boolean isHearingChecked() {
        return isHearingChecked;
    }

    public void setHearingChecked(boolean isHearingChecked) {
        this.isHearingChecked = isHearingChecked;
    }

    /**
     * Устанавливает время начала для текущего вопроса
     */
    public void startTiming() {
        long id = getCurrentQuestionId();
        SurveyQuestion question = questions.get(id);
        if (question != null) {
            question.setStartTime(System.currentTimeMillis());
        }
    }

    /**
     * Устанавливает время окончания для текущего опроса
     */
    public void endTiming() {
        long id = getCurrentQuestionId();
        SurveyQuestion question = questions.get(id);
        if (question != null) {
            question.setEndTime(System.currentTimeMillis());
        }
    }

    public interface Listener {

        public static final Listener STUB = new Listener() {

            @Override
            public void onSurveyVariantBeforeClick(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant) {
            }

            @Override
            public void onSurveyVariantAfterClick(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant) {
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

        };

        /**
         * Вызывается перед нажатием на вариант ответа
         *
         * @param surveyQuestion
         * @param surveyVariant
         */
        void onSurveyVariantBeforeClick(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant);

        /**
         * Вызывается после нажатия на вариант ответа
         *
         * @param surveyQuestion
         * @param surveyVariant
         */
        void onSurveyVariantAfterClick(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant);

        /**
         * Вызывается если для ввода значения используется диалог и значение было измененно
         *
         * @param surveyQuestion
         * @param surveyVariant
         */
        void onSurveyVariantOnCommit(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant);

        /**
         * Вызывается если для ввода значения используется диалог и изменение занчения было отмененно
         *
         * @param surveyQuestion
         * @param surveyVariant
         */
        void onSurveyVariantOnCancel(SurveyQuestion surveyQuestion, SurveyVariant surveyVariant);

        /**
         * Обновляет опрос.
         */
        void onRefreshSurvey();

    }

}
