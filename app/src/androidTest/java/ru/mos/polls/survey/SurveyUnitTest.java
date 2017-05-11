package ru.mos.polls.survey;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.parsers.SurveyQuestionFactory;
import ru.mos.polls.survey.questions.SimpleSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;

/**
 * Created by Trunks on 11.05.2017.
 */

public class SurveyUnitTest extends BaseUnitTest {

    @Test
    public void getSurveyQuestionTest() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        long surveyId = 1;
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        testListSQ.add(sq);
        Survey testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);

        SurveyQuestion surveyQuestion = testSurvey.getQuestion(2);
        Assert.assertEquals(surveyQuestion.getId(), 2);

    }
}
