package ru.mos.polls.aguser;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.model.flat.Flat;

public class AgUserUnitTest extends BaseUnitTest {

    public static final String BIRTHDAY_1996 = "1996";
    Context appContext = InstrumentationRegistry.getTargetContext();
    AgUser agUser1;
    AgUser agUser2;

    @Before
    public void init() {
        agUser1 = new AgUser(appContext, fromTestRawAsJson("aguser.json"));
        agUser2 = new AgUser(appContext, fromTestRawAsJson("aguser.json"));
    }

    @Test
    public void parseTest() {
        Assert.assertNotNull(agUser1);
        Flat registration = agUser1.getRegistration();
        Assert.assertNotNull(registration);
        Assert.assertEquals(registration.isRegistration(), true);

        Flat work = agUser1.getWork();
        Assert.assertNotNull(work);
        Assert.assertEquals(work.isWork(), true);
    }

    @Test
    public void agUserTest() {
        Assert.assertEquals(agUser1.isEmptyPersonal(), true);
        agUser1.setGender(AgUser.Gender.MALE);
        Assert.assertEquals(agUser1.isEmptyPersonal(), false);


        Assert.assertEquals(agUser1.equalsFamily(agUser2), true);
        Assert.assertEquals(agUser1.equalsPersonal(agUser2), true);
        Assert.assertEquals(agUser1.equalsWork(agUser2), true);

        Assert.assertEquals(agUser1.isEmptyFamily(), false);
        Assert.assertEquals(agUser1.isEmptyFlats(), false);
        Assert.assertEquals(agUser1.isEmptyWork(), true);

        Assert.assertEquals(agUser1.isEmailConfirmed(), false);
        agUser1.setEmailConfirmed(true);
        Assert.assertEquals(agUser1.isEmailConfirmed(), true);

        Assert.assertEquals(agUser1.isPguConnected(), false);
        agUser1.setPguConnected(true);
        Assert.assertEquals(agUser1.isPguConnected(), true);

        Assert.assertEquals(agUser1.getMaritalStatus(), AgUser.MaritalStatus.SINGLE);

        Assert.assertEquals(agUser1.getChildCount(), 3);
        Assert.assertEquals(agUser1.getFirstBirthday(), BIRTHDAY_1996);
        Assert.assertEquals(agUser1.hasOneChild(), false);
        Assert.assertEquals(agUser1.hasMoreThanOneChild(), true);

        agUser1.clearBirthdays();
        Assert.assertEquals(agUser1.getChildBirthdays().size(), 0);

        agUser1.add(BIRTHDAY_1996);
        agUser1.setChildCount(1);
        Assert.assertEquals(agUser1.hasOneChild(), true);
        Assert.assertEquals(agUser1.hasMoreThanOneChild(), false);
        agUser1.remove(BIRTHDAY_1996);
        Assert.assertEquals(agUser1.getChildBirthdays().size(), 0);

        Assert.assertEquals(agUser1.equalsFamily(agUser2), false);
        agUser2.setFirstName("test");
        Assert.assertEquals(agUser1.equalsPersonal(agUser2), false);
        agUser1.setAgSocialStatus(2);
        Assert.assertEquals(agUser1.isEmptyWork(), false);
        Assert.assertEquals(agUser1.equalsWork(agUser2), false);


        Assert.assertEquals(agUser1.isProfileVisible(), false);
        agUser1.setProfileVisible(true);
        Assert.assertEquals(agUser1.isProfileVisible(), true);

        assertNotNullOrEmpty(agUser1.getFullUserName());
        assertNotNullOrEmpty(agUser1.getSurnameAndFirstName());

//        Assert.assertEquals(agUser1.birthdayToLongFromView("2000"), 2000);

        Assert.assertEquals(agUser1.isCarExist(), false);
        agUser1.setCarExist(true);
        Assert.assertEquals(agUser1.isCarExist(), true);
    }
}
