package ru.mos.polls.support;

import junit.framework.Assert;

import org.junit.Test;

import ru.mos.polls.support.model.FeedbackBody;

/**
 * on 28.03.17 7:55.
 */

public class FeedbackBodyUnitTest {
    private static final String EMAIL = "email@example.com";
    private static final long ORDER_NUMBER = 10001;
    private static final String MESSAGE = "user message for feedback";

    @Test
    public void hasEmail() {
        FeedbackBody testValue = getTestValue();
        Assert.assertEquals(EMAIL, testValue.getEmail());
    }

    @Test
    public void hasMessage() {
        FeedbackBody testValue = getTestValue();
        Assert.assertEquals(MESSAGE, testValue.getMessage());
    }

    @Test
    public void hasOrderNumber() {
        FeedbackBody testValue = getTestValue();
        Assert.assertEquals(ORDER_NUMBER, testValue.getOrderNumber());
    }

    @Test
    public void setEmail() {
        FeedbackBody testValue = getTestValue();
        testValue.setEmail("");
        Assert.assertEquals("", testValue.getEmail());
    }

    @Test
    public void setMessage() {
        FeedbackBody testValue = getTestValue();
        testValue.setMessage("");
        Assert.assertEquals("", testValue.getMessage());
    }

    @Test
    public void setOrderNumber() {
        FeedbackBody testValue = getTestValue();
        testValue.setOrderNumber(0L);
        Assert.assertEquals(0L, testValue.getOrderNumber());
    }

    private FeedbackBody getTestValue() {
        FeedbackBody testValue = new FeedbackBody();
        testValue.setEmail(EMAIL);
        testValue.setMessage(MESSAGE);
        testValue.setOrderNumber(ORDER_NUMBER);
        return testValue;
    }
}
