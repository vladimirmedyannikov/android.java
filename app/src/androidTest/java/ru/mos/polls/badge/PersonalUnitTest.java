package ru.mos.polls.badge;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.badge.model.Personal;

/**
 *
 */

public class PersonalUnitTest extends BaseUnitTest {
    @Test
    public void parse() {
        Personal testValue = new Personal(fromTestRawAsJson("badge_personal.json"));
        Assert.assertNotNull(testValue);

        assertNotNullOrEmpty(testValue.getFirstName());
        Assert.assertEquals("Тест", testValue.getFirstName());

        assertNotNullOrEmpty(testValue.getSurname());
        Assert.assertEquals("Тестов", testValue.getSurname());

        assertNotNullOrEmpty(testValue.getMiddleName());
        Assert.assertEquals("Тестович", testValue.getMiddleName());

        assertNotNullOrEmpty(testValue.getPhone());
        Assert.assertEquals("+79171234567", testValue.getPhone());

        assertNotNullOrEmpty(testValue.getIcon());
        boolean isURL = testValue.getIcon().startsWith("http://") || testValue.getIcon().startsWith("https://");
        Assert.assertEquals(true, isURL);
        Assert.assertEquals("https://i504.mycdn.me/res/stub_50x50.gif", testValue.getIcon());
    }
}
