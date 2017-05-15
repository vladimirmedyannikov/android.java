package ru.mos.polls.survey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.filter.Answer;
import ru.mos.polls.survey.filter.EmptyFilter;
import ru.mos.polls.survey.filter.EqualsFilter;
import ru.mos.polls.survey.filter.LessTimeFilter;
import ru.mos.polls.survey.filter.NotEqualsFilter;
import ru.mos.polls.survey.filter.conditions.AndCondition;
import ru.mos.polls.survey.filter.conditions.OrCondition;
import ru.mos.polls.survey.parsers.SurveyQuestionFactory;
import ru.mos.polls.survey.questions.CheckboxSurveyQuestion;
import ru.mos.polls.survey.questions.SimpleSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;

/**
 * Created by Trunks on 11.05.2017.
 */

public class SurveyUnitTest extends BaseUnitTest {
    Survey testSurvey;
    long surveyId = 1;

    @Before
    public void init() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        testListSQ.add(sq);
        testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);
    }

    @Test
    public void getSurveyQuestionTest() {
        SurveyQuestion surveyQuestion = testSurvey.getQuestion(2);
        Assert.assertEquals(surveyQuestion.getId(), 2);
    }

    @Test
    public void getCurrentQuestionIdTest() {
        testSurvey.setCurrentPageIndex(0);
        Assert.assertEquals(testSurvey.getCurrentPageIndex(), 0);
        Assert.assertEquals(testSurvey.getCurrentQuestionId(), 2);
    }

    @Test
    public void pageIndexTest() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        CheckboxSurveyQuestion csq = (CheckboxSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_checkbox.json"), surveyId);

        testListSQ.add(sq);
        testListSQ.add(csq);
        testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);
        testSurvey.setCurrentPageIndex(0);
        Assert.assertEquals(testSurvey.getCurrentPageIndex(), 0);
        testSurvey.doNext(true);
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
        SurveyQuestion surveyQuestion = testSurvey.getQuestion(2);
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
        testSurvey.filterAdd(new LessTimeFilter(1, 0, 2));
        Assert.assertEquals(testSurvey.getFilteredQuestionList().size(), 0);
    }


}
