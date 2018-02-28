package ru.mos.polls.profile;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.BirthdayKids;

public class BirthdayKidsTest extends BaseUnitTest {

    @Test
    public void gettersTest() {
        BirthdayKids bk = new BirthdayKids(234, "hints", "");
        Assert.assertNotNull(bk.getBirtdayHints());
        Assert.assertNotNull(bk.getBirthDayTitle());
        Assert.assertNotNull(bk.getBirthdayYear());
    }

    @Test
    public void setterTest() {
        BirthdayKids bk = new BirthdayKids(234, "hints", "");
        long testValue = 1000000L;
        Assert.assertNotEquals(bk.getBirthdayYear(), testValue);
        bk.setBirthdayYear(testValue);
        Assert.assertEquals(bk.getBirthdayYear(), testValue);
    }
}
