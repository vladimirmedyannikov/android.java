package ru.mos.polls.survey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
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
}
