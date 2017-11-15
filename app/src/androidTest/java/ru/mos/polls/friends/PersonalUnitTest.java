package ru.mos.polls.friends;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.friend.model.Personal;

/**
 * Created by Trunks on 15.11.2017.
 */

public class PersonalUnitTest extends BaseUnitTest {

    @Test
    public void gettersTest() {
        Personal personal = mockObj("personal.json", Personal.class);
        Assert.assertNotNull(personal.getAvatar());
        Assert.assertNotNull(personal.getPhone());
        Assert.assertNotNull(personal.getFirstName());
        Assert.assertNotNull(personal.getRegistrationDate());
        Assert.assertNotNull(personal.getSurname());
    }
}
