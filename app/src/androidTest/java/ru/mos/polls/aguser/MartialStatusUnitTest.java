package ru.mos.polls.aguser;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.BaseUnitTest;

/**
 * Created by Trunks on 22.08.2017.
 */

public class MartialStatusUnitTest extends BaseUnitTest {

    AgUser.MaritalStatus noselected = AgUser.MaritalStatus.NULL;
    AgUser.MaritalStatus married = AgUser.MaritalStatus.MARRIED;
    AgUser.MaritalStatus single = AgUser.MaritalStatus.SINGLE;

    @Test
    public void parseTest() {

        Assert.assertEquals(AgUser.MaritalStatus.parse(noselected.getValue()), AgUser.MaritalStatus.NULL);

        Assert.assertEquals(AgUser.MaritalStatus.parse(married.getValue()), AgUser.MaritalStatus.MARRIED);

        Assert.assertEquals(AgUser.MaritalStatus.parse(single.getValue()), AgUser.MaritalStatus.SINGLE);
    }

    @Test
    public void genderTest() {
        Assert.assertEquals(married.toString(), AgUser.MaritalStatus.MARRIED.toString());

        married.setGender(AgUser.Gender.FEMALE);
        Assert.assertEquals(married.getLabelFemale(), AgUser.MaritalStatus.MARRIED.toString());

        married.setGender(AgUser.Gender.MALE);
        Assert.assertEquals(married.getLabelMale(), AgUser.MaritalStatus.MARRIED.toString());

        Assert.assertEquals(single.toString(), AgUser.MaritalStatus.SINGLE.toString());

        single.setGender(AgUser.Gender.FEMALE);
        Assert.assertEquals(single.getLabelFemale(), AgUser.MaritalStatus.SINGLE.toString());

        single.setGender(AgUser.Gender.MALE);
        Assert.assertEquals(single.getLabelMale(), AgUser.MaritalStatus.SINGLE.toString());
    }

    @Test
    public void equalTest() {
        AgUser.MaritalStatus single1 = AgUser.MaritalStatus.SINGLE;
        Assert.assertTrue(single.equals(single1));
        Assert.assertFalse(married.equals(noselected));
    }

}
