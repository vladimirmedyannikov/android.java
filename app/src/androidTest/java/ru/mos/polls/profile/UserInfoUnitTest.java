package ru.mos.polls.profile;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.profile.model.UserInfo;

public class UserInfoUnitTest extends BaseUnitTest {

    @Test
    public void gettersTest() {
        UserInfo userInfo = new UserInfo("test", "test");
        Assert.assertNotNull(userInfo.getValue());
        Assert.assertNotNull(userInfo.getTitle());
    }
}
