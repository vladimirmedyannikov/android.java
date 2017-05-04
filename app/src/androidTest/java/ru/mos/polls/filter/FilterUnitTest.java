package ru.mos.polls.filter;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.survey.filter.Answer;
import ru.mos.polls.survey.filter.EqualsFilter;
import ru.mos.polls.survey.filter.NotEqualsFilter;
import ru.mos.polls.survey.filter.conditions.AndCondition;

/**
 * Created by Trunks on 03.05.2017.
 */

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
}
