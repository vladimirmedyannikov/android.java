package ru.mos.polls.social;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.R;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.Social;

/**
 * Created by Trunks on 13.03.2017.
 */

public class SocialUnitTest extends BaseUnitTest {
    Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void jsonObject() {
        Social test = new Social("test", fromTestRawAsJson("social.json"));
        Assert.assertNotNull(test.tokenDataAsJson());
        Assert.assertNotNull(test.asKillJson());
        Assert.assertNotNull(test.asNull());
        Assert.assertEquals(test.isEmpty(), false);
    }

    @Test
    public void othertest() {
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

    @Test
    public void getList() {
        List<Social> list = Social.getSavedSocials(appContext);
        Assert.assertNotNull(list);
        Assert.assertEquals(4, list.size());

        Social fbs = Social.findFbSocial(list);
        Assert.assertEquals(fbs.getSocialId(), SocialManager.SOCIAL_ID_FB);


        Social vk = Social.findVkSocial(list);
        Assert.assertEquals(vk.getSocialId(), SocialManager.SOCIAL_ID_VK);

        Social ok = Social.findOkSocial(list);
        Assert.assertEquals(ok.getSocialId(), SocialManager.SOCIAL_ID_OK);

        Social tw = Social.findTwSocial(list);
        Assert.assertEquals(tw.getSocialId(), SocialManager.SOCIAL_ID_TW);

        Social tw2 = Social.findSocial(list, SocialManager.SOCIAL_ID_TW);
        Assert.assertEquals(tw2.getSocialId(), SocialManager.SOCIAL_ID_TW);

        Assert.assertEquals(true, Social.isEquals(list, list));
    }

    @Test
    public void fromPref() {
        Social test = Social.fromPreference(appContext, SocialManager.SOCIAL_ID_FB);
        Assert.assertNotNull(test);

    }

    @Test
    public void getSocialIconId() {
        int vkIcon = Social.getSocialIcon(SocialManager.SOCIAL_ID_VK);
        Assert.assertEquals(R.drawable.vk, vkIcon);

        int fbIcon = Social.getSocialIcon(SocialManager.SOCIAL_ID_FB);
        Assert.assertEquals(R.drawable.fb, fbIcon);

        int twIcon = Social.getSocialIcon(SocialManager.SOCIAL_ID_TW);
        Assert.assertEquals(R.drawable.tw, twIcon);

        int okIcon = Social.getSocialIcon(SocialManager.SOCIAL_ID_OK);
        Assert.assertEquals(R.drawable.odnklsnk, okIcon);

        int gpIcon = Social.getSocialIcon(SocialManager.SOCIAL_ID_GP);
        Assert.assertEquals(R.drawable.google, gpIcon);

        int defaultRed = Social.getSocialIcon(0);
        Assert.assertEquals(-1, defaultRed);
    }
}
