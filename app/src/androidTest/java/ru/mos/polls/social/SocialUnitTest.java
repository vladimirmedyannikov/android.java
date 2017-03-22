package ru.mos.polls.social;

import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.social.model.Social;

/**
 * Created by Trunks on 13.03.2017.
 */

public class SocialUnitTest extends BaseUnitTest {

    @Test
    public void jsonObject() {
        Social test = new Social("test", fromTestRawAsJson("social.json"));
        Assert.assertNotNull(test.tokenDataAsJson());
        Assert.assertNotNull(test.asKillJson());
        Assert.assertNotNull(test.asNull());
    }

    @Test
    public void othertest(){
        Social test = new Social("test", fromTestRawAsJson("social.json"));
        Social test2 = new Social();
        test2.copy(test);
        Assert.assertEquals(test2.getExpired(), test.getExpired());
        Assert.assertEquals(test2.getIcon(), test.getIcon());
        Assert.assertEquals(test2.getSocialId(), test.getSocialId());
        Assert.assertEquals(test2.getTokenData(), test.getTokenData());
        Assert.assertEquals(test2.isLogon(), test.isLogon());
        Assert.assertEquals(true, test.equals(test2));

        test.setIsLogin(false);
        Assert.assertEquals(false, test.isLogon());
        test.setIcon("test");
        Assert.assertEquals("test", test.getIcon());
        Assert.assertEquals(-1, test.getSocialId());
    }

}
