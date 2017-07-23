package ru.mos.polls.survey;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.R;
import ru.mos.polls.survey.filter.LessTimeFilter;
import ru.mos.polls.survey.parsers.SurveyQuestionFactory;
import ru.mos.polls.survey.questions.CheckboxSurveyQuestion;
import ru.mos.polls.survey.questions.RadioboxSurveyQuestion;
import ru.mos.polls.survey.questions.SimpleSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;

/**
 * Created by Trunks on 11.05.2017.
 */

public class SurveyUnitTest extends BaseUnitTest {
    Survey testSurvey;
    long surveyId = 1;
    final int SIMPLE_QUEST_ID = 1;
    Context appContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void init() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        testListSQ.add(sq);
        testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);
    }

    @Test
    public void getSurveyQuestionTest() {
        SurveyQuestion surveyQuestion = testSurvey.getQuestion(SIMPLE_QUEST_ID);
        Assert.assertEquals(surveyQuestion.getId(), SIMPLE_QUEST_ID);
    }

    @Test
    public void getCurrentQuestionIdTest() {
        testSurvey.setCurrentPageIndex(0);
        Assert.assertEquals(testSurvey.getCurrentPageIndex(), 0);
        Assert.assertEquals(testSurvey.getCurrentQuestionId(), SIMPLE_QUEST_ID);
    }

    @Test
    public void pageIndexTest() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        RadioboxSurveyQuestion rsq = (RadioboxSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_radiobutton.json"), surveyId);
        CheckboxSurveyQuestion csq = (CheckboxSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_checkbox.json"), surveyId);
        testListSQ.add(sq);
        testListSQ.add(rsq);
        testListSQ.add(csq);
        testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);
        testSurvey.setCurrentPageIndex(0);
        Assert.assertEquals(testSurvey.getCurrentPageIndex(), 0);

        testSurvey.doNext(true);
        testSurvey.doNext(true);
        Assert.assertEquals(testSurvey.getCurrentPageIndex(), 2);

        testSurvey.setCurrentPageIndex(testSurvey.doPrev());
        Assert.assertEquals(testSurvey.getCurrentPageIndex(), 1);
    }

    @Test
    public void getQuestionListTest() {
        Assert.assertEquals(testSurvey.getQuestionsList().size(), 1);
    }

    @Test
    public void isEndedTest() {
        Assert.assertEquals(true, testSurvey.isEnded());
        testSurvey.setEndDate(System.currentTimeMillis() + 1000000);
        Assert.assertEquals(false, testSurvey.isEnded());
    }

    @Test
    public void startTimingEndTimingTest() {
        SurveyQuestion surveyQuestion = testSurvey.getQuestion(SIMPLE_QUEST_ID);
        Assert.assertEquals(surveyQuestion.getStartTime(), 0);
        Assert.assertEquals(surveyQuestion.getEndTime(), 0);

        testSurvey.setCurrentPageIndex(0);
        Assert.assertNotEquals(surveyQuestion.getStartTime(), 0);
        testSurvey.endTiming();
        Assert.assertNotEquals(surveyQuestion.getEndTime(), 0);
    }

    @Test
    public void getFilteredQuestionListTest() {
        List<SurveyQuestion> list = testSurvey.getFilteredQuestionList();
        Assert.assertEquals(list.size(), 1);
        testSurvey.filterAdd(new LessTimeFilter(1, 0, SIMPLE_QUEST_ID));
        Assert.assertEquals(testSurvey.getFilteredQuestionList().size(), 0);
    }


    @Test
    public void getFirstNotCheckedQuestionTest() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        RadioboxSurveyQuestion rsq = (RadioboxSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_radiobutton.json"), surveyId);
        CheckboxSurveyQuestion csq = (CheckboxSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_checkbox.json"), surveyId);
        testListSQ.add(rsq);
        testListSQ.add(csq);
        testListSQ.add(sq);
        testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);

        SurveyQuestion question = testSurvey.getFirstNotCheckedQuestion();
        Assert.assertEquals(question, rsq);

        question = testSurvey.getFirstNotCheckedQuestion();

        question.getVariantsList().get(0).setChecked(true);

        question = testSurvey.getFirstNotCheckedQuestion();
        Assert.assertEquals(question, csq);
    }

    @Test
    public void getColorForTitleTest() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        testListSQ.add(sq);
        testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);
        Assert.assertEquals(testSurvey.getColorForTitle(), R.color.greenText);

        testSurvey = new Survey(surveyId, Survey.Status.PASSED, testListSQ);
        Assert.assertEquals(testSurvey.getColorForTitle(), R.color.ag_red);

        testSurvey = new Survey(surveyId, Survey.Status.OLD, testListSQ);
        Assert.assertEquals(testSurvey.getColorForTitle(), R.color.greyHint);
    }

    @Test
    public void getStatusTest() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        testListSQ.add(sq);
        testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);
        Assert.assertEquals(testSurvey.isActive(), true);

        testSurvey = new Survey(surveyId, Survey.Status.OLD, testListSQ);
        Assert.assertEquals(testSurvey.isOld(), true);

        testSurvey = new Survey(surveyId, Survey.Status.PASSED, testListSQ);
        Assert.assertEquals(testSurvey.isPassed(), true);

        testSurvey = new Survey(surveyId, Survey.Status.INTERRUPTED, testListSQ);
        Assert.assertEquals(testSurvey.isInterrupted(), true);
    }
}
