package ru.mos.polls.survey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.filter.EmptyFilter;
import ru.mos.polls.survey.filter.Filter;
import ru.mos.polls.survey.filter.MoreTimeFilter;
import ru.mos.polls.survey.parsers.SurveyQuestionFactory;
import ru.mos.polls.survey.questions.SimpleSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Created by Trunks on 04.05.2017.
 */

public class SurveyQuestionUnitTest extends BaseUnitTest {


    @Test
    public void resetAnswers() {
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), 0);
        List<SurveyVariant> variantsTestList = sq.getVariantsList();
        for (SurveyVariant surveyVariant : variantsTestList) {
            surveyVariant.setChecked(true);
        }
        for (SurveyVariant surveyVariant : variantsTestList) {
            if (surveyVariant.getBackId().equalsIgnoreCase(sq.getCheckedBackIds()[0]) && !surveyVariant.isChecked())
                throw new RuntimeException();
        }

        sq.reset();

        for (SurveyVariant surveyVariant : variantsTestList) {
            if (surveyVariant.getBackId().equalsIgnoreCase(sq.getCheckedBackIds()[0]) && surveyVariant.isChecked())
                throw new RuntimeException();
        }
    }

    @Test
    public void getFilterEmptyTest() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        long surveyId = 1;
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        Survey testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);
        Filter testFilter = sq.getFilter(testSurvey);
        if (!(testFilter instanceof EmptyFilter)) throw new RuntimeException();
    }

    @Test
    public void getFilterTest() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        long surveyId = 1;
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        testListSQ.add(sq);
        Survey testSurvey = new Survey(surveyId, Survey.Status.ACTIVE, testListSQ);
        testSurvey.filterAdd(new MoreTimeFilter(1, 0, 0));
        Filter testFilter = sq.getFilter(testSurvey);
        Assert.assertEquals(testFilter.getId(), 1);
    }


}
