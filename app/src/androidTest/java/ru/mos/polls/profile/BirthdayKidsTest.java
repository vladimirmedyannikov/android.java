package ru.mos.polls.profile;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.BirthdayKids;

/**
 * Created by Trunks on 09.11.2017.
 */

public class BirthdayKidsTest extends BaseUnitTest {

    @Test
    public void getttersTest() {
        BirthdayKids bk = new BirthdayKids(234,"hints", "");
        Assert.assertNotNull(bk.getBirtdayHints());
        Assert.assertNotNull(bk.getBirthDayTitle());
        Assert.assertNotNull(bk.getBirthdayYear());
    }
}
