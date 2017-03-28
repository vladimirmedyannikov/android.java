package ru.mos.polls.support;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.support.model.Subject;

/**
 * on 28.03.17 7:35.
 */

public class SubjectUnitTest extends BaseUnitTest {

    @Test
    public void parse() {
        Subject testValue = getSubjectTestValue();
        Assert.assertNotNull(testValue);
    }

    @Test
    public void parseList() {
        List<Subject> testValue = getSubjectListTestValue();
        Assert.assertNotNull(testValue);
        /**
         * 1 - {@link Subject#SUBJECT_NOT_SELECTED}
         * 5 - from stub
         */
        Assert.assertEquals(1 + 5 , testValue.size());
        Assert.assertEquals(Subject.SUBJECT_NOT_SELECTED.getId(), testValue.get(0).getId());
    }

    @Test
    public void hasId() {
        Subject testValue = getSubjectTestValue();
        Assert.assertEquals(1, testValue.getId());
    }

    @Test
    public void hasTitle() {
        Subject testValue = getSubjectTestValue();
        Assert.assertEquals("Отзыа о работе", testValue.getTitle());
    }

    @Test
    public void isEmpty() {
        Subject testValue = getSubjectTestValue();
        Assert.assertEquals(false, testValue.isEmpty());
    }

    @Test
    public void isSubjectNotSelected() {
        Assert.assertEquals(true, Subject.SUBJECT_NOT_SELECTED.isEmpty());
        Assert.assertEquals(Subject.ID_SUBJECT_NOT_SELECTED, Subject.SUBJECT_NOT_SELECTED.getId());
        Assert.assertEquals(Subject.TITLE_SUBJECT_NOT_SELECTED, Subject.SUBJECT_NOT_SELECTED.getTitle());
    }

    private Subject getSubjectTestValue() {
        return new Subject(fromTestRawAsJson("feedback_subject.json"));
    }

    private List<Subject> getSubjectListTestValue() {
        return Subject.fromJson(fromTestRawAsJsonArray("feedback_subjects.json"));
    }

}
