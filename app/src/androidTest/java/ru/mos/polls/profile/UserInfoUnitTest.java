package ru.mos.polls.profile;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.UserInfo;

/**
 * Created by Trunks on 15.11.2017.
 */

public class UserInfoUnitTest extends BaseUnitTest {

    @Test
    public void gettersTest() {
        UserInfo userInfo = new UserInfo("test", "test");
        Assert.assertNotNull(userInfo.getValue());
        Assert.assertNotNull(userInfo.getTitle());
    }
}
