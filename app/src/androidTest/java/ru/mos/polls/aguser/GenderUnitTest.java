package ru.mos.polls.aguser;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.BaseUnitTest;

/**
 * Created by Trunks on 17.08.2017.
 */

public class GenderUnitTest extends BaseUnitTest {
    AgUser.Gender female = AgUser.Gender.FEMALE;
    AgUser.Gender male = AgUser.Gender.MALE;
    AgUser.Gender nullValue = AgUser.Gender.NULL;

    @Test
    public void testParse() {
        Assert.assertEquals(AgUser.Gender.parse(female.getValue()), female);

        Assert.assertEquals(AgUser.Gender.parse(male.getValue()), male);

        Assert.assertEquals(AgUser.Gender.parse(nullValue.getValue()), nullValue);
    }

    @Test
    public void testGetters() {
        AgUser.Gender female = AgUser.Gender.FEMALE;
        assertNotNullOrEmpty(female.getValue());
        assertNotNullOrEmpty(female.name());
    }

    @Test
    public void testEquals() {
        Assert.assertFalse(female.equals(male));

        Assert.assertTrue(male.equals(male));
    }
}
