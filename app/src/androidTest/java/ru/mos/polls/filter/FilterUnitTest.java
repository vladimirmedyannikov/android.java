package ru.mos.polls.filter;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.filter.Answer;
import ru.mos.polls.survey.filter.EqualsFilter;
import ru.mos.polls.survey.filter.LessTimeFilter;
import ru.mos.polls.survey.filter.MoreTimeFilter;
import ru.mos.polls.survey.filter.NotEqualsFilter;
import ru.mos.polls.survey.filter.conditions.AndCondition;
import ru.mos.polls.survey.filter.conditions.OrCondition;
import ru.mos.polls.survey.parsers.SurveyQuestionFactory;
import ru.mos.polls.survey.questions.SimpleSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;

public class FilterUnitTest extends BaseUnitTest {


    @Test
    public void equalsFilterTest() {
        EqualsFilter testObj = new EqualsFilter(0, new Answer[0], new AndCondition());
        String[] testArr = {"test1", "test2"};
        Assert.assertEquals(false, testObj.onContains("test3", testArr));
        Assert.assertEquals(true, testObj.onContains("test1", testArr));
    }

    @Test
    public void notEqualsFilterTest() {
        NotEqualsFilter testObj = new NotEqualsFilter(0, new Answer[0], new AndCondition());
        String test1 = "test1";
        String[] testArr1 = {test1};
        String[] testArr2 = {test1, test1};
        Assert.assertEquals(true, testObj.onContains(test1, testArr2));
        Assert.assertEquals(false, testObj.onContains(test1, testArr1));
    }

    @Test
    public void andConditionTest() {
        AndCondition testObj = new AndCondition();
        List<Boolean> testList = new ArrayList<>();
        testObj.values = testList;
        testObj.add(true);
        testObj.add(true);
        Assert.assertEquals(true, testObj.get());
        testObj.add(false);
        Assert.assertEquals(false, testObj.get());
        Assert.assertEquals("AndCondition", testObj.toString());
        testObj.reset();
        Assert.assertEquals(testObj.values.size(), 0);
    }

    @Test
    public void orConditionTest() {
        OrCondition testObj = new OrCondition();
        List<Boolean> testList = new ArrayList<>();
        testObj.values = testList;
        testObj.add(false);
        testObj.add(false);
        Assert.assertEquals(false, testObj.get());

        testObj.add(true);
        Assert.assertEquals(true, testObj.get());

        Assert.assertEquals("OrCondition", testObj.toString());
    }

    @Test
    public void answersFilterTest() {
        List<SurveyQuestion> testListSQ = new ArrayList<>();
        long surveyId = 1;
        SimpleSurveyQuestion sq = (SimpleSurveyQuestion) SurveyQuestionFactory.fromJson(fromTestRawAsJson("surveyquestion_simple.json"), surveyId);
        Assert.assertEquals(sq.getId(), 1);
        testListSQ.add(sq);

    }

    @Test
    public void lessTimeFilterCompareTest() {
        LessTimeFilter testObj = new LessTimeFilter(0, 0, 0);
        Assert.assertEquals(true, testObj.compare(1, 2));
    }

    @Test
    public void moreTimeFilterCompareTest() {
        MoreTimeFilter testObj = new MoreTimeFilter(0, 0, 0);
        Assert.assertEquals(true, testObj.compare(2, 1));
    }
}
